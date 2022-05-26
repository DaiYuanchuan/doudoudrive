package com.doudoudrive.common.model.convert;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import org.apache.rocketmq.client.producer.SendResult;
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
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {ConstantConfig.class, Date.class})
public interface MqConsumerRecordConvert {

    /**
     * 将 SendResult(发送结果) 类型转换为 RocketmqConsumerRecord(RocketMQ消费记录)
     *
     * @param sendResult 发送结果
     * @return RocketMQ消费记录
     */
    @Mappings({
            @Mapping(target = "sendStatus", expression = "java(ConstantConfig.MqMessageSendStatus.getStatusValue(sendResult.getSendStatus()))"),
            @Mapping(target = "sendTime", expression = "java(new Date())")
    })
    RocketmqConsumerRecord sendResultConvertConsumerRecord(SendResult sendResult, MessageQueue messageQueue, String tag);

}
