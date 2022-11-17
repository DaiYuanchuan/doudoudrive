package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>系统日志标签数据模型</p>
 * <p>2022-11-17 21:40</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogLabelModel {

    /**
     * 日志链路追踪id
     */
    private String tracerId;

    /**
     * 日志链路追踪调度id
     */
    private String spanId;

}
