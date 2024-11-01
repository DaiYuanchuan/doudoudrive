package com.doudoudrive.log.controller;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.RedisDelayedQueueEnum;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.request.CreateMqConsumerRecordRequestDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.log.manager.MqConsumerRecordManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>RocketMQ消费记录信息服务控制层实现</p>
 * <p>2023-07-25 18:27</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/log/consumer-record")
public class MqConsumerRecordController {

    private MqConsumerRecordManager mqConsumerRecordManager;

    @Autowired
    public void setMqConsumerRecordManager(MqConsumerRecordManager mqConsumerRecordManager) {
        this.mqConsumerRecordManager = mqConsumerRecordManager;
    }

    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/create", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> createRecord(@RequestBody @Valid CreateMqConsumerRecordRequestDTO createConsumerRecordRequest,
                                       HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        RedisDelayedQueueEnum delayedQueueEnum;
        try {
            delayedQueueEnum = RedisDelayedQueueEnum.valueOf(createConsumerRecordRequest.getDelayedQueue());
        } catch (Exception e) {
            return Result.build(StatusCodeEnum.ENUM_TYPE_ERROR);
        }

        // 发送MQ消息，同时创建MQ消费者记录信息
        mqConsumerRecordManager.createConsumerRecord(delayedQueueEnum, createConsumerRecordRequest.getElement());
        return Result.ok();
    }
}
