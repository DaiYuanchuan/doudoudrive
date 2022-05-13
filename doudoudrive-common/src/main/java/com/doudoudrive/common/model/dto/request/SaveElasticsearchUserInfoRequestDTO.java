package com.doudoudrive.common.model.dto.request;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.RegexConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * <p>保存es用户信息时的请求数据模型</p>
 * <p>2022-03-21 10:27</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveElasticsearchUserInfoRequestDTO {

    /**
     * 自增长标识
     */
    @NotNull(message = "自增长标识不能为空")
    @Min(value = 0, message = "自增长标识不能为负")
    @Max(value = 9223372036854775807L, message = "自增长标识超出最大值")
    private Long autoId;

    /**
     * 用户系统内唯一标识
     */
    @NotBlank(message = "业务标识不能为空")
    @Size(min = 1, max = 35, message = "业务标识长度错误")
    private String businessId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 1, max = 50, message = "用户名称长度错误")
    private String userName;

    /**
     * 用户头像
     */
    @NotBlank(message = "用户头像不能为空")
    @Size(min = 1, max = 170, message = "url长度错误")
    @Pattern(regexp = RegexConstant.URL_HTTP, message = "url格式错误")
    private String userAvatar;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "用户邮箱不能为空")
    @Size(min = 1, max = 50, message = "邮箱长度错误")
    @Pattern(regexp = RegexConstant.EMAIL, message = "邮箱格式错误")
    private String userEmail;

    /**
     * 用户手机号
     */
    @Size(max = 15, message = "手机号长度错误")
    private String userTel;

    /**
     * 用户密码
     */
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 1, max = 150, message = "密码长度错误")
    private String userPwd;

    /**
     * 用于登录密码校验的盐值
     */
    @NotBlank(message = "密码的盐值不能为空")
    @Size(min = 1, max = 50, message = "密码的盐值长度错误")
    private String userSalt;

    /**
     * 当前账号是否可用(0:false,1:true)
     */
    @NotNull(message = "账号可用标识不能为空")
    private Boolean available;

    /**
     * 当前账号不可用原因
     */
    @Size(max = 150, message = "账号不可用原因长度错误")
    private String userReason;

    /**
     * 账号被封禁的时间(单位:秒)(-1:永久)最大2144448000
     */
    @NotNull(message = "账号被封禁的时间不能为空")
    @Min(value = -1, message = "账号被封禁的时间超出最小值")
    @Max(value = 2144448000, message = "账号被封禁的时间超出最大值")
    private Integer userBanTime;

    /**
     * 账号解封时间
     */
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DatePattern.NORM_DATETIME_PATTERN, timezone = ConstantConfig.TimeZone.DEFAULT_TIME_ZONE)
    private Date userUnlockTime;

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
    @NotBlank(message = "表后缀不能为空")
    @Size(min = 1, max = 5, message = "表后缀长度错误")
    private String tableSuffix;

}
