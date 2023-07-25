package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.cache.lock.RedisLockManager;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.RedisLockEnum;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessException;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
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
import java.util.List;

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

    private RedisLockManager redisLockManager;

    @Autowired
    public void setRocketmqConsumerRecordDao(RocketmqConsumerRecordDao rocketmqConsumerRecordDao) {
        this.rocketmqConsumerRecordDao = rocketmqConsumerRecordDao;
    }

    @Autowired
    public void setRedisLockManager(RedisLockManager redisLockManager) {
        this.redisLockManager = redisLockManager;
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
        // 锁的名称，根据businessId生成
        String name = RedisLockEnum.MQ_CONSUMER_RECORD.getLockName() + record.getBusinessId();
        String lock = redisLockManager.lock(name);
        try {
            // 先查找消费记录是否存在
            RocketmqConsumerRecord consumerRecord = this.getRocketmqConsumerRecord(record.getBusinessId(), record.getSendTime());
            // 如果消费记录存在，且状态不是待消费的，说明在消费中、或者已完成消费，抛出异常
            if (consumerRecord != null && !ConstantConfig.RocketmqConsumerStatusEnum.WAIT.getStatus().equals(consumerRecord.getStatus())) {
                throw new BusinessException(StatusCodeEnum.ROCKETMQ_CONSUMER_RECORD_ALREADY_EXIST);
            }

            try {
                // 如果消费记录不存在，则新增消费记录
                if (consumerRecord == null) {
                    // 保存消息消费记录
                    this.insert(record);
                } else {
                    // 更新消费记录状态
                    rocketmqConsumerRecordDao.update(record, DateUtils.toMonth(record.getSendTime()));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new BusinessException(StatusCodeEnum.ROCKETMQ_CONSUMER_RECORD_ALREADY_EXIST);
            }
        } finally {
            redisLockManager.unlock(name, lock);
        }
    }

    /**
     * 根据businessId删除RocketMQ消费记录
     *
     * @param record 需要删除的RocketMQ消费记录实体
     */
    @Override
    public void delete(RocketmqConsumerRecord record) {
        if (record == null || StringUtils.isBlank(record.getBusinessId()) || record.getSendTime() == null) {
            return;
        }
        rocketmqConsumerRecordDao.delete(record.getBusinessId(), DateUtils.toMonth(record.getSendTime()));
    }

    /**
     * 批量修改RocketMQ消费记录信息
     *
     * @param list        需要进行修改的RocketMQ消费记录集合
     * @param tableSuffix 表后缀
     */
    @Override
    public void updateBatch(List<RocketmqConsumerRecord> list, String tableSuffix) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(consumerRecord -> {
            if (CollectionUtil.isNotEmpty(consumerRecord)) {
                rocketmqConsumerRecordDao.updateBatch(consumerRecord, tableSuffix);
            }
        });
    }

    /**
     * 根据businessId更改RocketMQ消费者记录状态为: 已消费
     *
     * @param businessId 根据消费记录的业务标识查找
     * @param sendTime   消息发送、生产时间
     * @param status     消费记录的状态枚举，参考：{@link ConstantConfig.RocketmqConsumerStatusEnum}
     */
    @Override
    public void updateConsumerStatus(String businessId, Date sendTime, ConstantConfig.RocketmqConsumerStatusEnum status) {
        if (StringUtils.isBlank(businessId) || sendTime == null || status == null) {
            return;
        }
        rocketmqConsumerRecordDao.updateConsumerStatus(businessId, status.getStatus(), DateUtils.toMonth(sendTime));
    }

    /**
     * 查找RocketMQ消费记录
     *
     * @param businessId 根据消费记录的业务标识查找
     * @param sendTime   消息发送、生产时间
     * @return 返回查找到的RocketMQ消费记录实体
     */
    @Override
    public RocketmqConsumerRecord getRocketmqConsumerRecord(String businessId, Date sendTime) {
        if (StringUtils.isBlank(businessId)) {
            return null;
        }
        return rocketmqConsumerRecordDao.getRocketmqConsumerRecord(businessId, DateUtils.toMonth(sendTime));
    }

    /**
     * 根据状态信息批量查询RocketMQ消费记录数据，用于定时任务的重发消息
     *
     * @param tableSuffix 表后缀
     * @return 返回查找到的RocketMQ消费记录实体集合
     */
    @Override
    public List<RocketmqConsumerRecord> listResendMessage(String tableSuffix) {
        return rocketmqConsumerRecordDao.listResendMessage(tableSuffix);
    }
}
