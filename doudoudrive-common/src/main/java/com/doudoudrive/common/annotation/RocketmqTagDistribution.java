package com.doudoudrive.common.annotation;

import java.lang.annotation.*;

/**
 * <p>RocketMQ Tag 消息分发</p>
 * <p>注解用在消费端的方法上，用来处理同一topic中不同的tag类型的消息</p>
 * <p>2022-03-13 00:02</p>
 *
 * @author Dan
 **/
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RocketmqTagDistribution {

    /**
     * 订阅的tag
     */
    String tag() default "*";

    /**
     * 请求方消息类型，默认为字节数组
     */
    Class<?> messageClass() default byte[].class;

}
