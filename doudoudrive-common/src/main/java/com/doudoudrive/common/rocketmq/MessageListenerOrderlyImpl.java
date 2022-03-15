package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.constant.ConsumeStatusEnum;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * <p>顺序消费默认监听实现</p>
 * <p>2022-03-13 13:07</p>
 *
 * @author Dan
 **/
public record MessageListenerOrderlyImpl(
        RocketmqConsumerListener listener) implements MessageListenerOrderly {

    /**
     * @param msg     每次只取一条消息
     * @param context 封装队列和消息信息
     * @return 消费状态 成功（SUCCESS）   重试（SUSPEND_CURRENT_QUEUE_A_MOMENT）
     */
    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msg, ConsumeOrderlyContext context) {
        ConsumeStatusEnum status = MessageHandler.handleMessage(listener, msg, context.getMessageQueue());
        if (status.equals(ConsumeStatusEnum.SUCCESS)) {
            return ConsumeOrderlyStatus.SUCCESS;
        }
        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
    }
}
