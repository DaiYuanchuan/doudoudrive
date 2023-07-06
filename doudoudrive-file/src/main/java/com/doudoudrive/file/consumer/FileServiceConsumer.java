package com.doudoudrive.file.consumer;

import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.cache.RedisTemplateClient;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.request.CopyFileConsumerRequestDTO;
import com.doudoudrive.common.model.dto.request.DeleteFileConsumerRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.commonservice.service.DiskFileService;
import com.doudoudrive.commonservice.service.GlobalThreadPoolService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.file.manager.DiskUserAttrManager;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.model.dto.request.CreateFileRollbackConsumerRequestDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>文件系统消费者服务</p>
 * <p>2022-05-25 20:50</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@RocketmqListener(topic = ConstantConfig.Topic.FILE_SERVICE, consumerGroup = ConstantConfig.ConsumerGroup.FILE)
public class FileServiceConsumer implements CommandLineRunner, Closeable {

    private GlobalThreadPoolService globalThreadPoolService;
    private MqConsumerRecordConvert consumerRecordConvert;
    private RocketmqConsumerRecordService rocketmqConsumerRecordService;
    private FileManager fileManager;
    private DiskUserAttrManager diskUserAttrManager;
    private DiskFileService diskFileService;
    private RocketMQTemplate rocketmqTemplate;
    /**
     * 文件复制时使用的无界队列，用于异步处理文件复制
     */
    private static final BlockingQueue<Map<String, CopyFileConsumerRequestDTO>> FILE_COPY_UNBOUNDED_QUEUE = new LinkedBlockingQueue<>();

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
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }
    private RedisTemplateClient redisTemplateClient;
    /**
     * 文件删除时使用的无界队列，用于异步处理文件删除
     */
    private static final BlockingQueue<Map<String, List<DiskFile>>> FILE_DELETE_UNBOUNDED_QUEUE = new LinkedBlockingQueue<>();
    /**
     * 单线程调度执行器，用于异步推送文件复制和删除的队列
     */
    private ExecutorService executor;

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
        // 构建消息消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.messageContextConvertConsumerRecord(messageContext,
                ConstantConfig.Topic.FILE_SERVICE, ConstantConfig.Tag.DELETE_FILE);

        try {
            // 保存消息消费记录
            rocketmqConsumerRecordService.insertException(consumerRecord);

            // 使用文件复制线程池异步处理文件复制
            globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.FILE_DELETE_EXECUTOR, () -> {
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
                    fileDelete(fileIdSearchResult, consumerRequest.getUserId());
                }

                // 递归获取指定文件节点下所有的子节点信息
                fileManager.getUserFileAllNode(consumerRequest.getUserId(), consumerRequest.getBusinessId(), queryParentIdResponse -> {
                    try {
                        // 创建一个新的map集合
                        Map<String, List<DiskFile>> queueMap = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
                        queueMap.put(consumerRequest.getUserId(), queryParentIdResponse);

                        // 将map集合放入队列中
                        FILE_DELETE_UNBOUNDED_QUEUE.put(queueMap);
                    } catch (Exception e) {
                        log.error("put delete queue error, errorMsg:{}", e.getMessage(), e);
                    }
                });
            });
        } catch (Exception e) {
            // 重复消费拦截
            log.error("errorMsg:{}，消费记录：{}", e.getMessage(), consumerRecord, e);
        }
    }

    /**
     * 复制文件信息消费处理
     *
     * @param consumerRequest 删除文件时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = CopyFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.COPY_FILE)
    public void copyFileConsumer(CopyFileConsumerRequestDTO consumerRequest, MessageContext messageContext) {
        // 构建消息消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.messageContextConvertConsumerRecord(messageContext,
                ConstantConfig.Topic.FILE_SERVICE, ConstantConfig.Tag.COPY_FILE);

        try {
            // 保存消息消费记录
            rocketmqConsumerRecordService.insertException(consumerRecord);

            // 使用文件复制线程池异步处理文件复制
            globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.FILE_COPY_EXECUTOR, () -> {
                // 构建缓存key
                String cacheKey = ConstantConfig.Cache.FILE_COPY_NODE_CACHE + consumerRecord.getMsgId();

                // 获取指定文件节点下所有的子节点信息
                fileManager.getUserFileAllNode(consumerRequest.getFromUserId(), consumerRequest.getPreCopyFileList(), queryParentIdResponse -> {
                    try {
                        // 将查询到的文件信息放入队列中
                        consumerRequest.setFiles(queryParentIdResponse);

                        // 创建一个新的map集合
                        Map<String, CopyFileConsumerRequestDTO> queueMap = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
                        queueMap.put(consumerRecord.getMsgId(), consumerRequest);

                        // 将map集合放入队列中
                        FILE_COPY_UNBOUNDED_QUEUE.put(queueMap);
                    } catch (Exception e) {
                        log.error("put copy queue error, errorMsg:{}", e.getMessage(), e);
                    }
                });

                // 循环结束后，清空缓存信息
                redisTemplateClient.delete(cacheKey);
            });
        } catch (Exception e) {
            // 重复消费拦截
            log.error("errorMsg:{}，消费记录：{}", e.getMessage(), consumerRecord, e);
        }
    }

    @Override
    public void run(String... args) {
        this.shutdown(Boolean.TRUE);

        // 初始化线程池
        this.executor = TtlExecutors.getTtlExecutorService(ExecutorBuilder.create()
                .setCorePoolSize(NumberConstant.INTEGER_TWO)
                .setMaxPoolSize(NumberConstant.INTEGER_TWO)
                .setAllowCoreThreadTimeOut(false)
                .setWorkQueue(new LinkedBlockingQueue<>(NumberConstant.INTEGER_TWO))
                // 设置线程拒绝策略，丢弃队列中最旧的
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setThreadFactory(new CustomizableThreadFactory("file-handler-thread"))
                .build());

        // 开启文件复制、删除队列的处理程序
        this.executor.submit(this::copyHandler);
        this.executor.submit(this::deleteHandler);
    }

    @Override
    public void close() {
        this.shutdown(Boolean.FALSE);
    }

    /**
     * executor服务的销毁
     *
     * @param now 是否立即销毁
     */
    private void shutdown(boolean now) {
        if (executor != null) {
            if (now) {
                executor.shutdownNow();
            } else {
                executor.shutdown();
            }
        }
    }

    /**
     * 文件复制队列的处理程序，用于消费队列中的数据
     */
    private void copyHandler() {
        while (null != executor && !executor.isShutdown()) {
            try {
                // 从队列中取出指定数量的数据
                List<Map<String, CopyFileConsumerRequestDTO>> fileList = CollectionUtil.pollBatchOrWait(FILE_COPY_UNBOUNDED_QUEUE,
                        NumberConstant.INTEGER_ONE_THOUSAND, NumberConstant.LONG_ONE, TimeUnit.SECONDS);
                if (CollectionUtil.isEmpty(fileList)) {
                    continue;
                }

                // 合并所有的子集，如果key相同，则将所有需要进行复制的文件信息合并到一起
                Map<String, CopyFileConsumerRequestDTO> mergedMap = fileList.stream()
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (key1, key2) -> {
                                    key1.getFiles().addAll(key2.getFiles());
                                    return key1;
                                }
                        ));

                // 用于保存文件树结构的map
                mergedMap.forEach((msgId, consumerRequest) -> {
                    try {
                        // 构建缓存key
                        String cacheKey = ConstantConfig.Cache.FILE_COPY_NODE_CACHE + msgId;
                        // 获取缓存中的文件树结构
                        Map<String, String> fileCopyNodeMap = convertFileNodeMap(redisTemplateClient.entries(cacheKey), consumerRequest.getTreeStructureMap());
                        // 用户总磁盘容量
                        BigDecimal totalDiskCapacity = diskUserAttrManager.getUserAttrValue(consumerRequest.getTargetUserId(), ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY);

                        // 批量复制文件信息
                        Map<String, String> nodeMap = fileManager.batchCopyFile(consumerRequest.getTargetUserId(), consumerRequest.getTargetFolderId(),
                                fileCopyNodeMap, consumerRequest.getFiles(), totalDiskCapacity.stripTrailingZeros().toPlainString());

                        // 先删除之前的缓存
                        redisTemplateClient.delete(cacheKey);
                        // 将复制后的文件树结构放入缓存中
                        redisTemplateClient.putAll(cacheKey, nodeMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    } catch (Exception e) {
                        log.error("copyHandler error, errorMsg:{}", e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 删除文件队列处理程序，用于消费队列中的数据
     */
    private void deleteHandler() {
        while (null != executor && !executor.isShutdown()) {
            try {
                // 从队列中取出指定数量的数据
                List<Map<String, List<DiskFile>>> fileList = CollectionUtil.pollBatchOrWait(FILE_DELETE_UNBOUNDED_QUEUE,
                        NumberConstant.INTEGER_ONE_THOUSAND, NumberConstant.LONG_ONE, TimeUnit.SECONDS);
                if (CollectionUtil.isEmpty(fileList)) {
                    continue;
                }

                // 合并所有的子集，如果key相同，则将所有的文件信息合并到一起
                Map<String, List<DiskFile>> mergedMap = fileList.stream()
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (key1, key2) -> {
                                    key1.addAll(key2);
                                    return key1;
                                }
                        ));

                // 批量删除文件信息
                mergedMap.forEach((userId, content) -> fileDelete(content, userId));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 文件删除队列处理
     *
     * @param content 文件内容
     * @param userId  用户Id
     */
    private void fileDelete(List<DiskFile> content, String userId) {
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
     * 将缓存中的文件树结构转换为Map<String, String>类型，如果缓存中不存在文件树结构，则将当前的文件树结构放入缓存中
     *
     * @param fileCopyNodeMap  缓存中的文件树结构
     * @param treeStructureMap 当前的文件树结构
     * @return Map<String, String>
     */
    private Map<String, String> convertFileNodeMap(Map<Object, Object> fileCopyNodeMap, Map<String, String> treeStructureMap) {
        // 如果缓存中不存在文件树结构，则将当前的文件树结构放入缓存中
        if (CollectionUtil.isEmpty(fileCopyNodeMap)) {
            return treeStructureMap;
        }
        return fileCopyNodeMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> String.valueOf(entry.getKey()),
                        entry -> String.valueOf(entry.getValue())
                ));
    }
}
