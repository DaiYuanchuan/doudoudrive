package com.doudoudrive.common.rocketmq;

import cn.hutool.core.convert.Convert;
import com.doudoudrive.common.constant.ConsumeStatusEnum;
import com.doudoudrive.common.model.dto.model.MessageContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.nio.charset.StandardCharsets;
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
                listener.onMessage(convert(listener.getConsumerConfig().getTags().get(msg.getTags()), msg.getBody()), msg.getBody(), messageContext);
                if (log.isDebugEnabled()) {
                    log.debug("消费完成");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeStatusEnum.RETRY;
        }
        return ConsumeStatusEnum.SUCCESS;
    }

    /**
     * byte字节数组类型换算
     *
     * @param type 需要转换的类型
     * @param body 消息体
     * @return 响应类型
     */
    public static Object convert(Class<?> type, byte[] body) {
        if (type == null) {
            return body;
        }
        if (String.class.equals(type)) {
            return new String(body, StandardCharsets.UTF_8);
        }
        return Convert.convert(type, body);
    }
}
