package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessException;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.RocketmqConsumerRecordDao;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>RocketMQ消费记录服务层实现</p>
 * <p>2022-05-17 14:21</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
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
     * 新增RocketMQ消费记录，新增失败会抛出异常
     *
     * @param record 需要新增的RocketMQ消费记录实体
     */
    @Override
    public void insertException(RocketmqConsumerRecord record) throws BusinessException {
        // 先查找消费记录是否存在
        RocketmqConsumerRecord consumerRecord = this.getRocketmqConsumerRecord(record.getMsgId(), record.getSendTime());
        if (consumerRecord != null) {
            // 已经消费过，不再消费
            throw new BusinessException(StatusCodeEnum.ROCKETMQ_CONSUMER_RECORD_ALREADY_EXIST);
        }

        try {
            // 保存消息消费记录
            this.insert(record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(StatusCodeEnum.ROCKETMQ_CONSUMER_RECORD_ALREADY_EXIST);
        }
    }

    /**
     * 查找RocketMQ消费记录
     *
     * @param msgId    根据MQ消息唯一标识查找
     * @param sendTime 消息发送、生产时间
     * @return 返回查找到的RocketMQ消费记录实体
     */
    @Override
    public RocketmqConsumerRecord getRocketmqConsumerRecord(String msgId, Date sendTime) {
        return rocketmqConsumerRecordDao.getRocketmqConsumerRecord(msgId, DateUtils.toMonth(sendTime));
    }
}
