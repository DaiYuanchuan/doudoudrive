package com.doudoudrive.common.cache.lock;

import cn.hutool.core.util.IdUtil;
import com.doudoudrive.common.cache.RedisMessageSubscriber;
import com.doudoudrive.common.cache.RedisTemplateClient;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.ExpirationEntry;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.google.common.collect.Lists;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * <p>基于redis的分布式锁的实现，可实现重入锁</p>
 * <p>如果客户端断开连接，锁将自动删除，避免死锁</p>
 * <p>实现非公平锁，因此不保证锁的获取顺序</p>
 * <p>2023-02-10 18:26</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("redisLockManager")
public class RedisLockManagerImpl implements RedisLockManager, RedisMessageSubscriber, CommandLineRunner, Closeable {

    /**
     * Redis客户端操作相关工具类
     */
    private RedisTemplateClient redisTemplateClient;

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    /**
     * 看门狗，时间轮定时器
     */
    private HashedWheelTimer timer;

    /**
     * 获取锁的lua脚本
     * <pre>
     *   # 当key不存在或者key已经被当前客户端当前线程加锁了，则加锁成功，返回null
     *   if ((redis.call('exists', KEYS[1]) == 0) or (redis.call('hexists', KEYS[1], ARGV[2]) == 1)) then
     *     # 将key的value加1，表示当前客户端当前线程加锁次数加1
     *     redis.call('hincrby', KEYS[1], ARGV[2], 1);
     *     # 设置key的过期时间
     *     redis.call('pexpire', KEYS[1], ARGV[1]);
     *     # 加锁成功，返回null
     *     return nil;
     *   end;
     *     # 加锁失败，返回key的过期时间
     *     return redis.call('pttl', KEYS[1]);
     * </pre>
     */
    private static final String ACQUIRE_LOCK_LUA = "if ((redis.call('exists', KEYS[1]) == 0) or (redis.call('hexists', KEYS[1], ARGV[2]) == 1)) then "
            + "redis.call('hincrby', KEYS[1], ARGV[2], 1); "
            + "redis.call('pexpire', KEYS[1], ARGV[1]); "
            + "return nil; "
            + "end; "
            + "return redis.call('pttl', KEYS[1]);";

    /**
     * 用于刷新锁的超时时间的lua脚本
     * <pre>
     *   # 如果key存在并且key已经被当前客户端当前线程加锁了，则刷新锁的超时时间
     *   if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then
     *     # 设置key的过期时间
     *     redis.call('pexpire', KEYS[1], ARGV[1]);
     *     # 刷新锁的超时时间成功，返回1
     *     return 1;
     *   end;
     *     # 刷新锁的超时时间失败，返回0
     *     return 0;
     * </pre>
     */
    private static final String REFRESH_LOCK_LUA = "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then "
            + "redis.call('pexpire', KEYS[1], ARGV[1]); "
            + "return 1; "
            + "end; "
            + "return 0;";

    /**
     * 用于释放锁的lua脚本
     * null ：删除锁失败，0：锁重入，1：删除锁成功
     * <pre>
     *   # 如果哈希表 key 中，给定域 field 字段不存在，则释放锁失败，返回nil
     *   if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then
     *     return nil;
     *   end;
     *   # 将key的value减1，表示当前客户端当前线程加锁次数减1
     *   local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1);
     *   # 如果当前客户端当前线程加锁次数大于0，锁重入，返回0
     *   if (counter > 0) then
     *     # 设置key的过期时间
     *     redis.call('pexpire', KEYS[1], ARGV[2]);
     *     return 0;
     *   else
     *     # 如果当前客户端当前线程加锁次数等于0，则释放锁成功，删除key，返回1
     *     redis.call('del', KEYS[1]);
     *     # 发布消息，通知其他客户端
     *     redis.call('publish', KEYS[2], ARGV[1]);
     *     return 1;
     *   end;
     *   # 释放锁失败，返回nil
     *   return nil;
     * </pre>
     */
    private static final String UNLOCK_LUA = "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then "
            + "return nil; end; "
            + "local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); "
            + "if (counter > 0) then "
            + "redis.call('pexpire', KEYS[1], ARGV[2]); "
            + "return 0; "
            + "else "
            + "redis.call('del', KEYS[1]); "
            + "redis.call('publish', '" + ConstantConfig.Cache.ChanelEnum.REDIS_LOCK_CHANNEL.getChannel() + "', ARGV[1]); "
            + "return 1; "
            + "end; "
            + "return nil;";

    /**
     * 到期续订的map，用于保存全局使用到watchDog锁续命机制的线程详情
     */
    private static final ConcurrentMap<String, ExpirationEntry> EXPIRATION_RENEWAL_MAP = new ConcurrentHashMap<>();

    /**
     * 用于保存全局使用到的锁的实例，key是锁的名称，value是锁的实例
     */
    private static final ConcurrentMap<String, List<Semaphore>> ENTRIES = new ConcurrentHashMap<>();

    /**
     * 如果获取锁时没有设置锁过期释放时间leaseTime 则设置锁的默认过期时间为30秒
     */
    private static Long internalLockLeaseTime = NumberConstant.LONG_THREE * NumberConstant.LONG_TEN * NumberConstant.LONG_ONE_THOUSAND;

    /**
     * 看门狗的时间轮的刻度数量
     */
    private static final Integer TICKS_PER_WHEEL = 1024;

    /**
     * 看门狗的时间轮线程池名称
     */
    private static final String POOL_NAME = "redis-lock";

    /**
     * 格式化的连接符
     */
    private static final String CONNECTOR_FORMAT = "%s:%s";

    /**
     * 获取锁，如果锁不可用，则当前线程出于线程调度的目的将被禁用，并处于休眠状态，直到获得锁。
     *
     * @param name 锁的名称，缓存的key
     * @return 锁的value，用于释放锁
     */
    @Override
    public String lock(String name) {
        // 生成锁的uuid
        String uuid = IdUtil.fastSimpleUUID();
        try {
            this.lock(name, uuid, NumberConstant.INTEGER_MINUS_ONE, null);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
        return uuid;
    }

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
    @Override
    public String lock(String name, long leaseTime, TimeUnit unit) {
        // 生成锁的uuid
        String uuid = IdUtil.fastSimpleUUID();
        try {
            lock(name, uuid, leaseTime, unit);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
        return uuid;
    }

    /**
     * 释放锁
     *
     * @param name  锁的名称，缓存的key
     * @param value 锁的value
     */
    @Override
    public void unlock(String name, String value) {
        // 获取当前需要解锁的线程id
        long threadId = Thread.currentThread().getId();
        // 尝试释放锁
        Boolean refresh = redisTemplateClient.eval(UNLOCK_LUA, Collections.singletonList(name), Boolean.class, name, internalLockLeaseTime, getLockName(value, threadId));
        // 取消过期续期任务
        this.cancelExpirationRenewal(getTaskName(name, value), threadId);
        if (refresh == null) {
            log.error("attempt to unlock lock, not locked by current thread by node id: {} thread-id: {}", value, threadId);
        }
    }

    /**
     * 订阅redis释放锁时的消息
     * 获取到锁的线程：释放锁时发布所释放信号
     * 获取锁时设置等待时间并且获取不到锁的线程：订阅其他线程释放锁时的信号，收到信号后重新获取锁
     *
     * @param message 发布的消息内容
     * @param channel 当前消息体对应的通道
     */
    @Override
    public void receiveMessage(byte[] message, String channel) {
        // 锁释放时触发
        if (ConstantConfig.Cache.ChanelEnum.REDIS_LOCK_CHANNEL.getChannel().equals(channel)) {
            if (ENTRIES.isEmpty()) {
                return;
            }
            // 获取指定锁下的所有实例
            Optional.ofNullable((String) redisTemplateClient.getValueSerializer().deserialize(message))
                    .flatMap(key -> Optional.ofNullable(ENTRIES.get(key))
                            .flatMap(entryList -> Optional.ofNullable(entryList.get(NumberConstant.INTEGER_ZERO))))
                    .ifPresent(Semaphore::release);
        }
    }

    /**
     * 初始化看门狗，时间轮定时器，刻度持续时间为100毫秒，刻度数量为1024
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        // 初始化时间轮定时器
        timer = new HashedWheelTimer(new DefaultThreadFactory(POOL_NAME), NumberConstant.INTEGER_HUNDRED, TimeUnit.MILLISECONDS, TICKS_PER_WHEEL, Boolean.FALSE);
    }

    /**
     * 关闭看门狗，时间轮定时器
     */
    @Override
    public void close() {
        timer.stop();
    }

    // ==================================================== private ====================================================

    /**
     * 获取锁
     * <p>如果锁不可用，则当前线程出于线程调度的目的将被禁用，并处于休眠状态，直到获得锁</p>
     *
     * @param name      锁的名称(key值)
     * @param value     锁的value，用于释放锁
     * @param leaseTime 持有锁的时间
     * @param unit      锁的过期时间单位
     * @throws InterruptedException 线程被中断异常
     */
    private void lock(String name, String value, long leaseTime, TimeUnit unit) throws InterruptedException {
        // 获取当前需要加锁的线程id
        long threadId = Thread.currentThread().getId();
        // 尝试加锁，加锁成功返回null，失败返回锁的剩余超时时间
        Long ttl = tryAcquireLock(name, value, leaseTime, unit, threadId);
        // 成功获取到锁
        if (ttl == null) {
            return;
        }

        // 获取锁失败，构建一个Semaphore用于挂起当前线程
        List<Semaphore> semaphoreList = ENTRIES.compute(name, (key, entryList) -> Optional.ofNullable(entryList).orElse(Lists.newArrayList()));
        // 插销，可以设定一个阈值，基于此，多个线程竞争获取许可信号，做完自己的申请后归还，超过阈值后，线程申请许可信号将会被阻塞
        Semaphore latch = new Semaphore(NumberConstant.INTEGER_ZERO);
        semaphoreList.add(latch);

        try {
            while (true) {
                // 再次获取锁
                ttl = tryAcquireLock(name, value, leaseTime, unit, threadId);
                // 获取锁成功，退出循环
                if (ttl == null) {
                    break;
                }

                // 获取锁失败，挂起当前线程等待锁的释放
                if (ttl >= NumberConstant.INTEGER_ZERO) {
                    // 尝试获取许可信号，如果获取不到，则阻塞当前线程，如果在获得许可证之前等待时间已过，则返回false
                    if (!latch.tryAcquire(ttl, TimeUnit.MILLISECONDS)) {
                        // 等待时间已过，尝试再次获取锁
                        ttl = tryAcquireLock(name, value, leaseTime, unit, threadId);
                        // 获取锁成功，退出循环
                        if (ttl == null) {
                            break;
                        }
                    }
                } else {
                    latch.acquireUninterruptibly();
                }
            }
        } finally {
            // 因为是同步操作，所以无论加锁成功或失败，都要释放许可
            this.unsubscribe(name, latch);
        }
    }

    /**
     * 执行lua脚本，尝试获取锁
     * <p>持有锁的线程在{@code leaseTime}时间内没有释放锁，则锁会自动释放</p>
     * <p>如果{@code leaseTime}为null，或者-1，则启用定时任务，续订锁的过期时间，直到显示的调用unlock方法</p>
     *
     * @param name      锁的名称(key值)
     * @param value     锁的value，用于释放锁
     * @param leaseTime 持有锁的时间
     * @param unit      锁的过期时间单位
     * @param threadId  线程id
     * @return 如果返回null，则表示获取锁成功，否则获取锁失败，返回锁的过期时间
     */
    private Long tryAcquireLock(String name, String value, long leaseTime, TimeUnit unit, long threadId) {
        Long ttl;

        // 执行lua脚本，尝试获取锁，获取锁失败会返回锁的过期时间
        if (leaseTime > NumberConstant.INTEGER_ZERO) {
            ttl = redisTemplateClient.eval(ACQUIRE_LOCK_LUA, Collections.singletonList(name), Long.class, unit.toMillis(leaseTime), getLockName(value, threadId));
        } else {
            // 设置默认的过期时间为30秒
            long millis = TimeUnit.MILLISECONDS.toMillis(internalLockLeaseTime);
            ttl = redisTemplateClient.eval(ACQUIRE_LOCK_LUA, Collections.singletonList(name), Long.class, millis, getLockName(value, threadId));
        }

        // 获取锁成功
        if (ttl == null) {
            if (leaseTime > NumberConstant.INTEGER_ZERO) {
                internalLockLeaseTime = unit.toMillis(leaseTime);
            } else {
                // 启用时间轮任务，续订锁的过期时间，直到显示的调用unlock方法
                scheduleExpirationRenewal(name, value, threadId);
            }
        }
        return ttl;
    }

    /**
     * 释放锁的许可
     *
     * @param name  锁的名称
     * @param latch 许可信号
     */
    private void unsubscribe(String name, Semaphore latch) {
        // 释放许可
        latch.release();
        // 从map中移除许可
        ENTRIES.compute(name, (key, entryList) -> {
            if (CollectionUtil.isEmpty(entryList)) {
                return null;
            }
            entryList.removeIf(redisLockEntry -> redisLockEntry.equals(latch));
            if (CollectionUtil.isEmpty(entryList)) {
                return null;
            }
            return entryList;
        });
    }

    /**
     * 执行续订锁的过期时间任务
     *
     * @param name     锁的名称
     * @param uuid     锁的uuid
     * @param threadId 线程id
     */
    private void scheduleExpirationRenewal(String name, String uuid, long threadId) {
        ExpirationEntry entry = new ExpirationEntry();
        // 对应任务不存在时，添加任务，否则返回任务名称对应的任务对象
        Optional.ofNullable(EXPIRATION_RENEWAL_MAP.putIfAbsent(getTaskName(name, uuid), entry))
                // 如果任务存在，则在任务中添加一个线程id
                .ifPresentOrElse(oldEntry -> oldEntry.addThreadId(threadId), () -> {
                    entry.addThreadId(threadId);
                    try {
                        // 执行续订锁的任务
                        renewExpiration(name, uuid);
                    } finally {
                        // 如果当前线程被中断，则取消续订锁的任务
                        if (Thread.currentThread().isInterrupted()) {
                            cancelExpirationRenewal(getTaskName(name, uuid), threadId);
                        }
                    }
                });
    }

    /**
     * 更新锁的过期时间
     *
     * @param name 锁的名称
     * @param uuid 锁的uuid
     */
    private void renewExpiration(String name, String uuid) {
        ExpirationEntry entry = EXPIRATION_RENEWAL_MAP.get(getTaskName(name, uuid));
        if (entry == null) {
            return;
        }

        // 创建一个定时任务，用于续订锁的过期时间，每隔internalLockLeaseTime/3(10s)的时间续订一次
        Timeout task = timer.newTimeout(timeout -> Optional.ofNullable(EXPIRATION_RENEWAL_MAP.get(getTaskName(name, uuid)))
                // 获取map队列里面的第一个线程Id
                .flatMap(ent -> Optional.ofNullable(ent.getFirstThreadId()))
                .ifPresent(threadId -> {
                    try {
                        // 刷新锁的超时时间
                        Boolean refresh = redisTemplateClient.eval(REFRESH_LOCK_LUA, Collections.singletonList(name), Boolean.class, internalLockLeaseTime, getLockName(uuid, threadId));
                        if (refresh) {
                            // 刷新锁的超时时间成功，继续执行
                            renewExpiration(name, uuid);
                        } else {
                            // 刷新锁的超时时间失败，取消续期
                            cancelExpirationRenewal(getTaskName(name, uuid), threadId);
                        }
                    } catch (Exception e) {
                        log.error("Can't update lock {} expiration", name, e);
                        EXPIRATION_RENEWAL_MAP.remove(getTaskName(name, uuid));
                    }
                }), internalLockLeaseTime / NumberConstant.INTEGER_THREE, TimeUnit.MILLISECONDS);
        entry.setTimeout(task);
    }

    /**
     * 取消过期续期任务
     *
     * @param taskName 过期续期任务的名称
     * @param threadId 线程id
     */
    private void cancelExpirationRenewal(String taskName, Long threadId) {
        Optional.ofNullable(EXPIRATION_RENEWAL_MAP.get(taskName)).ifPresent(task -> {
            // 移除线程id
            Optional.ofNullable(threadId).ifPresent(task::removeThreadId);
            if (threadId == null || task.hasNoThreads()) {
                Optional.ofNullable(task.getTimeout()).ifPresent(Timeout::cancel);
                // 移除过期续期任务
                EXPIRATION_RENEWAL_MAP.remove(taskName);
            }
        });
    }

    /**
     * 获取过期续期任务的名称，格式为：uuid:name
     *
     * @param name 锁的名称
     * @param uuid 锁的uuid
     * @return 过期续期任务的名称
     */
    private String getTaskName(String name, String uuid) {
        return String.format(CONNECTOR_FORMAT, uuid, name);
    }

    /**
     * 获取锁的名称，格式为：uuid:threadId
     *
     * @param uuid     uuid
     * @param threadId 线程id
     * @return 锁的名称
     */
    private String getLockName(String uuid, long threadId) {
        return String.format(CONNECTOR_FORMAT, uuid, threadId);
    }
}
