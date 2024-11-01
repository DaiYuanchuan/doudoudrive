package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.config.ConsumerConfig;
import com.doudoudrive.common.global.ConsumeException;
import com.doudoudrive.common.model.dto.model.MessageContext;

/**
 * <p>RocketMQ消费者监听器接口</p>
 * <p>2022-03-13 12:48</p>
 *
 * @author Dan
 **/
public interface RocketmqConsumerListener {

    /**
     * 讯息处理
     *
     * @param body           消息体
     * @param type           消费者接受的消息类型
     * @param messageContext 消息的上下文信息
     * @throws ConsumeException 抛出自定义消费者异常
     */
    void onMessage(byte[] body, Class<?> type, MessageContext messageContext) throws ConsumeException;

    /**
     * 获取消费者配置信息
     *
     * @return consumer的基本配置
     */
    ConsumerConfig getConsumerConfig();

}
