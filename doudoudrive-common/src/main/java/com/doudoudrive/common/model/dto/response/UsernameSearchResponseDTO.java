package com.doudoudrive.common.model.dto.response;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>通过用户名查询用户信息请求的响应数据模型</p>
 * <p>2022-03-21 14:46</p>
 *
 * @author Dan
 **/
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UsernameSearchResponseDTO extends DiskUserModel {

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
