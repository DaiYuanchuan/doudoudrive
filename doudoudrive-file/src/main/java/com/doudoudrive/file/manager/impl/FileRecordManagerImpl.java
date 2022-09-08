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
        return fileRecordService.isFileRecordExist(userId, actionEnum.status, actionType.status);
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

    /**
     * 删除文件操作记录数据
     *
     * @param businessId 根据业务id(businessId)删除数据
     */
    @Override
    public void delete(String businessId) {
        fileRecordService.delete(businessId);
    }

    /**
     * 获取指定状态的文件操作记录数据
     *
     * @param userId     用户id
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回指定状态的文件操作记录数据
     */
    @Override
    public FileRecord getFileRecordByAction(String userId, String action, String actionType) {
        return fileRecordService.getFileRecordByAction(userId, action, actionType);
    }

    /**
     * 更新 指定动作类型 的文件操作记录的 动作类型
     *
     * @param businessId     文件操作记录系统内唯一标识
     * @param fromAction     原动作
     * @param fromActionType 原动作对应的动作类型
     * @param toAction       新动作
     * @param toActionType   新动作对应的动作类型
     * @return 返回更新的条数
     */
    @Override
    public Integer updateFileRecordByAction(String businessId, String fromAction, String fromActionType, String toAction, String toActionType) {
        return fileRecordService.updateFileRecordByAction(businessId, fromAction, fromActionType, toAction, toActionType);
    }
}
