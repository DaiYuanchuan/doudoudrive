package com.doudoudrive.auth.controller;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.model.dto.MockToken;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.convert.DiskUserInfoConvert;
import com.doudoudrive.common.model.dto.model.Region;
import com.doudoudrive.common.model.dto.model.UserSimpleModel;
import com.doudoudrive.common.model.dto.request.UserLoginRequestDTO;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.pojo.LogLogin;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.ip.IpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * <p>登陆模块控制层</p>
 * <p>2022-04-04 21:35</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/oauth")
public class LoginController {

    private LoginManager loginManager;

    private DiskUserInfoConvert diskUserInfoConvert;

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Autowired(required = false)
    public void setDiskUserInfoConvert(DiskUserInfoConvert diskUserInfoConvert) {
        this.diskUserInfoConvert = diskUserInfoConvert;
    }

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @SneakyThrows
    @OpLog(title = "密码登录", businessType = "用户登录")
    @PostMapping(value = "/login", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<?> login(@RequestBody @Valid UserLoginRequestDTO requestDTO,
                           HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        if (SecurityUtils.getSubject().isAuthenticated()) {
            log.info("重复调用登陆模块");
            // 获取当前登录的用户信息
            UserLoginResponseDTO userLoginInfo = loginManager.getUserInfoToSession();
            if (userLoginInfo == null) {
                return Result.refuse();
            }
            return Result.ok(userLoginInfo);
        }

        // 构建系统登录日志
        LogLogin logLogin = builderLogLogin(request, requestDTO.getUsername());

        // 异步获取IP实际地理位置信息
        Future<Region> future = ThreadUtil.execAsync(() -> IpUtils.getIpLocationByBtree(logLogin.getIp()));

        try {
            // 开始登录流程
            SecurityUtils.getSubject().login(new MockToken(requestDTO.getUsername(), requestDTO.getPassword()));
        } catch (DisabledAccountException e) {
            // 被封禁的账户实体
            UserSimpleModel userInfo = JSONObject.parseObject(e.getMessage(), UserSimpleModel.class);
            // 构建响应体
            UserLoginResponseDTO responseInfo = UserLoginResponseDTO.builder()
                    .userInfo(diskUserInfoConvert.userSimpleModelConvert(userInfo))
                    .build();
            // 如果封禁的时间大于0 则格式化被封禁的时间
            if (userInfo.getUserBanTime() > NumberConstant.INTEGER_ZERO) {
                // 获取封禁开始的时间
                Date beginDate = DateUtil.offsetSecond(userInfo.getUserUnlockTime(), userInfo.getUserBanTime() * -1);
                responseInfo.setUserBanTimeFormat(DateUtil.formatBetween(beginDate, userInfo.getUserUnlockTime(), BetweenFormatter.Level.SECOND));
            }
            saveLoginFail(StatusCodeEnum.ACCOUNT_FORBIDDEN.message, logLogin, future);
            return Result.build(StatusCodeEnum.ACCOUNT_FORBIDDEN).data(responseInfo);
        } catch (UnknownAccountException e) {
            saveLoginFail(StatusCodeEnum.USER_NO_EXIST.message, logLogin, future);
            return Result.build(StatusCodeEnum.USER_NO_EXIST);
        } catch (IncorrectCredentialsException e) {
            saveLoginFail(StatusCodeEnum.ACCOUNT_NO_EXIST.message, logLogin, future);
            return Result.build(StatusCodeEnum.ACCOUNT_NO_EXIST);
        } catch (ExpiredCredentialsException e) {
            saveLoginFail(StatusCodeEnum.EXPIRED_CREDENTIALS.message, logLogin, future);
            return Result.build(StatusCodeEnum.EXPIRED_CREDENTIALS);
        } catch (AuthenticationException e) {
            // 身份验证失败
            saveLoginFail(StatusCodeEnum.AUTHENTICATION.message, logLogin, future);
            return Result.build(StatusCodeEnum.AUTHENTICATION);
        }

        // 获取当前登录的用户信息
        UserLoginResponseDTO userLoginInfo = loginManager.getUserInfoToSession();
        if (userLoginInfo == null) {
            // 无法获取用户信息
            saveLoginFail(StatusCodeEnum.INVALID_USERINFO.message, logLogin, future);
            return Result.build(StatusCodeEnum.INVALID_USERINFO);
        }

        log.info("用户:{}登陆成功", requestDTO.getUsername());
        logLogin.setSuccess(true);

        // 向登录日志中添加新的用户信息
        logLogin.setUsername(userLoginInfo.getUserInfo().getUserName());
        logLogin.setSessionId(userLoginInfo.getToken());

        // 使用one-way模式发送消息，发送端发送完消息后会立即返回
        String destination = ConstantConfig.Topic.LOG_RECORD + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.LOGIN_LOG_RECORD;
        rocketmqTemplate.sendOneWay(destination, ObjectUtil.serialize(logLogin));

        return Result.ok(userLoginInfo);
    }

    @SneakyThrows
    @GetMapping(value = "/logout", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);
        SecurityUtils.getSubject().logout();
        return Result.ok();
    }

    /**
     * 构建系统登录日志
     *
     * @param request  请求体对象
     * @param username 当前登录的用户名
     * @return 登录日志实体
     */
    private LogLogin builderLogLogin(HttpServletRequest request, String username) {
        // 构建系统登录日志
        LogLogin logLogin = LogLogin.builder()
                .ip(ServletUtil.getClientIP(request))
                .username(username)
                .location("0-0-内网IP 内网IP")
                .build();

        // 判断IP地址
        if (logLogin.getIp().equals(ConstantConfig.HttpRequest.IPV6_LOCAL_IP)) {
            logLogin.setIp(ConstantConfig.HttpRequest.IPV4_LOCAL_IP);
        }

        // 获取浏览器信息
        UserAgent ua = UserAgentUtil.parse(request.getHeader(ConstantConfig.HttpRequest.USER_AGENT));
        logLogin.setUserAgent(request.getHeader(ConstantConfig.HttpRequest.USER_AGENT));
        if (ua != null) {
            // 获取/赋值 浏览器、os系统等信息
            logLogin.setBrowser(ua.getBrowser().toString());
            logLogin.setBrowserVersion(ua.getVersion());
            logLogin.setBrowserEngine(ua.getEngine().toString());
            logLogin.setBrowserEngineVersion(ua.getEngineVersion());
            logLogin.setMobile(ua.isMobile());
            logLogin.setOs(ua.getOs().toString());
            logLogin.setPlatform(ua.getPlatform().getName());
        }
        return logLogin;
    }

    /**
     * 保存登陆失败的记录
     *
     * @param msg      提示的消息
     * @param logLogin 登录日志实体信息
     * @param future   异步获取到的IP地址实际地理信息
     */
    private void saveLoginFail(String msg, LogLogin logLogin, Future<Region> future) {
        logLogin.setSuccess(false);
        logLogin.setMsg(msg);
        setLocation(logLogin, future);
        // 使用one-way模式发送消息，发送端发送完消息后会立即返回
        String destination = ConstantConfig.Topic.LOG_RECORD + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.LOGIN_LOG_RECORD;
        rocketmqTemplate.sendOneWay(destination, ObjectUtil.serialize(logLogin));
    }

    /**
     * 将异步获取到的IP地址实际地理位置信息赋值到系统登录日志实体中
     *
     * @param logLogin 登录日志实体信息
     * @param future   异步获取到的IP地址实际地理信息
     */
    private void setLocation(LogLogin logLogin, Future<Region> future) {
        try {
            Region region = future.get();
            logLogin.setLocation(region.getCountry() + ConstantConfig.SpecialSymbols.HYPHEN
                    + region.getProvince() + ConstantConfig.SpecialSymbols.HYPHEN + region.getCity() + StringUtils.SPACE
                    + region.getIsp());
        } catch (Exception e1) {
            logLogin.setSuccess(false);
            logLogin.setMsg(e1.getCause().getMessage());
        }
    }
}
