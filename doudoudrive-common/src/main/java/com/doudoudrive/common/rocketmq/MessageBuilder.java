package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.log.tracer.context.TracerContextFactory;
import com.doudoudrive.common.model.dto.model.LogLabelModel;
import com.doudoudrive.common.model.dto.model.MessageModel;
import com.doudoudrive.common.util.lang.CompressionUtil;
import com.doudoudrive.common.util.lang.ProtostuffUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <p>通用消息数据统一构建</p>
 * <p>2022-11-18 16:45</p>
 *
 * @author Dan
 **/
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
     * 反序列化通用消息数据内容，解压缩消息内容后反序列化
     *
     * @param message 通用消息内容的字节数组
     * @return 原始消息内容，反序列化失败返回null
     */
    public static Object convert(byte[] message) {
        try {
            // 字节解压缩为字节数组
            byte[] bytes = CompressionUtil.decompressBytes(message);
            // 反序列化为通用消息数据模型
            MessageModel messageModel = SERIALIZER.deserialize(bytes, MessageModel.class);
            if (messageModel == null) {
                return null;
            }

            if (!StringUtils.isAnyBlank(messageModel.getTracerId(), messageModel.getSpanId())) {
                // 链路追踪id和调度id不为空时，设置日志追踪内容
                TracerContextFactory.set(LogLabelModel.builder()
                        .tracerId(messageModel.getTracerId())
                        .spanId(messageModel.getSpanId())
                        .build());
            }
            return messageModel.getMessage();
        } catch (Exception e) {
            return null;
        }
    }
}
