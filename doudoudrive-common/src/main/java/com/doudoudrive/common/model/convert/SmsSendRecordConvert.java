package com.doudoudrive.common.model.convert;

import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * <p>SMS发送记录信息等相关的实体数据类型转换器</p>
 * <p>2022-04-15 22:09</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SmsSendRecordConvert {

    /**
     * 将 SmsSendRecord(SMS发送记录) 类型转换为 SmsSendRecordModel(SMS发送记录BO模型)
     *
     * @param smsSendRecord SMS发送记录
     * @param tableSuffix   入表时的后缀参数
     * @return SMS发送记录BO模型
     */
    SmsSendRecordModel smsSendRecordConvert(SmsSendRecord smsSendRecord, String tableSuffix);

}
