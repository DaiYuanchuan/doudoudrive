package com.doudoudrive.file.manager;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.pojo.FileRecord;

import java.util.List;

/**
 * <p>用户文件操作记录服务的通用业务处理层接口</p>
 * <p>2022-09-07 23:32</p>
 *
 * @author Dan
 **/
public interface FileRecordManager {

    /**
     * 新增文件操作记录
     *
     * @param fileRecord 需要新增的文件操作记录实体
     */
    void insert(FileRecord fileRecord);

    /**
     * 批量新增文件操作记录
     *
     * @param list 需要新增的文件操作记录集合
     */
    void insertBatch(List<FileRecord> list);

    /**
     * 判断指定状态的文件操作记录是否存在
     *
     * @param userId     指定用户
     * @param actionEnum 动作枚举
     * @param actionType 任务类型枚举
     * @return true:用户存在指定未完成的任务 false:不存在指定任务
     */
    Boolean isFileRecordExist(String userId, ConstantConfig.FileRecordAction.ActionEnum actionEnum, ConstantConfig.FileRecordAction.ActionTypeEnum actionType);

    /**
     * 删除指定状态的文件操作记录数据
     *
     * @param userId     指定用户
     * @param etag       文件唯一标识
     * @param actionEnum 动作枚举
     * @param actionType 动作类型枚举
     */
    void deleteAction(String userId, String etag,
                      ConstantConfig.FileRecordAction.ActionEnum actionEnum,
                      ConstantConfig.FileRecordAction.ActionTypeEnum actionType);

    /**
     * 删除文件操作记录数据
     *
     * @param businessId 根据业务id(businessId)删除数据
     */
    void delete(String businessId);

    /**
     * 获取指定状态的文件操作记录数据
     *
     * @param userId     用户id
     * @param etag       文件唯一标识
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回指定状态的文件操作记录数据
     */
    FileRecord getFileRecordByAction(String userId, String etag, String action, String actionType);

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
    Integer updateFileRecordByAction(String businessId, String fromAction, String fromActionType, String toAction, String toActionType);
}
