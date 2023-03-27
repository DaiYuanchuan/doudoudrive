package com.doudoudrive.common.cache.lock;

import java.util.concurrent.TimeUnit;

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
     * 获取锁，如果锁不可用，则当前线程出于线程调度的目的将被禁用，并处于休眠状态，直到获得锁。
     *
     * @param name  锁的名称，缓存的key
     * @param value 自定义锁的value，可用于重入锁
     */
    void lock(String name, String value);

    /**
     * 获取定义{@code leaseTime}的锁，如果需要，等待直到锁可用，
     * 锁定将在定义{@code leaseTime} 间隔后自动释放。
     *
     * @param name      锁的名称，缓存的key
     * @param leaseTime 获取锁后持有锁的最大时间
     *                  如果它还没有被释放，调用{@code unlock}方法
     *                  如果{@code leaseTime}为-1，则持有锁直到显式解锁为止
     * @param unit      时间单位
     * @return 锁的value，用于释放锁
     */
    String lock(String name, long leaseTime, TimeUnit unit);

    /**
     * 获取定义{@code leaseTime}的锁，如果需要，等待直到锁可用，
     * 锁定将在定义{@code leaseTime} 间隔后自动释放。
     *
     * @param name      锁的名称，缓存的key
     * @param value     自定义锁的value，可用于重入锁
     * @param leaseTime 获取锁后持有锁的最大时间
     *                  如果它还没有被释放，调用{@code unlock}方法
     *                  如果{@code leaseTime}为-1，则持有锁直到显式解锁为止
     * @param unit      时间单位
     */
    void lock(String name, String value, long leaseTime, TimeUnit unit);

    /**
     * 尝试获取锁，只有当锁在调用时是空闲的，才会获取锁。
     * 如果锁可用，则获取锁并立即返回值为{@code true}
     * 如果锁不可用，则此方法将立即返回{@code false}
     * 这种方法的一个典型用法是：
     * <pre> {@code
     * Lock lock = ...;
     * if (lock.tryLock()) {
     *   try {
     *     // manipulate protected state
     *   } finally {
     *     lock.unlock();
     *   }
     * } else {
     *   // perform alternative actions
     * }}</pre>
     *
     * @param name  锁的名称，缓存的key
     * @param value 锁的value，用于释放锁
     * @return 如果获得锁则为{@code true}，否则为{@code false}
     */
    boolean tryLock(String name, String value);

    /**
     * 释放锁
     *
     * @param name  锁的名称，缓存的key
     * @param value 锁的value
     */
    void unlock(String name, String value);

}
