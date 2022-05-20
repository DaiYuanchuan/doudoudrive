package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.RocketmqConsumerRecordDao;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>RocketMQ消费记录服务层实现</p>
 * <p>2022-05-17 14:21</p>
 *
 * @author Dan
 **/
@DataSource(DataSourceEnum.LOG)
@Service("rocketmqConsumerRecordService")
public class RocketmqConsumerRecordServiceImpl implements RocketmqConsumerRecordService {

    private RocketmqConsumerRecordDao rocketmqConsumerRecordDao;

    @Autowired
    public void setRocketmqConsumerRecordDao(RocketmqConsumerRecordDao rocketmqConsumerRecordDao) {
        this.rocketmqConsumerRecordDao = rocketmqConsumerRecordDao;
    }

    /**
     * 新增RocketMQ消费记录
     *
     * @param record 需要新增的RocketMQ消费记录实体
     */
    @Override
    public void insert(RocketmqConsumerRecord record) {
        if (ObjectUtils.isEmpty(record) || record.getSendTime() == null) {
            return;
        }
        if (StringUtils.isBlank(record.getBusinessId())) {
            record.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.ROCKETMQ_CONSUMER_RECORD));
        }
        rocketmqConsumerRecordDao.insert(record, DateUtils.toMonth(record.getSendTime()));
    }

    /**
     * 查找RocketMQ消费记录
     *
     * @param businessId 根据业务id(businessId)查找
     * @param sendTime   消息发送、生产时间
     * @return 返回查找到的RocketMQ消费记录实体
     */
    @Override
    public RocketmqConsumerRecord getRocketmqConsumerRecord(String businessId, Date sendTime) {
        return rocketmqConsumerRecordDao.getRocketmqConsumerRecord(businessId, DateUtils.toMonth(sendTime));
    }
}
