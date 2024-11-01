package com.doudoudrive.log.manager;

import com.doudoudrive.common.constant.RedisDelayedQueueEnum;

/**
 * <p>MQ消费者记录信息的通用业务处理层接口</p>
 * <p>2023-07-25 18:39</p>
 *
 * @author Dan
 **/
public interface MqConsumerRecordManager {

    /**
     * 根据指定的topic发送延迟消息队列消息，同时创建MQ消费者记录信息
     *
     * @param delayedQueueEnum 延迟队列通用枚举，参考{@link RedisDelayedQueueEnum}
     * @param element          元素内容(Base64编码)
     */
    void createConsumerRecord(RedisDelayedQueueEnum delayedQueueEnum, String element);

}
