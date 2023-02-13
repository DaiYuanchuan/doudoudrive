package com.doudoudrive.common.cache.lock;

/**
 * <p>redis分布式锁的处理</p>
 * <p>2023-02-11 21:02</p>
 *
 * @author Dan
 **/
public interface RedisLockManager {

    /**
     * 获取锁，如果锁不可用，则当前线程出于线程调度的目的将被禁用，并处于休眠状态，直到获得锁。
     *
     * @param name 锁的名称，缓存的key
     * @return 锁的value，用于释放锁
     */
    String lock(String name);


    /**
     * 释放锁
     *
     * @param name  锁的名称，缓存的key
     * @param value 锁的value
     */
    void unlock(String name, String value);

}
