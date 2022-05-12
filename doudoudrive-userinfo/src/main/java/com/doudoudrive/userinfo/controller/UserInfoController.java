package com.doudoudrive.userinfo.controller;

import cn.hutool.core.util.ReUtil;
import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.RegexConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.VerifyCodeRequestDTO;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.userinfo.client.SmsFeignClient;
import com.doudoudrive.userinfo.manager.UserInfoManager;
import com.doudoudrive.userinfo.model.dto.request.ResetPasswordRequestDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>用户信息服务控制层实现</p>
 * <p>2022-03-21 18:07</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/userinfo")
public class UserInfoController {

    /**
     * 用户信息搜索服务
     */
    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    private UserInfoManager userInfoManager;

    private SmsFeignClient smsFeignClient;

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired
    public void setUserInfoManager(UserInfoManager userInfoManager) {
        this.userInfoManager = userInfoManager;
    }

    @Autowired
    public void setSmsFeignClient(SmsFeignClient smsFeignClient) {
        this.smsFeignClient = smsFeignClient;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "用户注册", businessType = "用户信息配置中心")
    @PostMapping(value = "/register", produces = "application/json;charset=UTF-8")
    public Result<String> register(@RequestBody @Valid SaveUserInfoRequestDTO requestDTO,
                                   HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 判断用户账号类型，同时校验对应的验证码
        Result<String> verifyCode = verifyCode(requestDTO.getUserEmail(), requestDTO.getCode());
        if (Result.isNotSuccess(verifyCode)) {
            return verifyCode;
        }

        // 查询用户关键信息是否存在
        Result<String> userInfoKeyExistsSearchResult = userInfoSearchFeignClient.userInfoKeyExistsSearch(requestDTO.getUserName(),
                requestDTO.getUserEmail(), null);
        if (Result.isNotSuccess(userInfoKeyExistsSearchResult)) {
            return userInfoKeyExistsSearchResult;
        }

        // 保存用户信息
        userInfoManager.insert(requestDTO);
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "重置用户密码", businessType = "用户信息配置中心")
    @PostMapping(value = "/reset-password", produces = "application/json;charset=UTF-8")
    public Result<String> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO requestDTO,
                                        HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 判断用户账号类型，同时校验对应的验证码
        Result<String> verifyCode = verifyCode(requestDTO.getUsername(), requestDTO.getCode());
        if (Result.isNotSuccess(verifyCode)) {
            return verifyCode;
        }

        // 获取当前用户信息
        Result<UsernameSearchResponseDTO> usernameSearchResult = userInfoSearchFeignClient.usernameSearch(requestDTO.getUsername());
        if (Result.isNotSuccess(usernameSearchResult)) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.USER_NO_EXIST);
        }

        // 修改用户信息
        userInfoManager.resetPassword(usernameSearchResult.getData().getBusinessId(), requestDTO.getPassword());

        return Result.ok();
    }

    /**
     * 判断用户账号类型，获取对应的验证码校验结果
     *
     * @param username 收件人信息
     * @param code     验证码信息
     * @return 获取对应的验证码校验结果
     */
    private Result<String> verifyCode(String username, String code) {
        // 构建校验验证码请求对象
        VerifyCodeRequestDTO verifyCodeRequest = VerifyCodeRequestDTO.builder()
                .smsRecipient(username)
                .code(code)
                .build();

        // 判断是否为邮件类型
        boolean isMail = ReUtil.isMatch(RegexConstant.EMAIL, username);
        // 发起验证码校验请求
        if (isMail) {
            return smsFeignClient.mailVerifyCode(verifyCodeRequest);
        }

        // 判断是否为sms类型
        boolean isSms = ReUtil.isMatch(RegexConstant.MOBILE, username);
        if (isSms) {
            return smsFeignClient.smsVerifyCode(verifyCodeRequest);
        }

        return Result.build(StatusCodeEnum.ACCOUNT_TYPE_EXCEPTION);
    }
}
