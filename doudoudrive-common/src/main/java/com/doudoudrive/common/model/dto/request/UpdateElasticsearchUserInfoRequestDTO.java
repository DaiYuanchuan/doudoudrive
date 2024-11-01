package com.doudoudrive.common.model.dto.request;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * <p>更新es用户信息时的请求数据模型</p>
 * <p>2022-03-21 14:15</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateElasticsearchUserInfoRequestDTO {

    /**
     * 用户系统内唯一标识
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(min = 1, max = 35, message = "业务标识长度错误")
    private String businessId;

    /**
     * 用户头像
     */
    @Size(max = 170, message = "用户头像链接长度错误")
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @Size(max = 50, message = "邮箱长度错误")
    private String userEmail;

    /**
     * 用户手机号
     */
    @Size(max = 15, message = "手机号长度错误")
    private String userTel;

    /**
     * 用户密码
     */
    @Size(max = 150, message = "密码长度错误")
    private String userPwd;

    /**
     * 用于登录密码校验的盐值
     */
    @Size(max = 50, message = "密码的盐值长度错误")
    private String userSalt;

    /**
     * 原始用户名密码的MD5值，取值为:MD5({username}#{pwd})，用于回调、鉴权时的加密用
     */
    @Size(max = 50, message = "密钥信息长度错误")
    private String secretKey;

    /**
     * 当前账号是否可用(0:false,1:true)
     */
    private Boolean available;

    /**
     * 当前账号不可用原因
     */
    @Size(max = 150, message = "账号不可用原因长度错误")
    private String userReason;

    /**
     * 账号被封禁的时间(单位:秒)(-1:永久)最大2144448000
     */
    @Min(value = -1, message = "账号被封禁的时间超出最小值")
    @Max(value = 2144448000, message = "账号被封禁的时间超出最大值")
    private Integer userBanTime;

    /**
     * 账号解封时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date userUnlockTime;
}
