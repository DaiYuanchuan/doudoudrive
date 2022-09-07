package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>RocketMQ消费记录数据访问层</p>
 * <p>2022-05-17 12:01</p>
 *
 * @author Dan
 **/
@Repository
public interface RocketmqConsumerRecordDao {

    /**
     * 新增RocketMQ消费记录
     *
     * @param rocketmqConsumerRecord 需要新增的RocketMQ消费记录实体
     * @param tableSuffix            表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("record") RocketmqConsumerRecord rocketmqConsumerRecord, @Param("tableSuffix") String tableSuffix);

    /**
     * 查找RocketMQ消费记录
     *
     * @param msgId       根据MQ消息唯一标识查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的RocketMQ消费记录实体
     */
    RocketmqConsumerRecord getRocketmqConsumerRecord(@Param("msgId") String msgId, @Param("tableSuffix") String tableSuffix);

}
