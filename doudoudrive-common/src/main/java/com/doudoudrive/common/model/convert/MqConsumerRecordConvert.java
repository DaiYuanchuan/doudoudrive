package com.doudoudrive.common.model.convert;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageQueue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

/**
 * <p>RocketMQ消费记录信息等相关的实体数据类型转换器</p>
 * <p>2022-05-25 23:52</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {ConstantConfig.class, Date.class, MessageConst.class})
public interface MqConsumerRecordConvert {

    /**
     * 将 SendResult(发送结果) 类型转换为 RocketmqConsumerRecord(RocketMQ消费记录)
     *
     * @param sendResult   发送结果
     * @param messageQueue 消息队列
     * @param tag          消息标签
     * @return RocketMQ消费记录
     */
    @Mappings({
            @Mapping(target = "sendStatus", expression = "java(ConstantConfig.MqMessageSendStatus.getStatusValue(sendResult.getSendStatus()))"),
            @Mapping(target = "sendTime", expression = "java(new Date())")
    })
    RocketmqConsumerRecord sendResultConvertConsumerRecord(SendResult sendResult, MessageQueue messageQueue, String tag);

    /**
     * 将 MessageContext(消息上下文) 类型转换为 RocketmqConsumerRecord(RocketMQ消费记录)
     *
     * @param messageContext 消息上下文
     * @param topic          消息主题
     * @param tag            消息标签
     * @return RocketMQ消费记录
     */
    @Mappings({
            @Mapping(target = "msgId", source = "messageContext.messageExt.msgId"),
            @Mapping(target = "offsetMsgId", expression = "java(messageContext.getMessageExt().getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX))"),
            @Mapping(target = "sendStatus", expression = "java(ConstantConfig.MqMessageSendStatus.SEND_OK.status)"),
            @Mapping(target = "brokerName", source = "messageContext.messageQueue.brokerName"),
            @Mapping(target = "queueId", source = "messageContext.messageExt.queueId"),
            @Mapping(target = "queueOffset", source = "messageContext.messageExt.queueOffset"),
            @Mapping(target = "sendTime", expression = "java(new Date(messageContext.getMessageExt().getBornTimestamp()))"),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    RocketmqConsumerRecord messageContextConvertConsumerRecord(MessageContext messageContext, String topic, String tag);

}
