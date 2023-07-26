package com.doudoudrive.log.manager.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.RedisDelayedQueueEnum;
import com.doudoudrive.common.model.dto.model.DelayQueueMsg;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.lang.CompressionUtil;
import com.doudoudrive.common.util.lang.ProtostuffUtil;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.log.manager.MqConsumerRecordManager;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

/**
 * <p>MQ消费者记录信息的通用业务处理层实现</p>
 * <p>2023-07-25 18:39</p>
 *
 * @author Dan
 **/
@Service("mqConsumerRecordManager")
public class MqConsumerRecordManagerImpl implements MqConsumerRecordManager {

    /**
     * Base64解码器
     */
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    /**
     * 序列化工具
     */
    private static final ProtostuffUtil<DelayQueueMsg> SERIALIZER = new ProtostuffUtil<>();
    private RocketmqConsumerRecordService rocketmqConsumerRecordService;
    private RocketMQTemplate rocketmqTemplate;

    @Autowired
    public void setRocketmqConsumerRecordService(RocketmqConsumerRecordService rocketmqConsumerRecordService) {
        this.rocketmqConsumerRecordService = rocketmqConsumerRecordService;
    }

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    /**
     * 根据指定的topic发送延迟消息队列消息，同时创建MQ消费者记录信息
     *
     * @param delayedQueueEnum 延迟队列通用枚举，参考{@link RedisDelayedQueueEnum}
     * @param element          元素内容(Base64编码)
     */
    @Override
    public void createConsumerRecord(RedisDelayedQueueEnum delayedQueueEnum, String element) {
        // 原始消息体
        byte[] body = DECODER.decode(element);
        // 字节解压缩为字节数组
        byte[] bytes = CompressionUtil.decompressBytes(body);
        // 反序列化为延迟队列的消息体
        Optional.ofNullable(SERIALIZER.deserialize(bytes, DelayQueueMsg.class))
                // 使用RocketMQ同步模式发送消息，同时创建MQ消费者记录信息
                .ifPresent(delayQueueMsg -> MessageBuilder.syncSend(ConstantConfig.Topic.DELAY_MESSAGE_QUEUE_SERVICE,
                        delayedQueueEnum.getTopic(), delayQueueMsg.getBody(), rocketmqTemplate,
                        consumerRecord -> rocketmqConsumerRecordService.insert(consumerRecord)));
    }
}
