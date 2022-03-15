package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>RocketMQ相关配置参数属性</p>
 * <p>2022-03-10 22:36</p>
 *
 * @author Dan
 **/
@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "rocketmq")
public final class RocketMqProperties {

    /**
     * rocketMQ的 name server 地址，格式为：`host:port;host:port`
     */
    private String nameServer = "localhost:9876";

    /**
     * 消费者端最小线程数
     */
    private int consumeThreadMin = 20;

    /**
     * 消费者端最大线程数
     */
    private int consumeThreadMax = 64;

}
