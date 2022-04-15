package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.pojo.SmsSendRecord;

/**
 * <p>SMS发送记录服务层接口</p>
 * <p>2022-04-15 10:04</p>
 *
 * @author Dan
 **/
public interface SmsSendRecordService {

    /**
     * 新增SMS发送记录
     *
     * @param smsSendRecord 需要新增的SMS发送记录实体
     * @return 插入成功时，返回一个入表时的后缀参数
     */
    SmsSendRecordModel insert(SmsSendRecord smsSendRecord);

    /**
     * 修改SMS发送记录
     *
     * @param smsSendRecord 需要进行修改的SMS发送记录实体
     * @param tableSuffix   表后缀
     * @return 返回修改的条数
     */
    Integer update(SmsSendRecord smsSendRecord, String tableSuffix);

    /**
     * 查找SMS发送记录
     *
     * @param sendRecordModel SMS发送记录BO模型
     * @return 返回查找到的SMS发送记录实体
     */
    SmsSendRecord getSmsSendRecord(SmsSendRecordModel sendRecordModel);

}
