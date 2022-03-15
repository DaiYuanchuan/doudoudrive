package com.doudoudrive.common.constant;

/**
 * <p>序列模块枚举信息</p>
 * <p>2022-03-06 22:02</p>
 *
 * @author Dan
 **/
public enum SequenceModuleEnum {

    /**
     * 用户模块
     */
    DISK_USER("01"),

    /**
     * 登录日志模块
     */
    LOG_LOGIN("02"),

    /**
     * API操作日志模块
     */
    LOG_OP("03");

    public final String code;

    SequenceModuleEnum(String code) {
        this.code = code;
    }
}
