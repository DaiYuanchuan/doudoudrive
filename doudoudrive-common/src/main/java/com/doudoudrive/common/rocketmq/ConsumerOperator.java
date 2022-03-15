package com.doudoudrive.common.rocketmq;

/**
 * <p>消费者容器操作接口</p>
 * <p>2022-03-10 23:05</p>
 *
 * @author Dan
 **/
public interface ConsumerOperator {

    /**
     * 根据topic暂停某个消费者的消费
     *
     * @param topic 消费者的topic
     */
    void suspendConsumer(String topic);

    /**
     * 根据topic恢复某个消费者的消费
     *
     * @param topic 消费者topic
     */
    void resumeConsumer(String topic);

}
