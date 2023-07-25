package com.doudoudrive.task.consumer;

import cn.hutool.http.HttpRequest;
import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.cache.timer.RedisDelayedQueue;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.RedisDelayedQueueEnum;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.pojo.CallbackRecord;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.commonservice.service.CallbackRecordService;
import com.doudoudrive.commonservice.service.GlobalThreadPoolService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * <p>延迟消息队列服务消费者</p>
 * <p>2023-06-06 22:48</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@RocketmqListener(topic = ConstantConfig.Topic.DELAY_MESSAGE_QUEUE_SERVICE, consumerGroup = ConstantConfig.ConsumerGroup.DELAY_MESSAGE_QUEUE)
public class DelayMessageQueueServiceConsumer {

    /**
     * 请求id，16位随机字符串，包含大小写
     */
    private static final String REQUEST_ID = "requestId";
    /**
     * 回调ua字符串
     */
    private static final String USER_AGENT_CALLBACK = "doudou-callback";
    /**
     * 设置超时时间，5000ms
     */
    private static final Integer TIMEOUT = NumberConstant.INTEGER_FIVE * NumberConstant.INTEGER_ONE_THOUSAND;
    private MqConsumerRecordConvert consumerRecordConvert;
    private RocketmqConsumerRecordService rocketmqConsumerRecordService;
    private CallbackRecordService callbackRecordService;
    private RedisDelayedQueue redisDelayedQueue;
    private GlobalThreadPoolService globalThreadPoolService;

    @Autowired(required = false)
    public void setConsumerRecordConvert(MqConsumerRecordConvert consumerRecordConvert) {
        this.consumerRecordConvert = consumerRecordConvert;
    }

    @Autowired
    public void setRocketmqConsumerRecordService(RocketmqConsumerRecordService rocketmqConsumerRecordService) {
        this.rocketmqConsumerRecordService = rocketmqConsumerRecordService;
    }

    @Autowired
    public void setCallbackRecordService(CallbackRecordService callbackRecordService) {
        this.callbackRecordService = callbackRecordService;
    }

    @Autowired
    public void setRedisDelayedQueue(RedisDelayedQueue redisDelayedQueue) {
        this.redisDelayedQueue = redisDelayedQueue;
    }

    @Autowired
    public void setGlobalThreadPoolService(GlobalThreadPoolService globalThreadPoolService) {
        this.globalThreadPoolService = globalThreadPoolService;
    }

    /**
     * 外部回调任务消费处理
     *
     * @param record         回调记录对象
     * @param messageContext mq消息内容
     */
    @RocketmqTagDistribution(messageClass = CallbackRecord.class, tag = ConstantConfig.Tag.EXTERNAL_CALLBACK_TASK)
    public void createFileConsumer(CallbackRecord record, MessageContext messageContext) {
        // 构建消息消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.messageContextConvertConsumerRecord(messageContext, null);

        try {
            // 保存消息消费记录
            rocketmqConsumerRecordService.insertException(consumerRecord);

            // 更新回调记录状态为执行中
            Boolean result = callbackRecordService.updateStatusToExecute(record.getBusinessId());
            if (!result) {
                return;
            }

            // 通过业务id获取回调记录
            CallbackRecord callbackRecord = callbackRecordService.getCallbackRecord(record.getBusinessId());

            // 构建回调地址初始化请求头配置Map
            Map<String, String> header = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_THREE);
            header.put(REQUEST_ID, record.getBusinessId());
            header.put(ConstantConfig.HttpRequest.USER_AGENT, USER_AGENT_CALLBACK);
            header.put(ConstantConfig.HttpRequest.HOST, URI.create(callbackRecord.getHttpUrl()).getHost());

            // 多线程异步发送回调请求
            globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.THIRD_PARTY_CALLBACK, () -> {
                record.setSendTime(LocalDateTime.now());

                // 构建回调请求
                long start = System.currentTimeMillis();
                try (cn.hutool.http.HttpResponse execute = HttpRequest.post(callbackRecord.getHttpUrl())
                        .headerMap(header, Boolean.TRUE)
                        .contentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON)
                        .charset(StandardCharsets.UTF_8)
                        .body(callbackRecord.getRequestBody().getBytes(StandardCharsets.UTF_8))
                        .timeout(TIMEOUT)
                        .execute()) {
                    record.setHttpStatus(String.valueOf(execute.getStatus()));
                    record.setResponseBody(execute.body());
                    record.setSendStatus(execute.isOk() ? ConstantConfig.CallbackStatusEnum.SUCCESS.getStatus() : ConstantConfig.CallbackStatusEnum.FAIL.getStatus());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    // 异常时，设置回调状态为失败，记录异常信息
                    record.setResponseBody(e.getMessage());
                    record.setSendStatus(ConstantConfig.CallbackStatusEnum.FAIL.getStatus());
                } finally {
                    record.setCostTime(System.currentTimeMillis() - start);
                    // 出现失败时，重试次数+1，扔到延迟队列中
                    if (ConstantConfig.CallbackStatusEnum.FAIL.getStatus().equals(record.getSendStatus())) {
                        // 设置重试次数
                        Integer retry = Optional.ofNullable(callbackRecord.getRetry()).orElse(NumberConstant.INTEGER_ZERO) + NumberConstant.INTEGER_ONE;
                        if (retry <= NumberConstant.INTEGER_THREE) {
                            record.setRetry(retry);
                            record.setSendStatus(ConstantConfig.CallbackStatusEnum.WAIT.getStatus());

                            // 获取消息重试级别
                            Optional.ofNullable(ConstantConfig.RetryLevelEnum.getLevel(record.getRetry())).ifPresent(level -> {
                                // 构建延迟消息体
                                CallbackRecord delayedMessage = CallbackRecord.builder()
                                        .businessId(callbackRecord.getBusinessId())
                                        .build();
                                // 扔到延迟队列中
                                redisDelayedQueue.offer(RedisDelayedQueueEnum.EXTERNAL_CALLBACK_TASK, level.getDelay(), level.getTimeUnit(), delayedMessage);
                            });
                        }
                    }
                    // 更新回调记录
                    callbackRecordService.update(record);
                }
            });
        } catch (Exception e) {
            log.error("errorMsg:{}，消费记录：{}", e.getMessage(), consumerRecord, e);
        }
    }
}
