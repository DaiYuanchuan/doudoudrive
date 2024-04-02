package com.doudoudrive.common.model.dto.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>shiro服务鉴权对象模型，此对象序列化存储于Redis缓存中，用于服务之间通信</p>
 * <p>2022-04-19 12:26</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiroAuthenticationModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前请求的会话id
     */
    private Serializable sessionId;

    /**
     * 当前登录的用户id
     */
    private String userId;

    /**
     * 当前登录的用户名
     */
    private String username;

    /**
     * 当前session的序列化字节码
     */
    private byte[] session;
}
