package com.doudoudrive.search.consumer;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.request.DeleteFileConsumerRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveFileConsumerRequestDTO;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.search.manager.DiskFileSearchManager;
import com.doudoudrive.search.model.convert.DiskFileModelConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    private DiskFileModelConvert diskFileModelConvert;

    @Autowired
    public void setDiskFileSearchManager(DiskFileSearchManager diskFileSearchManager) {
        this.diskFileSearchManager = diskFileSearchManager;
    }

    @Autowired(required = false)
    public void setDiskFileModelConvert(DiskFileModelConvert diskFileModelConvert) {
        this.diskFileModelConvert = diskFileModelConvert;
    }

    /**
     * 删除文件es数据的消费处理
     *
     * @param consumerRequest 删除文件时的消费者请求数据模型
     * @param messageContext  mq消息内容
     */
    @RocketmqTagDistribution(messageClass = DeleteFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.DELETE_FILE_ES)
    public void deleteFileConsumer(DeleteFileConsumerRequestDTO consumerRequest, MessageContext messageContext) {
        for (List<String> businessId : CollectionUtil.collectionCutting(consumerRequest.getBusinessId(), NumberConstant.LONG_TEN_THOUSAND)) {
            // 删除es中的数据
            diskFileSearchManager.deleteDiskFile(businessId);
        }
    }

    @RocketmqTagDistribution(messageClass = SaveFileConsumerRequestDTO.class, tag = ConstantConfig.Tag.SAVE_FILE_ES)
    public void deleteFileConsumer(SaveFileConsumerRequestDTO consumerRequest) {
        for (List<SaveElasticsearchDiskFileRequestDTO> fileInfo : CollectionUtil.collectionCutting(consumerRequest.getFileInfo(), NumberConstant.LONG_ONE_THOUSAND)) {
            diskFileSearchManager.saveDiskFile(diskFileModelConvert.saveElasticsearchDiskFileRequestConvertDiskFile(fileInfo));
        }
    }
}
