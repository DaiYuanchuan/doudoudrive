package com.doudoudrive.file.manager.impl;

import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.request.CreateFileConsumerRequestDTO;
import com.doudoudrive.common.model.pojo.CallbackRecord;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.commonservice.service.CallbackRecordService;
import com.doudoudrive.file.manager.FileEventListener;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFileCallbackRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * <p>文件变更事件实现</p>
 * <p>2023-03-30 15:33</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("fileEventListener")
public class FileEventListenerImpl implements FileEventListener {

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;
    private CallbackRecordService callbackRecordService;
    private DiskFileConvert diskFileConvert;

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired
    public void setCallbackRecordService(CallbackRecordService callbackRecordService) {
        this.callbackRecordService = callbackRecordService;
    }

    @Autowired(required = false)
    public void setDiskFileConvert(DiskFileConvert diskFileConvert) {
        this.diskFileConvert = diskFileConvert;
    }

    /**
     * 回调地址最大长度500
     */
    private static final Integer MAX_CALLBACK_URL_LENGTH = NumberConstant.INTEGER_FIVE * NumberConstant.INTEGER_HUNDRED;

    /**
     * 创建文件
     *
     * @param consumerRequest 创建文件时的消费者请求数据模型
     */
    @Override
    public void create(CreateFileConsumerRequestDTO consumerRequest) {
        // 回调地址为空时，或者url链接长度达到最大值，不做处理
        if (StringUtils.isBlank(consumerRequest.getFileInfo().getCallbackUrl())
                || consumerRequest.getFileInfo().getCallbackUrl().length() > MAX_CALLBACK_URL_LENGTH) {
            return;
        }

        // 构建回调请求对象json串
        CreateFileCallbackRequestDTO fileCallbackRequest = diskFileConvert.ossFileConvertCreateFileCallbackRequest(consumerRequest.getFileInfo(),
                consumerRequest.getFileId(), consumerRequest.getPreview(), consumerRequest.getDownload());
        String body = JSON.toJSONString(fileCallbackRequest);

        // 构建回调记录信息
        CallbackRecord callbackRecord = CallbackRecord.builder()
                .httpUrl(consumerRequest.getFileInfo().getCallbackUrl())
                .requestBody(body)
                .retry(NumberConstant.INTEGER_ZERO)
                .sendStatus(ConstantConfig.CallbackStatusEnum.WAIT.getStatus())
                .build();
        callbackRecordService.insert(callbackRecord);

        // 使用one-way模式发送消息，外部回调任务处理
        String destination = ConstantConfig.Topic.DELAY_MESSAGE_QUEUE_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.EXTERNAL_CALLBACK_TASK;
        // 往MQ中发送的回调记录只需要业务id即可
        rocketmqTemplate.sendOneWay(destination, MessageBuilder.build(CallbackRecord.builder()
                .businessId(callbackRecord.getBusinessId())
                .build()));
    }

    @Override
    public void delete(DiskFile file) {
    }
}
