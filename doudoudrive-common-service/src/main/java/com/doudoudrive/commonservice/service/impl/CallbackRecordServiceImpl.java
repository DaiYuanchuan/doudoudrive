package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.CallbackRecord;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.CallbackRecordDao;
import com.doudoudrive.commonservice.service.CallbackRecordService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>外部系统回调记录服务层实现</p>
 * <p>2023-03-30 14:32:28</p>
 *
 * @author Dan
 */
@Service("callbackRecordService")
public class CallbackRecordServiceImpl implements CallbackRecordService {

    private CallbackRecordDao callbackRecordDao;

    @Autowired
    public void setCallbackRecordDao(CallbackRecordDao callbackRecordDao) {
        this.callbackRecordDao = callbackRecordDao;
    }

    /**
     * 新增外部系统回调记录
     *
     * @param callbackRecord 需要新增的外部系统回调记录实体
     */
    @Override
    public void insert(CallbackRecord callbackRecord) {
        if (ObjectUtils.isEmpty(callbackRecord)) {
            return;
        }
        if (StringUtils.isBlank(callbackRecord.getBusinessId())) {
            callbackRecord.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.CALLBACK_RECORD));
        }

        // 获取序列生成时间的月份
        String tableSuffix = SequenceUtil.generateTimeSuffix(callbackRecord.getBusinessId());
        if (StringUtils.isBlank(tableSuffix)) {
            return;
        }
        callbackRecordDao.insert(callbackRecord, tableSuffix);
    }

    /**
     * 修改外部系统回调记录
     *
     * @param callbackRecord 需要进行修改的外部系统回调记录实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(CallbackRecord callbackRecord) {
        if (ObjectUtils.isEmpty(callbackRecord) || StringUtils.isBlank(callbackRecord.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }

        // 获取序列生成时间的月份
        String tableSuffix = SequenceUtil.generateTimeSuffix(callbackRecord.getBusinessId());
        if (StringUtils.isBlank(tableSuffix)) {
            return NumberConstant.INTEGER_ZERO;
        }

        return callbackRecordDao.update(callbackRecord, tableSuffix);
    }

    /**
     * 将外部系统回调记录状态从等待修改为执行中
     *
     * @param businessId 回调记录业务id
     * @return true:修改成功 false:修改失败
     */
    @Override
    public Boolean updateStatusToExecute(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return Boolean.FALSE;
        }

        // 获取序列生成时间的月份
        String tableSuffix = SequenceUtil.generateTimeSuffix(businessId);
        if (StringUtils.isBlank(tableSuffix)) {
            return Boolean.FALSE;
        }

        // 更新回调记录状态为执行中
        Integer result = callbackRecordDao.updateStatusToExecute(businessId, tableSuffix);
        if (result == null || NumberConstant.INTEGER_ZERO.equals(result)) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * 查找外部系统回调记录
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的外部系统回调记录实体
     */
    @Override
    public CallbackRecord getCallbackRecord(String businessId) {
        // 获取序列生成时间的月份
        String tableSuffix = SequenceUtil.generateTimeSuffix(businessId);
        if (StringUtils.isBlank(tableSuffix)) {
            return null;
        }
        return callbackRecordDao.getCallbackRecord(businessId, tableSuffix);
    }
}
