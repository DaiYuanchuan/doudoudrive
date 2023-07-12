package com.doudoudrive.common.cache.timer;

import com.doudoudrive.common.constant.RedisDelayedQueueEnum;

import java.util.concurrent.TimeUnit;

/**
 * <p>redis延迟队列接口</p>
 * <p>2023-05-28 18:12</p>
 *
 * @author Dan
 **/
public interface RedisDelayedQueue {

    /**
     * 将元素插入指定的延迟队列中
     *
     * @param delayedQueue 需要将元素插入到指定的队列
     * @param delay        延迟时间
     * @param timeUnit     时间单位
     * @param body         消息体
     */
    void offer(RedisDelayedQueueEnum delayedQueue, long delay, TimeUnit timeUnit, Object body);

}
