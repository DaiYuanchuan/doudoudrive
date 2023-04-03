package com.doudoudrive.file.consumer;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.request.CopyFileConsumerRequestDTO;
import com.doudoudrive.common.model.dto.request.DeleteFileConsumerRequestDTO;
import com.doudoudrive.common.model.pojo.CallbackRecord;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.commonservice.service.CallbackRecordService;
import com.doudoudrive.commonservice.service.DiskFileService;
import com.doudoudrive.commonservice.service.GlobalThreadPoolService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.file.manager.DiskUserAttrManager;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFileCallbackRequestDTO;
import com.doudoudrive.file.model.dto.request.CreateFileConsumerRequestDTO;
import com.doudoudrive.file.model.dto.request.CreateFileRollbackConsumerRequestDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private FileManager fileManager;

    private DiskUserAttrManager diskUserAttrManager;

    private DiskFileService diskFileService;

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;

    private CallbackRecordService callbackRecordService;

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

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setDiskUserAttrManager(DiskUserAttrManager diskUserAttrManager) {
        this.diskUserAttrManager = diskUserAttrManager;
    }

    @Autowired
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired
    public void setCallbackRecordService(CallbackRecordService callbackRecordService) {
        this.callbackRecordService = callbackRecordService;
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

            // 获取回调记录信息
            CallbackRecord record = CallbackRecord.builder()
                    .businessId(consumerRequest.getCallbackRecordId())
                    .sendStatus(ConstantConfig.CallbackStatusEnum.EXECUTING.getStatus())
                    .build();
            // 更新回调记录状态为执行中
            Integer result = callbackRecordService.update(record);
            if (result == null || NumberConstant.INTEGER_ZERO.equals(result)) {
                return;
            }

            CreateFileAuthModel fileInfo = consumerRequest.getFileInfo();

            // 构建回调地址初始化请求头配置Map
            Map<String, String> header = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_THREE);
            header.put(REQUEST_ID, consumerRequest.getCallbackRecordId());
            header.put(ConstantConfig.HttpRequest.USER_AGENT, USER_AGENT_CALLBACK);
            header.put(ConstantConfig.HttpRequest.HOST, URI.create(fileInfo.getCallbackUrl()).getHost());

            // 构建回调请求对象json串
            CreateFileCallbackRequestDTO fileCallbackRequest = diskFileConvert.ossFileConvertCreateFileCallbackRequest(fileInfo,
                    consumerRequest.getFileId(), consumerRequest.getPreview(), consumerRequest.getDownload());
            String body = JSON.toJSONString(fileCallbackRequest);

            // 多线程异步发送回调请求
            globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.THIRD_PARTY_CALLBACK, () -> {
                record.setRequestBody(body);
                record.setSendTime(LocalDateTime.now());

                // 构建回调请求
                long start = System.currentTimeMillis();
                try (cn.hutool.http.HttpResponse execute = HttpRequest.post(fileInfo.getCallbackUrl())
                        .headerMap(header, Boolean.TRUE)
                        .contentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON)
                        .charset(StandardCharsets.UTF_8)
                        .body(body.getBytes(StandardCharsets.UTF_8))
                        .timeout(TIMEOUT)
                        .execute()) {
                    record.setHttpStatus(String.valueOf(execute.getStatus()));
                    record.setResponseBody(execute.body());
                    record.setSendStatus(execute.isOk() ? ConstantConfig.CallbackStatusEnum.SUCCESS.getStatus() : ConstantConfig.CallbackStatusEnum.FAIL.getStatus());
                    if (log.isDebugEnabled()) {
                        log.debug("callback request: {}ms {}\n{}", (System.currentTimeMillis() - start), fileInfo.getCallbackUrl(), body);
                        log.debug(execute.toString());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    // 出现失败时，重试次数+1，扔到延迟队列中
                    if (ConstantConfig.CallbackStatusEnum.FAIL.getStatus().equals(record.getSendStatus())) {
                        // 设置重试次数
                        Optional.ofNullable(callbackRecordService.getCallbackRecord(consumerRequest.getCallbackRecordId())).ifPresent(callbackRecord -> {
                            Integer retry = Optional.ofNullable(callbackRecord.getRetry()).orElse(NumberConstant.INTEGER_ZERO);
                            if (retry <= NumberConstant.INTEGER_THREE) {
                                record.setRetry(retry + NumberConstant.INTEGER_ONE);
                                record.setSendStatus(ConstantConfig.CallbackStatusEnum.WAIT.getStatus());
                                String destination = consumerRecord.getTopic() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + consumerRecord.getTag();
                                // 获取消息重试级别
                                Integer level = ConstantConfig.RetryLevelEnum.getLevel(record.getRetry());
                                // 超时时间
                                final int timeout = NumberConstant.INTEGER_SIX * NumberConstant.INTEGER_TEN_THOUSAND;
                                // 发送MQ延迟消息
                                rocketmqTemplate.syncSend(destination, org.springframework.messaging.support.MessageBuilder
                                        .withPayload(MessageBuilder.build(consumerRequest)).build(), timeout, level);
                            }
                        });
                    }
                    // 更新回调记录
                    callbackRecordService.update(record);
                }
            });
        } catch (Exception e) {
            log.error("errorMsg:{}，消费记录：{}", e.getMessage(), consumerRecord, e);
        }
    }

    /**
     * 创建文件失败时异步回滚的消费处理，当前消费者服务需要做幂等处理
     *
     * @param consumerRequest 创建文件失败时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = CreateFileRollbackConsumerRequestDTO.class, tag = ConstantConfig.Tag.CREATE_FILE_ROLLBACK)
    public void createFileRollbackConsumer(CreateFileRollbackConsumerRequestDTO consumerRequest, MessageContext messageContext) {
        // 构建消息消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.messageContextConvertConsumerRecord(messageContext,
                ConstantConfig.Topic.FILE_SERVICE, ConstantConfig.Tag.CREATE_FILE_ROLLBACK);
        try {
            // 保存消息消费记录
            consumerRecord.setRetryCount(consumerRequest.getRetryCount());
            rocketmqConsumerRecordService.insertException(consumerRecord);
        } catch (Exception e) {
            // 重复消费拦截
            log.error("errorMsg:{}，消费记录：{}", e.getMessage(), consumerRecord, e);
            return;
        }

        // 重试次数超过3次，不再重试
        if (consumerRequest.getRetryCount() > NumberConstant.INTEGER_THREE) {
            return;
        }

        try {
            // 出现异常时手动减去用户已用磁盘容量
            diskUserAttrManager.deducted(consumerRequest.getUserId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY, consumerRequest.getSize());
            // 手动删除用户文件(这里直接调用delete方法是因为会出现增加了磁盘容量但是文件不存在的情况，所以不需要做幂等处理)
            diskFileService.delete(consumerRequest.getFileId(), consumerRequest.getUserId());
        } catch (Exception e) {
            // 重试次数加1
            consumerRequest.setRetryCount(consumerRequest.getRetryCount() + NumberConstant.INTEGER_ONE);
            // 回滚失败时将本次回滚失败的消息重新放入队列中重试
            String destination = ConstantConfig.Topic.FILE_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.CREATE_FILE_ROLLBACK;
            // 使用sync模式发送消息，保证消息发送成功
            SendResult sendResult = rocketmqTemplate.syncSend(destination, MessageBuilder.build(consumerRequest));
            // 判断消息是否发送成功
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("send to mq, destination:{}, msgId:{}, sendStatus:{}, errorMsg:{}, sendResult:{}, fileId:{}",
                        destination, sendResult.getMsgId(), sendResult.getSendStatus(), e.getMessage(), sendResult, consumerRequest.getFileId());
            }
        }
    }

    /**
     * 删除文件消费处理
     *
     * @param consumerRequest 删除文件时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = DeleteFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.DELETE_FILE)
    public void deleteFileConsumer(DeleteFileConsumerRequestDTO consumerRequest, MessageContext messageContext) {
        globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.GLOBAL_THREAD_POOL, () -> {
            // 根据传入的文件业务标识查找是否存在对应的文件信息
            List<DiskFile> fileIdSearchResult = fileManager.fileIdSearch(consumerRequest.getUserId(), consumerRequest.getBusinessId());
            // 其中所有的文件夹信息集合
            List<String> fileFolderList = fileIdSearchResult.stream()
                    .filter(DiskFile::getFileFolder)
                    .map(DiskFile::getBusinessId).toList();

            // 如果存在文件夹信息，则需要删除文件夹信息
            if (CollectionUtil.isNotEmpty(fileFolderList)) {
                consumerRequest.getBusinessId().addAll(fileFolderList);
            }

            // 如果消息中包含有文件信息，则需要先删除文件信息
            if (CollectionUtil.isNotEmpty(fileIdSearchResult)) {
                deleteHandler(fileIdSearchResult, consumerRequest.getUserId());
            }

            // 递归获取指定文件节点下所有的子节点信息
            fileManager.getUserFileAllNode(consumerRequest.getUserId(), consumerRequest.getBusinessId(),
                    queryParentIdResponse -> deleteHandler(queryParentIdResponse, consumerRequest.getUserId()));
        });
    }

    /**
     * 复制文件信息消费处理
     *
     * @param consumerRequest 删除文件时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = CopyFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.COPY_FILE)
    public void copyFileConsumer(CopyFileConsumerRequestDTO consumerRequest, MessageContext messageContext) {
        // 用户总磁盘容量
        BigDecimal totalDiskCapacity = diskUserAttrManager.getUserAttrValue(consumerRequest.getTargetUserId(), ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY);
        globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.GLOBAL_THREAD_POOL, () -> copyHandler(consumerRequest.getTargetUserId(), consumerRequest.getFromUserId(),
                consumerRequest.getTargetFolderId(), consumerRequest.getTreeStructureMap(), consumerRequest.getPreCopyFileList(), totalDiskCapacity.stripTrailingZeros().toPlainString()));
    }

    /**
     * 删除文件消费处理程序
     *
     * @param content 文件内容
     * @param userId  用户Id
     */
    private void deleteHandler(List<DiskFile> content, String userId) {
        try {
            // 根据文件id批量删除文件或文件夹
            fileManager.delete(content, userId);
        } catch (Exception e) {
            // 删除文件失败时将本次删除失败的文件消息重新放入队列中，使用sync模式发送消息，保证消息发送成功
            String destination = ConstantConfig.Topic.FILE_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.DELETE_FILE;
            SendResult sendResult = rocketmqTemplate.syncSend(destination, MessageBuilder.build(DeleteFileConsumerRequestDTO.builder()
                    .userId(userId)
                    .businessId(content.stream().map(DiskFile::getBusinessId).toList())
                    .build()));
            // 消息发送失败，打印错误日志
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                log.error("send to mq, destination:{}, msgId:{}, sendStatus:{}, contentSize:{}, errorMsg:{}, sendResult:{}",
                        destination, sendResult.getMsgId(), sendResult.getSendStatus(), content.size(), e.getMessage(), sendResult);
            }
        }
    }

    /**
     * 复制文件消费处理程序
     *
     * @param targetUserId      目标用户Id
     * @param fromUserId        源用户Id
     * @param targetFolderId    目标文件夹Id
     * @param treeStructureMap  文件树结构
     * @param parentId          父节点Id，需要复制的文件夹标识
     * @param totalDiskCapacity 用户总磁盘容量
     */
    private void copyHandler(String targetUserId, String fromUserId, String targetFolderId, Map<String, String> treeStructureMap, List<String> parentId, String totalDiskCapacity) {
        fileManager.getAllFileInfo(null, fromUserId, parentId, queryParentIdResponse -> {
            // 获取查询结果中的所有文件夹标识
            List<String> parentFileList = queryParentIdResponse.stream()
                    .filter(DiskFile::getFileFolder)
                    .map(DiskFile::getBusinessId).toList();

            // 批量复制文件信息
            Map<String, String> nodeMap = fileManager.batchCopyFile(targetUserId, targetFolderId, treeStructureMap, queryParentIdResponse, totalDiskCapacity);

            if (CollectionUtil.isNotEmpty(parentFileList)) {
                // 存在有文件夹时，继续递归查询
                this.copyHandler(targetUserId, fromUserId, targetFolderId, nodeMap, parentFileList, totalDiskCapacity);
            }
        });
    }
}
