package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>系统日志消息实例对象</p>
 * <p>2022-11-09 21:11</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysLogMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务标识
     */
    private String businessId;

    /**
     * 追踪id
     */
    private String tracerId;

    /**
     * 调度id
     */
    private String spanId;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 日志级别 info、error
     */
    private String level;

    /**
     * 应用名
     */
    private String appName;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 线程名
     */
    private String threadName;

    /**
     * 创建的时间戳
     */
    private Date timestamp;

}