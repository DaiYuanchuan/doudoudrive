package com.doudoudrive.file.manager.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.pojo.FileRecord;
import com.doudoudrive.commonservice.service.FileRecordService;
import com.doudoudrive.file.manager.FileRecordManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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

    private FileRecordService fileRecordService;

    @Autowired
    public void setFileRecordService(FileRecordService fileRecordService) {
        this.fileRecordService = fileRecordService;
    }

    /**
     * 批量新增文件操作记录
     *
     * @param list 需要新增的文件操作记录集合
     */
    @Override
    public void insertBatch(List<FileRecord> list) {
        fileRecordService.insertBatch(list);
    }

    /**
     * 获取指定状态的文件操作记录状态
     *
     * @param userId     指定用户
     * @param actionEnum 动作枚举
     * @param actionType 动作类型枚举
     * @return true:用户存在指定未完成的任务 false:不存在指定任务
     */
    @Override
    public Boolean getFileRecordByAction(String userId, ConstantConfig.FileRecordAction.ActionEnum actionEnum, ConstantConfig.FileRecordAction.ActionTypeEnum actionType) {
        // 获取指定状态的文件操作记录数据
        return fileRecordService.getFileRecordByAction(userId, actionEnum.status, actionType.status);
    }

    /**
     * 创建一条文件的任务记录
     *
     * @param userId     指定用户
     * @param actionType 任务类型枚举
     */
    @Override
    public void createTask(String userId, ConstantConfig.FileRecordAction.ActionTypeEnum actionType) {
        fileRecordService.insert(FileRecord.builder()
                .userId(userId)
                .action(ConstantConfig.FileRecordAction.ActionEnum.FILE.status)
                .actionType(actionType.status)
                .build());
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
    public void deleteAction(String userId, String etag,
                             ConstantConfig.FileRecordAction.ActionEnum actionEnum,
                             ConstantConfig.FileRecordAction.ActionTypeEnum actionType) {
        fileRecordService.deleteAction(userId, etag, actionEnum.status, actionType.status);
    }
}
