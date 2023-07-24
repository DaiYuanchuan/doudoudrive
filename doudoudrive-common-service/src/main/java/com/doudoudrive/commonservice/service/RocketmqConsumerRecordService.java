package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.global.BusinessException;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;

import java.util.Date;

/**
 * <p>RocketMQ消费记录服务层接口</p>
 * <p>2022-05-17 14:11</p>
 *
 * @author Dan
 **/
public interface RocketmqConsumerRecordService {

    /**
     * 新增RocketMQ消费记录
     *
     * @param record 需要新增的RocketMQ消费记录实体
     */
    void insert(RocketmqConsumerRecord record);

    /**
     * 新增RocketMQ消费记录，新增失败会抛出异常
     *
     * @param record 需要新增的RocketMQ消费记录实体
     * @throws BusinessException 新增失败会抛出异常
     */
    void insertException(RocketmqConsumerRecord record) throws BusinessException;

    /**
     * 根据msgId删除RocketMQ消费记录
     *
     * @param record 需要删除的RocketMQ消费记录实体
     */
    void delete(RocketmqConsumerRecord record);

    /**
     * 根据msgId更改RocketMQ消费者记录状态为: 已消费
     *
     * @param msgId    根据MQ消息唯一标识查找
     * @param sendTime 消息发送、生产时间
     * @param status   消费记录的状态枚举，参考：{@link ConstantConfig.RocketmqConsumerStatusEnum}
     */
    void updateConsumerStatus(String msgId, Date sendTime, ConstantConfig.RocketmqConsumerStatusEnum status);

    /**
     * 查找RocketMQ消费记录
     *
     * @param msgId    根据MQ消息唯一标识查找
     * @param sendTime 消息发送、生产时间
     * @return 返回查找到的RocketMQ消费记录实体
     */
    RocketmqConsumerRecord getRocketmqConsumerRecord(String msgId, Date sendTime);

}
