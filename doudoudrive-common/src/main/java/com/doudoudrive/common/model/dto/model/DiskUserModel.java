package com.doudoudrive.common.model.dto.model;

import cn.hutool.core.date.DatePattern;
import com.doudoudrive.common.constant.ConstantConfig;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>通用的用户信息数据模型</p>
 * <p>2022-04-05 18:13</p>
 *
 * @author Dan
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiskUserModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 文件访问密钥，拼接在文件链接后面，用于文件访问鉴权
     */
    private String fileAccessKey;

    /**
     * 当前用户所属的所有角色信息
     */
    private List<SysUserRoleModel> roleInfo;

    /**
     * 用户属性内容
     */
    private Map<String, String> userAttr;

}
