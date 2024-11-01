package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>用户机密数据信息模型</p>
 * <p>2022-05-13 15:47</p>
 *
 * @author Dan
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConfidentialInfo implements Serializable {

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
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户手机号
     */
    private String userTel;

    /**
     * 用户密文密码
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

}
