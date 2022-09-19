package com.doudoudrive.search.consumer;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.request.DeleteFileConsumerRequestDTO;
import com.doudoudrive.search.manager.DiskFileSearchManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>文件搜索系统消费者服务</p>
 * <p>2022-09-19 23:27</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@RocketmqListener(topic = ConstantConfig.Topic.FILE_SEARCH_SERVICE, consumerGroup = ConstantConfig.ConsumerGroup.FILE_SEARCH)
public class DiskFileSearchConsumer {

    private DiskFileSearchManager diskFileSearchManager;

    @Autowired
    public void setDiskFileSearchManager(DiskFileSearchManager diskFileSearchManager) {
        this.diskFileSearchManager = diskFileSearchManager;
    }

    /**
     * 删除文件es数据的消费处理
     *
     * @param consumerRequest 删除文件时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = DeleteFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.DELETE_FILE_ES)
    public void deleteFileConsumer(DeleteFileConsumerRequestDTO consumerRequest, MessageContext messageContext) {
        // 删除es中的数据
        diskFileSearchManager.deleteDiskFile(consumerRequest.getBusinessId());
    }
}
