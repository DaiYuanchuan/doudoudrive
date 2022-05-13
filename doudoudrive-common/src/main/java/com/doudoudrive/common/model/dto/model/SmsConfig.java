package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>短信发送时的基本配置</p>
 * <p>2022-04-27 17:34</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问者身份
     * 阿里云是accessKeyId
     * 腾讯云是secretId
     */
    private String appId;

    /**
     * 加密签名字符串和服务器端验证签名字符串的密钥
     * 阿里云是accessKeySecret
     * 腾讯云是secretKey
     */
    private String appKey;

    /**
     * 腾讯云应用专属，短信应用的唯一标识
     */
    private String sdkAppId;

    /**
     * 发送短信时用的域名，统一使用https协议，配置时不用带https请求头
     */
    private String domain;

    /**
     * 短信发送时使用的签名
     */
    private String signName;

    /**
     * 发送短信时使用的应用类型(阿里云、腾讯云)
     */
    private String appType;

}
