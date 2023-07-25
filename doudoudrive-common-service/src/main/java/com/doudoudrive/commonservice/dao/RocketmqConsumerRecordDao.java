package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>RocketMQ消费记录数据访问层</p>
 * <p>2022-05-17 12:01</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.LOG)
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
     * 删除RocketMQ消费记录
     *
     * @param msgId       根据MQ消息唯一标识删除数据
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer delete(@Param("msgId") String msgId, @Param("tableSuffix") String tableSuffix);

    /**
     * 修改RocketMQ消费记录
     *
     * @param rocketmqConsumerRecord 需要进行修改的RocketMQ消费记录实体
     * @param tableSuffix            表后缀
     * @return 返回修改的条数
     */
    Integer update(@Param("rocketmqConsumerRecord") RocketmqConsumerRecord rocketmqConsumerRecord, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量修改RocketMQ消费记录
     *
     * @param list        需要进行修改的RocketMQ消费记录集合
     * @param tableSuffix 表后缀
     * @return 返回修改的条数
     */
    Integer updateBatch(@Param("list") List<RocketmqConsumerRecord> list,
                        @Param("tableSuffix") String tableSuffix);

    /**
     * 根据msgId更改RocketMQ消费者记录状态为: 已消费
     *
     * @param msgId       根据MQ消息唯一标识查找
     * @param status      消费记录的状态
     * @param tableSuffix 表后缀
     * @return 返回修改的条数
     */
    Integer updateConsumerStatus(@Param("msgId") String msgId,
                                 @Param("status") String status,
                                 @Param("tableSuffix") String tableSuffix);

    /**
     * 查找RocketMQ消费记录
     *
     * @param msgId       根据MQ消息唯一标识查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的RocketMQ消费记录实体
     */
    RocketmqConsumerRecord getRocketmqConsumerRecord(@Param("msgId") String msgId, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据状态信息批量查询RocketMQ消费记录数据，用于定时任务的重发消息
     *
     * @param tableSuffix 表后缀
     * @return 返回查找到的RocketMQ消费记录实体集合
     */
    List<RocketmqConsumerRecord> listResendMessage(@Param("tableSuffix") String tableSuffix);

}
