package com.doudoudrive.file.consumer;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.commonservice.service.GlobalThreadPoolService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFileCallbackRequestDTO;
import com.doudoudrive.file.model.dto.request.CreateFileConsumerRequestDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>文件系统消费者服务</p>
 * <p>2022-05-25 20:50</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@RocketmqListener(topic = ConstantConfig.Topic.FILE_SERVICE, consumerGroup = ConstantConfig.ConsumerGroup.FILE)
public class FileServiceConsumer {

    private DiskFileConvert diskFileConvert;

    /**
     * 用于外部回调的线程池服务
     */
    private GlobalThreadPoolService globalThreadPoolService;

    private MqConsumerRecordConvert consumerRecordConvert;

    private RocketmqConsumerRecordService rocketmqConsumerRecordService;

    @Autowired(required = false)
    public void setDiskFileConvert(DiskFileConvert diskFileConvert) {
        this.diskFileConvert = diskFileConvert;
    }

    @Autowired
    public void setGlobalThreadPoolService(GlobalThreadPoolService globalThreadPoolService) {
        this.globalThreadPoolService = globalThreadPoolService;
    }

    @Autowired(required = false)
    public void setConsumerRecordConvert(MqConsumerRecordConvert consumerRecordConvert) {
        this.consumerRecordConvert = consumerRecordConvert;
    }

    @Autowired
    public void setRocketmqConsumerRecordService(RocketmqConsumerRecordService rocketmqConsumerRecordService) {
        this.rocketmqConsumerRecordService = rocketmqConsumerRecordService;
    }

    /**
     * 请求id，16位随机字符串，包含大小写
     */
    private static final String REQUEST_ID = "requestId";

    /**
     * 常量 16
     */
    private static final Integer SIXTEEN = NumberConstant.INTEGER_TEN + NumberConstant.INTEGER_SIX;

    /**
     * 回调ua字符串
     */
    private static final String USER_AGENT_CALLBACK = "doudou-callback";

    /**
     * 设置超时时间，3000ms
     */
    private static final Integer TIMEOUT = NumberConstant.INTEGER_THREE * NumberConstant.INTEGER_ONE_THOUSAND;

    /**
     * 创建文件消费处理，当前消费者服务需要做幂等处理
     *
     * @param consumerRequest 创建文件时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = CreateFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.CREATE_FILE)
    public void createFileConsumer(CreateFileConsumerRequestDTO consumerRequest, MessageContext messageContext) {
        // 构建消息消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.messageContextConvertConsumerRecord(messageContext,
                ConstantConfig.Topic.FILE_SERVICE, ConstantConfig.Tag.CREATE_FILE);

        try {
            // 保存消息消费记录
            rocketmqConsumerRecordService.insertException(consumerRecord);

            CreateFileAuthModel fileInfo = consumerRequest.getFileInfo();

            String requestId = consumerRequest.getRequestId();
            if (StringUtils.isBlank(requestId)) {
                // 设置请求Id的默认值
                requestId = RandomUtil.randomString(SIXTEEN);
            }

            // 构建回调地址初始化请求头配置Map
            Map<String, String> header = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_THREE);
            header.put(REQUEST_ID, requestId);
            header.put(ConstantConfig.HttpRequest.USER_AGENT, USER_AGENT_CALLBACK);
            header.put(ConstantConfig.HttpRequest.HOST, URI.create(fileInfo.getCallbackUrl()).getHost());

            // 构建回调请求对象json串
            CreateFileCallbackRequestDTO fileCallbackRequest = diskFileConvert.ossFileConvertCreateFileCallbackRequest(fileInfo,
                    consumerRequest.getFileId(), consumerRequest.getPreview(), consumerRequest.getDownload());
            String body = JSON.toJSONString(fileCallbackRequest);

            // 多线程异步发送回调请求
            globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.THIRD_PARTY_CALLBACK, () -> {
                // 构建回调请求
                long start = System.currentTimeMillis();
                try (cn.hutool.http.HttpResponse execute = HttpRequest.post(fileInfo.getCallbackUrl())
                        .headerMap(header, Boolean.TRUE)
                        .contentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON)
                        .charset(StandardCharsets.UTF_8)
                        .body(body.getBytes(StandardCharsets.UTF_8))
                        .timeout(TIMEOUT)
                        .execute()) {
                    if (log.isDebugEnabled()) {
                        log.debug("callback request: {}ms {}\n{}", (System.currentTimeMillis() - start), fileInfo.getCallbackUrl(), body);
                        log.debug(execute.toString());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error("errorMsg:{}，消费记录：{}", e.getMessage(), consumerRecord, e);
        }
    }
}
