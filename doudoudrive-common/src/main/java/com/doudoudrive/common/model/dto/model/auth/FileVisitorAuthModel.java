package com.doudoudrive.common.model.dto.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>文件访问者鉴权模型</p>
 * <p>2024-04-01 21:27</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileVisitorAuthModel {

    /**
     * 访问者用户id
     */
    private String userId;

    /**
     * 访问者登录时的用户名
     */
    private String username;

    /**
     * 原始用户名密码的MD5值，取值为:MD5({username}#{pwd})，用于回调、鉴权时的加密用
     */
    private String secretKey;

}
