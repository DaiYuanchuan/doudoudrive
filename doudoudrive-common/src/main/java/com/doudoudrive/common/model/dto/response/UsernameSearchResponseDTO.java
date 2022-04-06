package com.doudoudrive.common.model.dto.response;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>通过用户名查询用户信息请求的响应数据模型</p>
 * <p>2022-03-21 14:46</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsernameSearchResponseDTO {

    /**
     * 用户系统内唯一标识
     */
    private String businessId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户手机号
     */
    private String userTel;

    /**
     * 当前账号是否可用(0:false,1:true)
     */
    private Boolean available;

    /**
     * 当前账号不可用原因
     */
    private String userReason;

    /**
     * 账号被封禁的时间(单位:秒)(-1:永久)最大2144448000
     */
    private Integer userBanTime;

    /**
     * 账号解封时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date userUnlockTime;

    /**
     * 用户密码
     */
    private String userPwd;

    /**
     * 用于登录密码校验的盐值
     */
    private String userSalt;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date updateTime;

    /**
     * 表后缀
     */
    private String tableSuffix;

}
