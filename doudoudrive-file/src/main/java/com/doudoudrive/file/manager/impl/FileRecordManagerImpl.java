package com.doudoudrive.file.manager.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.request.BatchSaveElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.request.DeleteElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchFileRecordResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.file.client.DiskFileSearchFeignClient;
import com.doudoudrive.file.manager.FileRecordManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>用户文件操作记录服务的通用业务处理层接口实现</p>
 * <p>2022-09-07 23:33</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("fileRecordManager")
public class FileRecordManagerImpl implements FileRecordManager {

    private DiskFileSearchFeignClient diskFileSearchFeignClient;

    @Autowired
    public void setDiskFileSearchFeignClient(DiskFileSearchFeignClient diskFileSearchFeignClient) {
        this.diskFileSearchFeignClient = diskFileSearchFeignClient;
    }

    /**
     * 新增文件操作记录
     *
     * @param fileRecordInfo 需要新增的文件操作记录实体
     */
    @Override
    public void insert(SaveElasticsearchFileRecordRequestDTO fileRecordInfo) {
        this.insertBatch(Collections.singletonList(fileRecordInfo));
    }

    /**
     * 批量新增文件操作记录
     *
     * @param fileRecordInfo 需要新增的文件操作记录集合
     */
    @Override
    public void insertBatch(List<SaveElasticsearchFileRecordRequestDTO> fileRecordInfo) {
        // 保存文件操作记录信息
        CollectionUtil.collectionCutting(fileRecordInfo, NumberConstant.LONG_ONE_THOUSAND)
                .forEach(fileRecord -> diskFileSearchFeignClient.saveFileRecord(BatchSaveElasticsearchFileRecordRequestDTO.builder()
                        .fileRecordInfo(fileRecord)
                        .build()));
    }

    /**
     * 判断指定状态的文件操作记录是否存在
     *
     * @param userId     指定用户
     * @param actionEnum 动作枚举
     * @param actionType 动作类型枚举
     * @return true:用户存在指定未完成的任务 false:不存在指定任务
     */
    @Override
    public Boolean isFileRecordExist(String userId, ConstantConfig.FileRecordAction.ActionEnum actionEnum, ConstantConfig.FileRecordAction.ActionTypeEnum actionType) {
        // 获取指定状态的文件操作记录数据
        return CollectionUtil.isNotEmpty(this.getFileRecordByAction(userId, null,
                actionEnum == null ? null : actionEnum.getStatus(), actionType == null ? null : actionType.getStatus()));
    }

    /**
     * 删除指定状态的文件操作记录数据
     *
     * @param userId     指定用户
     * @param etag       文件唯一标识
     * @param actionEnum 动作枚举
     * @param actionType 动作类型枚举
     */
    @Override
    public void deleteAction(String userId, List<String> etag,
                             ConstantConfig.FileRecordAction.ActionEnum actionEnum,
                             ConstantConfig.FileRecordAction.ActionTypeEnum actionType) {
        // 删除文件操作记录
        diskFileSearchFeignClient.deleteFileRecord(DeleteElasticsearchFileRecordRequestDTO.builder()
                .userId(userId)
                .action(actionEnum == null ? null : actionEnum.getStatus())
                .actionType(actionType == null ? null : actionType.getStatus())
                .etag(etag)
                .build());
    }

    /**
     * 获取指定状态的文件操作记录数据
     *
     * @param userId     用户id
     * @param etag       文件唯一标识
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回指定状态的文件操作记录数据
     */
    @Override
    public List<QueryElasticsearchFileRecordResponseDTO> getFileRecordByAction(String userId, String etag, String action, String actionType) {
        Result<List<QueryElasticsearchFileRecordResponseDTO>> result = diskFileSearchFeignClient.fileRecordSearch(QueryElasticsearchFileRecordRequestDTO.builder()
                .userId(userId)
                .action(action)
                .actionType(actionType)
                .etag(etag)
                .build());
        if (Result.isNotSuccess(result)) {
            return null;
        }
        return result.getData();
    }
}
