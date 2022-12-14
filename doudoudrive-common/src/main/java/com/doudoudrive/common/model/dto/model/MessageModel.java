package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>消息构建时通用消息数据模型</p>
 * <p>2022-11-18 14:50</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志链路追踪id
     */
    private String tracerId;

    /**
     * 日志链路追踪调度id
     */
    private String spanId;

    /**
     * mq消息内容的原始数据类型
     */
    private Object message;

}
