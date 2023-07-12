package com.doudoudrive.common.constant;

import lombok.Getter;

/**
 * <p>redis分布式锁名称通用枚举类</p>
 * <p>2023-02-15 18:31</p>
 *
 * @author Dan
 **/
@Getter
public enum RedisLockEnum {

    /**
     * 用户注册锁，防止短时间内用户重复注册
     */
    USER_REGISTER("USER_REGISTER"),

    /**
     * oss文件对象存储信息加锁，防止短时间内重复插入
     */
    OSS_FILE_INSERT("OSS_FILE_INSERT");

    private final String lockName;

    RedisLockEnum(String lockName) {
        this.lockName = lockName;
    }

}
