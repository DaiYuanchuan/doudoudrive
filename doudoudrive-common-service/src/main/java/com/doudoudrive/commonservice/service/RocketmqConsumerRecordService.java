package com.doudoudrive.commonservice.service;

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
     * 查找RocketMQ消费记录
     *
     * @param businessId 根据业务id(businessId)查找
     * @param sendTime   消息发送、生产时间
     * @return 返回查找到的RocketMQ消费记录实体
     */
    RocketmqConsumerRecord getRocketmqConsumerRecord(String businessId, Date sendTime);

}