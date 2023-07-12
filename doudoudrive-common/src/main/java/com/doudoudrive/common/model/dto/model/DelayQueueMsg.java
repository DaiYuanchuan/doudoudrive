package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>redis延迟队列内容消息体</p>
 * <p>2023-05-27 23:07</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayQueueMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = 1482957683138497786L;

    /**
     * 延迟队列任务名称
     */
    private String topic;

    /**
     * 到期时间，当前时间+延迟时间的绝对时间，单位毫秒
     */
    private Long expireTime;

    /**
     * 延迟任务消息体，供消费者做具体的业务处理
     */
    private byte[] body;
}
