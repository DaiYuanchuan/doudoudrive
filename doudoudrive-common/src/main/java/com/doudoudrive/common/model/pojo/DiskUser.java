package com.doudoudrive.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>用户模块实体类</p>
 * <p>2020-10-12 22:54:40</p>
 *
 * @author Dan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiskUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 2960485242968403366L;

    /**
     * 自增长标识
     */
    private Long autoId;

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
     * 用户密码
     */
    private String userPwd;

    /**
     * 用于登录密码校验的盐值
     */
    private String userSalt;

    /**
     * 原始用户名密码的MD5值，取值为:MD5({username}#{pwd})，用于回调、鉴权时的加密用
     */
    private String secretKey;

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
    private Date userUnlockTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}