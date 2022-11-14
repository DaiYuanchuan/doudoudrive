package com.doudoudrive.common.config;

import com.doudoudrive.common.model.dto.model.WorkerUdpProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;

/**
 * <p>udp 服务集成客户端配置</p>
 * <p>2022-11-12 22:21</p>
 *
 * @author Dan
 **/
@Configuration
public class UdpIntegrationClientConfig {

    /**
     * 获取配置类中worker模块的udp通信参数配置
     *
     * @return {@link WorkerUdpProperties} 日志工蜂模块udp通信参数配置
     */
    @Bean
    @ConfigurationProperties(prefix = "worker.udp")
    public WorkerUdpProperties getWorkerUdpProperties() {
        return WorkerUdpProperties.builder().build();
    }

    /**
     * 单播发送udp消息处理配置
     *
     * @return {@link UnicastSendingMessageHandler}
     */
    @Bean
    public UnicastSendingMessageHandler sending() {
        WorkerUdpProperties udpProperties = getWorkerUdpProperties();
        return new UnicastSendingMessageHandler(udpProperties.getServer(), udpProperties.getPort(), udpProperties.getLengthCheck());
    }
}
