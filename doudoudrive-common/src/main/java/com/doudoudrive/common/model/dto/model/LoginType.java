package com.doudoudrive.common.model.dto.model;

import lombok.Getter;

/**
 * <p>登录类型</p>
 * <p>2020-04-23 23:15</p>
 *
 * @author Dan
 **/
@Getter
public enum LoginType {

    /**
     * 密码登录
     */
    PASSWORD("password"),

    /**
     * 免密登录
     */
    NO_PASSWORD("noPassword");

    /**
     * 状态值
     */
    private final String code;

    LoginType(String code) {
        this.code = code;
    }

}