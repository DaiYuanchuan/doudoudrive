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
     * 批量新增文件操作记录
     *
     * @param list 需要新增的文件操作记录集合
     */
    void insertBatch(List<FileRecord> list);

    /**
     * 获取指定状态的文件操作记录状态
     *
     * @param userId     指定用户
     * @param actionEnum 动作枚举
     * @param actionType 任务类型枚举
     * @return true:用户存在指定未完成的任务 false:不存在指定任务
     */
    Boolean getFileRecordByAction(String userId, ConstantConfig.FileRecordAction.ActionEnum actionEnum, ConstantConfig.FileRecordAction.ActionTypeEnum actionType);

    /**
     * 创建一条文件的任务记录
     *
     * @param userId     指定用户
     * @param actionType 任务类型枚举
     */
    void createTask(String userId, ConstantConfig.FileRecordAction.ActionTypeEnum actionType);

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

}
