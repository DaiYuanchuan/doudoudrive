package com.doudoudrive.task.job;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson2.JSON;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.MessageModel;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <p>MQ消费者记录相关定时任务处理程序（Bean模式）</p>
 * <p>2023-07-25 10:24</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class MqConsumerRecordJobHandler {

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

    /**
     * 每10分钟重发上次MQ消费失败的消息
     * 配置定时任务 ，每10分钟执行(cron = 0 0/10 * * * ?)
     *
     * @return 返回公共处理状态
     */
    @XxlJob(value = "resendMessageJobHandler")
    public ReturnT<String> resendMessageJobHandler() {
        // 打印执行日志
        log.info("resendMessageJobHandler start...");

        // 当前时间减去10分钟，获取10分钟前的时间
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minus(NumberConstant.INTEGER_TEN, ChronoUnit.MINUTES);

        // 格式化为yyyyMM字符串，用于查询消费记录表
        String tableSuffix = tenMinutesAgo.format(DatePattern.SIMPLE_MONTH_FORMATTER);

        // 获取前10分钟内，消费失败的消息
        List<RocketmqConsumerRecord> consumerRecordList = rocketmqConsumerRecordService.listResendMessage(tableSuffix);

        // 重发消息
        for (RocketmqConsumerRecord consumerRecord : consumerRecordList) {
            try {
                // 使用sync模式发送消息，保证消息发送成功
                String destination = consumerRecord.getTopic() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + consumerRecord.getTag();
                MessageModel message = JSON.parseObject(consumerRecord.getBody(), MessageModel.class);
                SendResult sendResult = rocketmqTemplate.syncSend(destination, MessageBuilder.build(message));
                // 重置消息发送状态
                consumerRecord.setSendStatus(ConstantConfig.MqMessageSendStatus.getStatusValue(sendResult.getSendStatus()));
                // 消息发送失败，重试次数+1
                if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                    consumerRecord.setRetryCount(consumerRecord.getRetryCount() + NumberConstant.INTEGER_ONE);
                }
            } catch (Exception e) {
                log.error("resendMessageJobHandler errorMsg: {}", e.getMessage(), e);
            }
        }

        // 批量修改消费记录
        rocketmqConsumerRecordService.updateBatch(consumerRecordList, tableSuffix);

        log.info("resendMessageJobHandler end...");
        return ReturnT.SUCCESS;
    }
}
