package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.constant.ConsumeStatusEnum;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * <p>并发消费监听默认实现</p>
 * <p>2022-03-13 13:06</p>
 *
 * @author Dan
 **/
public record MessageListenerConcurrentlyImpl(
        RocketmqConsumerListener listener) implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
        ConsumeStatusEnum status = MessageHandler.handleMessage(listener, messages, context.getMessageQueue());
        if (status.equals(ConsumeStatusEnum.SUCCESS)) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
