package com.doudoudrive.sms.controller;

import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.sms.client.UserInfoSearchFeignClient;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.model.dto.request.MailVerificationCodeRequestDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>邮件信息发送服务控制层实现</p>
 * <p>2022-04-25 17:58</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/send/mail")
public class MailController {

    /**
     * 用户信息搜索服务
     */
    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    private SmsManager smsManager;

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired
    public void setSmsManager(SmsManager smsManager) {
        this.smsManager = smsManager;
    }

    @SneakyThrows
    @OpLog(title = "邮箱验证码", businessType = "发送")
    @PostMapping(value = "/verification-code", produces = "application/json;charset=UTF-8")
    public Result<String> verificationCode(@RequestBody @Valid MailVerificationCodeRequestDTO requestDTO,
                                           HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        if (requestDTO.getExist()) {
            // 根据用户邮箱查询用户信息
            Result<String> userInfoSearch = userInfoSearchFeignClient.userInfoKeyExistsSearch(null, requestDTO.getEmail(), null);
            if (Result.isNotSuccess(userInfoSearch)) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.USER_EMAIL_NOT_EXIST);
            }
        }

        // 邮箱验证码信息发送
        smsManager.mailVerificationCode(requestDTO.getEmail(), requestDTO.getUsername());
        return Result.ok();
    }
}
