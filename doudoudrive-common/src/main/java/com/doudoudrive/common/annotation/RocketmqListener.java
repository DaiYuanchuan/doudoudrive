package com.doudoudrive.common.annotation;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>RocketMQ消费者监听器</p>
 * <p>注解在类上，则这个类会被转换成为一个 DefaultMqPushConsumer</p>
 * <p>2022-03-12 23:56</p>
 *
 * @author Dan
 **/
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketmqListener {

    /**
     * 是否为顺序消息
     *
     * @return true：顺序消息 false：非顺序消息
     */
    boolean orderly() default false;

    /**
     * 转换为DefaultMqPushConsumer后订阅的topic
     */
    String topic();

    /**
     * 消息模式，默认为集群模式
     *
     * @see MessageModel
     */
    MessageModel messageModel() default MessageModel.CLUSTERING;

    /**
     * 消费者组
     */
    String consumerGroup();

    /**
     * 此消费者在消费时的最大线程数，如果在此处设置则使用此处设置的值
     * 否则统一使用配置文件中的值
     */
    int consumeThreadMax() default 0;

    /**
     * 此消费者在消费时的最小线程数，如果在此处设置则使用此处设置的值
     * 否则统一使用配置文件中的值
     */
    int consumeThreadMin() default 0;

}
