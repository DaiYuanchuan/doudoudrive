package com.doudoudrive.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>RocketMQ消费记录实体类</p>
 * <p>2022-05-17 11:56</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RocketmqConsumerRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增长标识
     */
    private Long autoId;

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * MQ消息标识
     */
    private String msgId;

    /**
     * MQ消息偏移id
     */
    private String offsetMsgId;

    /**
     * 消息重试次数
     */
    private Integer retryCount;

    /**
     * MQ消息主题
     */
    private String topic;

    /**
     * MQ消息标签
     */
    private String tag;

    /**
     * MQ分片名
     */
    private String brokerName;

    /**
     * MQ消费队列id
     */
    private String queueId;

    /**
     * MQ逻辑队列偏移
     */
    private String queueOffset;

    /**
     * 消息发送时间
     */
    private Date sendTime;

    /**
     * 消息发送状态
     * 参见：{@link com.doudoudrive.common.constant.ConstantConfig.MqMessageSendStatus}
     */
    private String sendStatus;

    /**
     * 消息的消费状态
     * 参见：{@link com.doudoudrive.common.constant.ConstantConfig.RocketmqConsumerStatusEnum}
     */
    private String status;

    /**
     * 消息体内容
     */
    private String body;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
