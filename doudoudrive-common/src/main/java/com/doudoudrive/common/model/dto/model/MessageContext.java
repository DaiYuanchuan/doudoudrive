package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

/**
 * <p>RocketMQ消费时，当前所消费的消息的上下文信息</p>
 * <p>2022-03-10 23:23</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageContext {

    /**
     * 消费消息所在的消息队列
     *
     * @see MessageQueue
     */
    private MessageQueue messageQueue;

    /**
     * 所消费的消息的扩展属性
     *
     * @see MessageExt
     */
    private MessageExt messageExt;

}
