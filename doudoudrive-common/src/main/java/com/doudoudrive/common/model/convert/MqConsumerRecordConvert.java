package com.doudoudrive.common.model.convert;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.util.lang.ConvertUtil;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageQueue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Date;

/**
 * <p>RocketMQ消费记录信息等相关的实体数据类型转换器</p>
 * <p>2022-05-25 23:52</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {ConstantConfig.class, Date.class, MessageConst.class, NumberConstant.class, ConvertUtil.class})
public interface MqConsumerRecordConvert {

    /**
     * MapStruct 框架生成，用于获取生成的实现类
     */
    MqConsumerRecordConvert INSTANCE = Mappers.getMapper(MqConsumerRecordConvert.class);

    /**
     * 将 SendResult(发送结果) 类型转换为 RocketmqConsumerRecord(RocketMQ消费记录)
     *
     * @param sendResult   发送结果
     * @param messageQueue 消息队列
     * @param tag          消息标签
     * @param body         消息体内容
     * @return RocketMQ消费记录
     */
    @Mappings({
            @Mapping(target = "retryCount", expression = "java(NumberConstant.INTEGER_ZERO)"),
            @Mapping(target = "sendStatus", expression = "java(ConstantConfig.MqMessageSendStatus.getStatusValue(sendResult.getSendStatus()))"),
            @Mapping(target = "sendTime", expression = "java(new Date())"),
            @Mapping(target = "status", expression = "java(ConstantConfig.RocketmqConsumerStatusEnum.WAIT.getStatus())"),
            @Mapping(target = "body", expression = "java(ConvertUtil.convertBase64(body))"),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    RocketmqConsumerRecord sendResultConvertConsumerRecord(SendResult sendResult, MessageQueue messageQueue, String tag, byte[] body);

    /**
     * 将 MessageContext(消息上下文) 类型转换为 RocketmqConsumerRecord(RocketMQ消费记录)
     *
     * @param messageContext 消息上下文
     * @param body           消息体内容
     * @return RocketMQ消费记录
     */
    @Mappings({
            @Mapping(target = "businessId", source = "messageContext.messageExt.keys"),
            @Mapping(target = "msgId", source = "messageContext.messageExt.msgId"),
            @Mapping(target = "offsetMsgId", expression = "java(messageContext.getMessageExt().getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX))"),
            @Mapping(target = "retryCount", expression = "java(NumberConstant.INTEGER_ZERO)"),
            @Mapping(target = "topic", expression = "java(messageContext.getMessageExt().getTopic())"),
            @Mapping(target = "tag", expression = "java(messageContext.getMessageExt().getTags())"),
            @Mapping(target = "brokerName", source = "messageContext.messageQueue.brokerName"),
            @Mapping(target = "queueId", source = "messageContext.messageExt.queueId"),
            @Mapping(target = "queueOffset", source = "messageContext.messageExt.queueOffset"),
            @Mapping(target = "sendTime", expression = "java(new Date(messageContext.getMessageExt().getBornTimestamp()))"),
            @Mapping(target = "status", expression = "java(ConstantConfig.RocketmqConsumerStatusEnum.WAIT.getStatus())"),
            @Mapping(target = "body", expression = "java(ConvertUtil.convertBase64(body))"),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    RocketmqConsumerRecord messageContextConvertConsumerRecord(MessageContext messageContext, byte[] body);

}
