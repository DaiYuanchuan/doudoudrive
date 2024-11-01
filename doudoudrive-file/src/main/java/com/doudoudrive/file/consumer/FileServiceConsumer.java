package com.doudoudrive.file.consumer;

import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.request.CopyFileConsumerRequestDTO;
import com.doudoudrive.common.model.dto.request.DeleteFileConsumerRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.rocketmq.InterceptorHook;
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
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>文件系统消费者服务</p>
 * <p>2022-05-25 20:50</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@RocketmqListener(topic = ConstantConfig.Topic.FILE_SERVICE, consumerGroup = ConstantConfig.ConsumerGroup.FILE)
public class FileServiceConsumer implements CommandLineRunner, Closeable, InterceptorHook {

    private GlobalThreadPoolService globalThreadPoolService;
    private MqConsumerRecordConvert consumerRecordConvert;
    private RocketmqConsumerRecordService rocketmqConsumerRecordService;
    private FileManager fileManager;
    private DiskUserAttrManager diskUserAttrManager;
    private DiskFileService diskFileService;
    private RocketMQTemplate rocketmqTemplate;

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
        // 出现异常时手动减去用户已用磁盘容量
        diskUserAttrManager.deducted(consumerRequest.getUserId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY, consumerRequest.getSize());
        // 手动删除用户文件(这里直接调用delete方法是因为会出现增加了磁盘容量但是文件不存在的情况，所以不需要做幂等处理)
        diskFileService.delete(consumerRequest.getFileId(), consumerRequest.getUserId());
    }

    /**
     * 删除文件消费处理
     *
     * @param consumerRequest 删除文件时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = DeleteFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.DELETE_FILE)
    public void deleteFileConsumer(DeleteFileConsumerRequestDTO consumerRequest, MessageContext messageContext) {
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
        copyHandler(consumerRequest.getTargetUserId(), consumerRequest.getFromUserId(), consumerRequest.getTargetFolderId(),
                consumerRequest.getTreeStructureMap(), consumerRequest.getPreCopyFileList(), totalDiskCapacity.stripTrailingZeros().toPlainString());
    }

    @Override
    public void run(String... args) {
        // 初始化线程池
        this.executor = TtlExecutors.getTtlExecutorService(ExecutorBuilder.create()
                .setCorePoolSize(NumberConstant.INTEGER_ONE)
                .setMaxPoolSize(NumberConstant.INTEGER_ONE)
                .setAllowCoreThreadTimeOut(false)
                .setWorkQueue(new LinkedBlockingQueue<>(NumberConstant.INTEGER_ONE))
                // 设置线程拒绝策略，丢弃队列中最旧的
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .setThreadFactory(new CustomizableThreadFactory("file-handler-thread"))
                .build());

        // 开启文件删除队列的处理程序
        this.executor.submit(this::deleteHandler);
    }

    @Override
    public void close() {
        this.shutdown();
    }

    /**
     * executor服务的销毁
     */
    private void shutdown() {
        if (executor != null) {
            executor.shutdown();
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
    private void copyHandler(String targetUserId, String fromUserId, String targetFolderId,
                             Map<String, String> treeStructureMap, List<String> parentId,
                             String totalDiskCapacity) {
        globalThreadPoolService.submit(ConstantConfig.ThreadPoolEnum.FILE_COPY_EXECUTOR, () ->
                fileManager.getAllFileInfo(fromUserId, parentId, queryParentIdResponse -> {
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
                }));
    }

    /**
     * 合并所有的子集，如果key相同，则将所有的文件信息合并到一起
     *
     * @param fileList 文件信息集合
     * @return 返回合并后的文件信息
     */
    private static Map<String, List<DiskFile>> getMergedMap(List<Map<String, List<DiskFile>>> fileList) {
        Map<String, List<DiskFile>> mergedMap = Maps.newHashMapWithExpectedSize(fileList.size());
        for (Map<String, List<DiskFile>> map : fileList) {
            // 遍历当前子集中的每个键值对
            for (Map.Entry<String, List<DiskFile>> entry : map.entrySet()) {
                mergedMap.compute(entry.getKey(), (key, list) -> {
                    if (CollectionUtil.isEmpty(list)) {
                        return new ArrayList<>(entry.getValue());
                    }
                    list.addAll(entry.getValue());
                    return list;
                });
            }
        }
        return mergedMap;
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
                Map<String, List<DiskFile>> mergedMap = getMergedMap(fileList);

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
            // 出现异常时，将文件信息重新放入队列中
            DeleteFileConsumerRequestDTO deleteFileConsumerRequest = DeleteFileConsumerRequestDTO.builder()
                    .userId(userId)
                    .businessId(content.stream().map(DiskFile::getBusinessId).toList())
                    .build();
            // 使用RocketMQ同步模式发送消息
            MessageBuilder.syncSend(ConstantConfig.Topic.FILE_SERVICE, ConstantConfig.Tag.DELETE_FILE, deleteFileConsumerRequest,
                    rocketmqTemplate, consumerRecord -> rocketmqConsumerRecordService.insert(consumerRecord));
        }
    }

    /**
     * 方法执行前的拦截
     *
     * @param body           消息体
     * @param messageContext 方法执行的参数
     */
    @Override
    public void preHandle(byte[] body, MessageContext messageContext) {
        // 构建消息消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.messageContextConvertConsumerRecord(messageContext, body);
        consumerRecord.setStatus(ConstantConfig.RocketmqConsumerStatusEnum.CONSUMING.getStatus());
        // 保存消息消费记录，保存失败时阻止消费方法的执行
        rocketmqConsumerRecordService.insertException(consumerRecord);
    }

    /**
     * 方法执行后的拦截
     *
     * @param methodSuccess  方法是否回调成功
     * @param body           消息体
     * @param messageContext 方法执行的参数
     */
    @Override
    public void nextHandle(boolean methodSuccess, byte[] body, MessageContext messageContext) {
        // 构建消息消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.messageContextConvertConsumerRecord(messageContext, null);
        // 根据方法执行结果设置消费记录的状态信息
        ConstantConfig.RocketmqConsumerStatusEnum status = methodSuccess
                ? ConstantConfig.RocketmqConsumerStatusEnum.COMPLETED
                : ConstantConfig.RocketmqConsumerStatusEnum.WAIT;
        // 更新消费记录状态信息
        rocketmqConsumerRecordService.updateConsumerStatus(consumerRecord.getBusinessId(), consumerRecord.getSendTime(), status);
    }
}
