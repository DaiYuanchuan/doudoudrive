package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.log.tracer.context.TracerContextFactory;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.LogLabelModel;
import com.doudoudrive.common.model.dto.model.MessageModel;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.util.lang.CompressionUtil;
import com.doudoudrive.common.util.lang.ProtostuffUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>通用消息数据统一构建</p>
 * <p>2022-11-18 16:45</p>
 *
 * @author Dan
 **/
@Slf4j
public class MessageBuilder {

    /**
     * 序列化工具
     */
    private static final ProtostuffUtil<MessageModel> SERIALIZER = new ProtostuffUtil<>();

    /**
     * 构建通用消息内容，序列化消息内容后压缩
     *
     * @param message 原始消息内容
     * @return 通用消息内容的字节数组
     */
    public static byte[] build(Object message) {
        Map<String, String> contextMap = TracerContextFactory.get();
        // 构建消息内容，使用protostuff序列化后压缩消息内容
        return CompressionUtil.compress(SERIALIZER.serialize(MessageModel.builder()
                .tracerId(contextMap.getOrDefault(ConstantConfig.LogTracer.TRACER_ID, StringUtils.EMPTY))
                .spanId(contextMap.getOrDefault(ConstantConfig.LogTracer.SPAN_ID, StringUtils.EMPTY))
                .message(message)
                .build()));
    }

    /**
     * RocketMq 使用sync模式同步发送消息，生成消费记录
     *
     * @param topic    消息主题
     * @param tag      消息标签
     * @param message  消息内容
     * @param template RocketMQTemplate实例
     * @param record   消息消费记录的回调，包含消息发送状态，只有消息发送失败时才会发起回调，通常用于保存记录
     */
    public static void syncSend(String topic, String tag, Object message,
                                RocketMQTemplate template, Consumer<RocketmqConsumerRecord> record) {
        // 获取一个通用消息数据模型
        syncSend(topic, tag, build(message), template, record);
    }

    /**
     * RocketMq 使用sync模式同步发送消息，生成消费记录
     *
     * @param topic    消息主题
     * @param tag      消息标签
     * @param message  消息内容
     * @param template RocketMQTemplate实例
     * @param record   消息消费记录的回调，包含消息发送状态，只有消息发送失败时才会发起回调，通常用于保存记录
     */
    public static void syncSend(String topic, String tag, byte[] message,
                                RocketMQTemplate template, Consumer<RocketmqConsumerRecord> record) {
        try {
            // 生成消费记录的业务id，用于幂等性校验
            String businessId = SequenceUtil.nextId(SequenceModuleEnum.ROCKETMQ_CONSUMER_RECORD);
            // 使用sync模式发送消息，保证消息发送成功
            String destination = topic + ConstantConfig.SpecialSymbols.ENGLISH_COLON + tag;
            // 发送消息
            SendResult sendResult = template.syncSend(destination, org.springframework.messaging.support.MessageBuilder.withPayload(message)
                    // 设置消息唯一标识
                    .setHeader(MessageConst.PROPERTY_KEYS, businessId)
                    .build());
            // 构建消息消费记录
            MqConsumerRecordConvert consumerRecordConvert = MqConsumerRecordConvert.INSTANCE;
            RocketmqConsumerRecord consumerRecord = consumerRecordConvert.sendResultConvertConsumerRecord(sendResult,
                    sendResult.getMessageQueue(), tag, message);
            consumerRecord.setBusinessId(businessId);
            // 消息发送失败，保存消息消费记录
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                consumerRecord.setRetryCount(consumerRecord.getRetryCount() + NumberConstant.INTEGER_ONE);
                // 发起消费记录回调
                record.accept(consumerRecord);
            }
        } catch (Exception e) {
            log.error("send to mq, topic {}:{}, errorMsg:{}", topic, tag, e.getMessage(), e);
        }
    }

    /**
     * RocketMq 使用one-way模式发送消息，不生成消费记录，发送端发送完消息后会立即返回，不等待服务器响应
     *
     * @param topic    消息主题
     * @param tag      消息标签
     * @param message  消息内容
     * @param template RocketMQTemplate实例
     */
    public static void sendOneWay(String topic, String tag, Object message, RocketMQTemplate template) {
        // 使用one-way模式发送消息
        String destination = topic + ConstantConfig.SpecialSymbols.ENGLISH_COLON + tag;
        template.sendOneWay(destination, build(message));
    }

    /**
     * 反序列化通用消息数据内容，解压缩消息内容后反序列化
     *
     * @param message 通用消息内容的字节数组
     * @return 原始消息内容，反序列化失败返回null
     */
    public static MessageModel convert(byte[] message) {
        try {
            // 字节解压缩为字节数组
            byte[] bytes = CompressionUtil.decompressBytes(message);
            // 反序列化为通用消息数据模型
            MessageModel messageModel = SERIALIZER.deserialize(bytes, MessageModel.class);
            if (messageModel == null) {
                return null;
            }

            if (StringUtils.isNoneBlank(messageModel.getTracerId(), messageModel.getSpanId())) {
                // 链路追踪id和调度id不为空时，设置日志追踪内容
                TracerContextFactory.set(LogLabelModel.builder()
                        .tracerId(messageModel.getTracerId())
                        .spanId(messageModel.getSpanId())
                        .build());
            }
            return messageModel;
        } catch (Exception e) {
            return null;
        }
    }
}
