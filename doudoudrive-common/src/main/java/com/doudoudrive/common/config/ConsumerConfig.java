package com.doudoudrive.common.config;

import com.doudoudrive.common.model.dto.model.MqConfig;
import lombok.*;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.Map;

/**
 * <p>RocketMQ的consumer配置类，用于封装consumer的基本配置</p>
 * <p>2022-03-10 23:29</p>
 *
 * @author Dan
 **/
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class ConsumerConfig extends MqConfig {

    /**
     * 消费者topic
     */
    private String topic;

    /**
     * 消费者组
     */
    private String consumerGroup;

    /**
     * 消息模式
     *
     * @see MessageModel
     */
    private MessageModel messageModel;

    /**
     * 保存一个消费者订阅的topic下不同的tag以及tag对应的消息体类型
     */
    private Map<String, Class<?>> tags;

    /**
     * 一个消费者默认最小线程数
     */
    private int consumeThreadMin;
    /**
     * 一个消费者默认最大线程数
     */
    private int consumeThreadMax;


    public static ConsumerConfigBuilder builder() {
        return new ConsumerConfigBuilder();
    }

    /**
     * ConsumerConfig的建造者，方便构建不同的配置
     */
    public static class ConsumerConfigBuilder {

        private final ConsumerConfig consumerConfig = new ConsumerConfig();


        public ConsumerConfigBuilder messageClass(Class<?> messageClass) {
            this.consumerConfig.setMessageClass(messageClass);
            return this;
        }

        public ConsumerConfigBuilder topic(String topic) {
            this.consumerConfig.setTopic(topic);
            return this;
        }

        public ConsumerConfigBuilder orderlyMessage(boolean orderlyMessage) {
            this.consumerConfig.setOrderlyMessage(orderlyMessage);
            return this;
        }

        public ConsumerConfigBuilder consumerGroup(String consumerGroup) {
            this.consumerConfig.setConsumerGroup(consumerGroup);
            return this;
        }

        public ConsumerConfigBuilder messageModel(MessageModel messageModel) {
            this.consumerConfig.setMessageModel(messageModel);
            return this;
        }

        public ConsumerConfigBuilder consumeThreadMax(int consumeThreadMax) {
            this.consumerConfig.setConsumeThreadMax(consumeThreadMax);
            return this;
        }

        public ConsumerConfigBuilder consumeThreadMin(int consumeThreadMin) {
            this.consumerConfig.setConsumeThreadMin(consumeThreadMin);
            return this;
        }

        public ConsumerConfig build() {
            return this.consumerConfig;
        }
    }

}
