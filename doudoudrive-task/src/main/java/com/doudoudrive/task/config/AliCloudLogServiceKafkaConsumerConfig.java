package com.doudoudrive.task.config;

import com.doudoudrive.common.model.dto.model.aliyun.AliCloudLogServiceKafkaConsumerProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>阿里云CDN日志服务Kafka实时消费配置</p>
 * <p>2024-03-31 15:52</p>
 *
 * @author Dan
 **/
@EnableKafka
@Configuration
@EnableConfigurationProperties(value = {AliCloudLogServiceKafkaConsumerProperties.class})
public class AliCloudLogServiceKafkaConsumerConfig {

    private AliCloudLogServiceKafkaConsumerProperties properties;

    @Autowired
    public void setProperties(AliCloudLogServiceKafkaConsumerProperties properties) {
        this.properties = properties;
    }

    /**
     * Kafka listener 容器工厂
     *
     * @return the kafka listener container factory
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> aliCloudLogServiceKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(properties.getConcurrency());
        factory.getContainerProperties().setPollTimeout(properties.getPollTimeout());
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL);
        factory.setBatchListener(true);
        return factory;
    }

    /**
     * Consumer factory
     *
     * @return the consumer factory
     */
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    /**
     * Consumer configs map
     *
     * @return the map
     */
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getServers());
        propsMap.put("security.protocol", "sasl_ssl");
        propsMap.put("sasl.mechanism", "PLAIN");
        propsMap.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + properties.getProject() + "\" password=\"" + properties.getPassword() + "\";");

        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        propsMap.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, properties.getAutoCommitIntervalMs());

        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, properties.getSessionTimeoutMs());
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
        propsMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, properties.getMaxPollIntervalMs());
        propsMap.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, properties.getHeartbeatIntervalMs());
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.getMaxPollRecords());

        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return propsMap;
    }
}
