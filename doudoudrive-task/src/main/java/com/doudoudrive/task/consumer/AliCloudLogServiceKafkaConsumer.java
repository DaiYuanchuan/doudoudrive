package com.doudoudrive.task.consumer;

import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.log.tracer.context.TracerContextFactory;
import com.doudoudrive.common.model.dto.model.LogLabelModel;
import com.doudoudrive.common.model.dto.model.aliyun.AliCloudCdnLogModel;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>阿里云CDN日志服务Kafka实时消费监听</p>
 * <p>2024-03-31 17:17</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class AliCloudLogServiceKafkaConsumer {

    private RocketMQTemplate rocketmqTemplate;
    private RocketmqConsumerRecordService rocketmqConsumerRecordService;

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired
    public void setRocketmqConsumerRecordService(RocketmqConsumerRecordService rocketmqConsumerRecordService) {
        this.rocketmqConsumerRecordService = rocketmqConsumerRecordService;
    }

    @KafkaListener(topics = {"${kafka.sls.consumer.topic}"}, containerFactory = "aliCloudLogServiceKafkaListenerFactory")
    public void hand(List<ConsumerRecord<String, String>> recList, Acknowledgment ack) {
        try {
            // 手动赋值tracerId、spanId
            TracerContextFactory.set(new LogLabelModel());
            if (CollectionUtil.isNotEmpty(recList)) {
                recList.forEach(rec -> {
                    try {
                        // 日志信息转换
                        AliCloudCdnLogModel cdnLogModel = JSONObject.parseObject(rec.value(), AliCloudCdnLogModel.class);

                        // RocketMq 使用sync模式同步发送消息，同时生成消费记录，避免消息丢失
                        MessageBuilder.syncSend(ConstantConfig.Topic.CDN_ACCESS_LOG_SERVICE,
                                ConstantConfig.Tag.CDN_ACCESS_LOG_RECORD, cdnLogModel, rocketmqTemplate,
                                consumerRecord -> rocketmqConsumerRecordService.insert(consumerRecord));
                    } catch (Exception e) {
                        log.error("consumer error，topic:{}，value:{}，errMsg:{}", rec.topic(), rec.value(), e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("consumer error，errMsg:{}", e.getMessage(), e);
        } finally {
            TracerContextFactory.clear();
            ack.acknowledge();
        }
    }
}
