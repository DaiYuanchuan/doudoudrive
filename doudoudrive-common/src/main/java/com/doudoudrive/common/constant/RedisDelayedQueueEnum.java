package com.doudoudrive.common.constant;

import lombok.Getter;

/**
 * <p>redis延迟队列通用枚举类</p>
 * <p>2023-05-28 22:29</p>
 *
 * @author Dan
 **/
@Getter
public enum RedisDelayedQueueEnum {

    /**
     * 外部回调延迟任务
     */
    EXTERNAL_CALLBACK_TASK(ConstantConfig.Tag.EXTERNAL_CALLBACK_TASK),

    /**
     * 订单超时延迟任务
     */
    ORDER_TIMEOUT_TASK(ConstantConfig.Tag.ORDER_TIMEOUT_TASK);

    /**
     * 延迟队列任务名称
     */
    private final String topic;

    RedisDelayedQueueEnum(String topic) {
        this.topic = topic;
    }

    /**
     * 获取延迟队列的消息通知渠道名称
     *
     * @return 消息通知渠道名，用于订阅
     */
    public String getChannelTopic() {
        return ConstantConfig.Cache.REDIS_DELAY_QUEUE_CHANNEL + ConstantConfig.SpecialSymbols.LEFT_BRACE + topic + ConstantConfig.SpecialSymbols.RIGHT_BRACE;
    }
}
