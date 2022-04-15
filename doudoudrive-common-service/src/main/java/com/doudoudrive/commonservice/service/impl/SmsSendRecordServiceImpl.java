package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.SmsSendRecordDao;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>SMS发送记录服务层实现</p>
 * <p>2022-04-15 10:11</p>
 *
 * @author Dan
 **/
@Service("smsSendRecordService")
@DataSource(DataSourceEnum.LOG)
public class SmsSendRecordServiceImpl implements SmsSendRecordService {

    private SmsSendRecordDao smsSendRecordDao;

    @Autowired
    public void setSmsSendRecordDao(SmsSendRecordDao smsSendRecordDao) {
        this.smsSendRecordDao = smsSendRecordDao;
    }

    /**
     * 新增SMS发送记录
     *
     * @param smsSendRecord 需要新增的SMS发送记录实体
     * @return 插入成功时，返回一个入表时的后缀参数
     */
    @Override
    public SmsSendRecordModel insert(SmsSendRecord smsSendRecord) {
        if (ObjectUtils.isEmpty(smsSendRecord)) {
            return null;
        }
        if (StringUtils.isBlank(smsSendRecord.getBusinessId())) {
            smsSendRecord.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SMS_SEND_RECORD));
        }

        String tableSuffix = DateUtils.toMonth();
        Integer result = smsSendRecordDao.insert(smsSendRecord, tableSuffix);
        if (result > NumberConstant.INTEGER_ZERO) {
            return SmsSendRecordModel.builder()
                    .businessId(smsSendRecord.getBusinessId())
                    .tableSuffix(tableSuffix)
                    .build();
        }
        return null;
    }

    /**
     * 修改SMS发送记录
     *
     * @param smsSendRecord 需要进行修改的SMS发送记录实体
     * @param tableSuffix   表后缀
     * @return 返回修改的条数
     */
    @Override
    public Integer update(SmsSendRecord smsSendRecord, String tableSuffix) {
        if (ObjectUtils.isEmpty(smsSendRecord) || StringUtils.isBlank(smsSendRecord.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return smsSendRecordDao.update(smsSendRecord, tableSuffix);
    }

    /**
     * 查找SMS发送记录
     *
     * @param sendRecordModel SMS发送记录BO模型
     * @return 返回查找到的SMS发送记录实体
     */
    @Override
    public SmsSendRecord getSmsSendRecord(SmsSendRecordModel sendRecordModel) {
        return smsSendRecordDao.getSmsSendRecord(sendRecordModel.getBusinessId(), sendRecordModel.getTableSuffix());
    }
}
