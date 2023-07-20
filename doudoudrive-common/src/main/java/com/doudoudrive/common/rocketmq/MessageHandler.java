package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.constant.ConsumeStatusEnum;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.model.MessageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

/**
 * <p>消息内容处理器</p>
 * <p>2022-03-13 12:46</p>
 *
 * @author Dan
 **/
@Slf4j
public class MessageHandler {

    /**
     * 消费者消息处理程序，接收并转换消息类型
     *
     * @param listener     RocketMQ消费者监听器接口
     * @param messages     需要处理的消息
     * @param messageQueue 消费消息所在的消息队列
     * @return RocketMQ消费者消费状态的枚举
     */
    public static ConsumeStatusEnum handleMessage(RocketmqConsumerListener listener, List<MessageExt> messages, MessageQueue messageQueue) {
        try {
            for (MessageExt msg : messages) {
                final MessageContext messageContext = new MessageContext();
                messageContext.setMessageExt(msg);
                messageContext.setMessageQueue(messageQueue);
                if (log.isDebugEnabled()) {
                    log.debug("开始消费，msgId={}，msg={}", msg.getMsgId(), msg);
                }
                // 解压缩消息体
                MessageModel messageModel = MessageBuilder.convert(msg.getBody());
                // 消费者接受的消息类型
                Class<?> type = listener.getConsumerConfig().getTags().get(msg.getTags());
                Object message = MessageModel.class.equals(type) ? messageModel : (messageModel == null ? null : messageModel.getMessage());
                listener.onMessage(message, msg.getBody(), messageContext);
                if (log.isDebugEnabled()) {
                    log.debug("消费完成");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ConsumeStatusEnum.RETRY;
        }
        return ConsumeStatusEnum.SUCCESS;
    }
}
