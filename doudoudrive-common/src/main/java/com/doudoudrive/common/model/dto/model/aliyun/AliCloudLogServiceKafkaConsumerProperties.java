package com.doudoudrive.common.model.dto.model.aliyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>kafka消费参数配置</p>
 * 参考：<a href="https://help.aliyun.com/zh/sls/user-guide/use-java-to-implement-kafka-consumption?spm=a2c4g.11186623.0.0.242b4c4etPXgwL">使用Java实现Kafka消费</a>
 * <p>2024-03-31 15:57</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "kafka.sls.consumer")
public class AliCloudLogServiceKafkaConsumerProperties {

    /**
     * 初始连接的集群地址，格式为${project}.${endpoint}:${port}
     * 阿里云VPC内网：端口号为10011。
     * 公网：端口号为10012。
     */
    private String servers;

    /**
     * 日志服务项目名称
     */
    private String project;

    /**
     * 日志库名称，也是topic名称
     */
    private String topic;

    /**
     * 消费组名称
     */
    private String groupId;

    /**
     * 阿里云只读账号的AccessKey，格式为{access-key-id}#{access-key-secret}
     */
    private String password;

    /**
     * auto.commit.interval.ms
     * 自动提交消费点位的间隔时间，单位为毫秒
     */
    private Integer autoCommitIntervalMs;

    /**
     * max.poll.interval.ms
     * 消费组在消费者发起加入组请求后，等待所有消费者加入的时间间隔。
     */
    private Integer maxPollIntervalMs;

    /**
     * 心跳最大超时时间，在该时间如果消费者没有发送心跳请求，则视为该消费者发生异常，触发消费组再平衡操作。
     * Java实现Kafka消费时需要设置session.timeout.ms值大于max.poll.interval.ms值。
     */
    private Integer sessionTimeoutMs;

    /**
     * 设置消费offset的起始位置，有三个值可选：latest、earliest、none
     * earliest：表示使用最早的偏移量，从最早的消息开始读取。当有已提交的偏移量时，从提交的偏移量开始消费；无提交的偏移量时，从头开始消费。
     * <p>
     * latest：表示使用最新的偏移量，即从最新消息开始读取。当有已提交的偏移量时，从提交的偏移量开始消费；无提交的偏移量时，消费新产生的数据。
     */
    private String autoOffsetReset;

    /**
     * 最大拉取记录数
     */
    private Integer maxPollRecords;

    /**
     * 并发线程数
     */
    private Integer concurrency;

    /**
     * 拉取超时时间
     */
    private Integer pollTimeout;

    /**
     * 消费者心跳间隔时间，心跳之间的预期时间
     */
    private Integer heartbeatIntervalMs;

}
