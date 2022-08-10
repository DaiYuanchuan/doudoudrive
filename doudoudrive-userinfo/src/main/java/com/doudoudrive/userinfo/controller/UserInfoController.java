package com.doudoudrive.userinfo.controller;

import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.ReUtil;
import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.util.EncryptionUtil;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.RegexConstant;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.UserConfidentialInfo;
import com.doudoudrive.common.model.dto.request.SaveUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.VerifyCodeRequestDTO;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.userinfo.client.SmsFeignClient;
import com.doudoudrive.userinfo.manager.UserInfoManager;
import com.doudoudrive.userinfo.model.dto.request.ResetPasswordRequestDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class UserInfoController {

    /**
     * 用户信息搜索服务
     */
    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    private UserInfoManager userInfoManager;

    private SmsFeignClient smsFeignClient;

    private LoginManager loginManager;

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

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "用户注册", businessType = "用户信息配置中心")
    @PostMapping(value = "/userinfo/register", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> register(@RequestBody @Valid SaveUserInfoRequestDTO requestDTO,
                                   HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 手机号不为空
        if (StringUtils.isNotBlank(requestDTO.getUserTel())) {
            // 校验手机号格式是否正确
            if (!PhoneUtil.isMobile(requestDTO.getUserTel())) {
                return Result.build(StatusCodeEnum.PHONE_NUMBER_FORMAT_ERROR);
            }
            // 校验手机号码对应的验证码是否正确
            Result<String> verifyCode = smsFeignClient.smsVerifyCode(VerifyCodeRequestDTO.builder()
                    .smsRecipient(requestDTO.getUserTel())
                    .code(requestDTO.getTelCode())
                    .build());
            if (Result.isNotSuccess(verifyCode)) {
                return verifyCode;
            }
        }

        // 校验邮箱验证码
        Result<String> verifyCode = smsFeignClient.mailVerifyCode(VerifyCodeRequestDTO.builder()
                .smsRecipient(requestDTO.getUserEmail())
                .code(requestDTO.getMailCode())
                .build());
        if (Result.isNotSuccess(verifyCode)) {
            return verifyCode;
        }

        // 查询用户关键信息是否存在
        Result<String> userInfoKeyExistsSearchResult = userInfoSearchFeignClient.userInfoKeyExistsSearch(requestDTO.getUserName(),
                requestDTO.getUserEmail(), requestDTO.getUserTel());
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
    @PostMapping(value = "/userinfo/reset-password", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO requestDTO,
                                        HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 判断用户账号类型，同时校验对应的验证码
        Result<String> verifyCode = this.verifyCode(requestDTO.getUsername(), requestDTO.getCode());
        if (Result.isNotSuccess(verifyCode)) {
            return verifyCode;
        }

        // 获取当前用户信息
        Result<UsernameSearchResponseDTO> usernameSearchResult = userInfoSearchFeignClient.usernameSearch(requestDTO.getUsername());
        if (Result.isNotSuccess(usernameSearchResult)) {
            return Result.build(StatusCodeEnum.USER_NO_EXIST);
        }

        // 修改用户信息
        userInfoManager.resetPassword(usernameSearchResult.getData().getBusinessId(), requestDTO.getPassword());

        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "修改用户信息", businessType = "用户信息配置中心")
    @PostMapping(value = "/userinfo/update", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> update(@RequestBody @Valid UpdateUserInfoRequestDTO requestDTO,
                                 HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 从缓存中获取当前登录的用户信息
        UserConfidentialInfo userinfo = loginManager.getUserConfidentialToSessionException();

        // 构建最终需要修改的用户数据实体
        DiskUser find = new DiskUser();
        find.setBusinessId(userinfo.getBusinessId());

        // 是否需要执行更新操作的标识符
        boolean isPerform = Boolean.FALSE;

        // 用户头像不为空时，对用户头像进行正则校验
        if (StringUtils.isNotBlank(requestDTO.getUserAvatar())) {
            if (!ReUtil.isMatch(RegexConstant.URL_HTTP, requestDTO.getUserAvatar())) {
                return Result.build(StatusCodeEnum.URL_FORMAT_ERROR);
            }
            find.setUserAvatar(requestDTO.getUserAvatar());
            isPerform = Boolean.TRUE;
        }

        // 用户邮箱不为空时，对用户邮箱进行正则校验
        if (StringUtils.isNotBlank(requestDTO.getUserEmail())) {
            if (!ReUtil.isMatch(RegexConstant.EMAIL, requestDTO.getUserEmail())) {
                return Result.build(StatusCodeEnum.EMAIL_FORMAT_ERROR);
            }
            // 校验邮箱验证码
            Result<String> verifyCode = smsFeignClient.mailVerifyCode(VerifyCodeRequestDTO.builder()
                    .smsRecipient(requestDTO.getUserEmail())
                    .code(requestDTO.getMailCode())
                    .build());
            if (Result.isNotSuccess(verifyCode)) {
                return verifyCode;
            }
            find.setUserEmail(requestDTO.getUserEmail());
            isPerform = Boolean.TRUE;
        }

        // 用户手机号不为空时，对用户手机号进行正则校验
        if (StringUtils.isNotBlank(requestDTO.getUserTel())) {
            if (!ReUtil.isMatch(RegexConstant.MOBILE, requestDTO.getUserTel())) {
                return Result.build(StatusCodeEnum.PHONE_NUMBER_FORMAT_ERROR);
            }
            // 校验手机号码对应的验证码是否正确
            Result<String> verifyCode = smsFeignClient.smsVerifyCode(VerifyCodeRequestDTO.builder()
                    .smsRecipient(requestDTO.getUserTel())
                    .code(requestDTO.getSmsCode())
                    .build());
            if (Result.isNotSuccess(verifyCode)) {
                return verifyCode;
            }
            find.setUserTel(requestDTO.getUserTel());
            isPerform = Boolean.TRUE;
        }

        // 用户明文密码不为空时，对用户原密码进行校验
        if (StringUtils.isNotBlank(requestDTO.getPassword())) {
            if (StringUtils.isBlank(requestDTO.getOldPassword())) {
                return Result.build(StatusCodeEnum.ORIGINAL_PASSWORD_ERROR);
            }
            // 对原始密码进行加盐加密,得到加密后的密码
            String sourcePassword = EncryptionUtil.digestEncodedPassword(requestDTO.getOldPassword(), userinfo.getUserSalt());
            // 原始密码不相等时抛出异常响应
            if (!userinfo.getUserPwd().equals(sourcePassword)) {
                return Result.build(StatusCodeEnum.ORIGINAL_PASSWORD_ERROR);
            }
            find.setUserPwd(requestDTO.getPassword());
            isPerform = Boolean.TRUE;
        }

        if (isPerform) {
            // 避免进行无效的更新操作
            userInfoManager.updateBasicsInfo(find);
        }
        return Result.ok();
    }

    @SneakyThrows
    @GetMapping(value = "/userinfo", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<UserLoginResponseDTO> getUserinfoToSession(HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);
        // 从session中获取当前登录的用户信息
        return Result.ok(loginManager.getUserInfoToSession());
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
