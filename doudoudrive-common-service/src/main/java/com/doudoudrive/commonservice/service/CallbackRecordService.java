package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.CallbackRecord;

/**
 * <p>外部系统回调记录服务层接口</p>
 * <p>2023-03-30 14:32:28</p>
 *
 * @author Dan
 */
public interface CallbackRecordService {

    /**
     * 新增外部系统回调记录
     *
     * @param callbackRecord 需要新增的外部系统回调记录实体
     */
    void insert(CallbackRecord callbackRecord);

    /**
     * 修改外部系统回调记录
     *
     * @param callbackRecord 需要进行修改的外部系统回调记录实体
     * @return 返回修改的条数
     */
    Integer update(CallbackRecord callbackRecord);

    /**
     * 将外部系统回调记录状态从等待修改为执行中
     *
     * @param businessId 回调记录业务id
     * @return true:修改成功 false:修改失败
     */
    Boolean updateStatusToExecute(String businessId);

    /**
     * 查找外部系统回调记录
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的外部系统回调记录实体
     */
    CallbackRecord getCallbackRecord(String businessId);
}

