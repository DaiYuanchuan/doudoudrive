package com.doudoudrive.common.global;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>业务自定义异常</p>
 * <p>2022-03-21 17:29</p>
 *
 * @author Dan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    /**
     * 业务异常错误码
     */
    private StatusCodeEnum status;

    /**
     * 业务异常的原因
     */
    private String reason;

    /**
     * 任务对象
     */
    private Object data;

    public BusinessException(StatusCodeEnum statusCode) {
        this(statusCode, statusCode.message);
    }

    public BusinessException(StatusCodeEnum statusCode, String reason) {
        this(statusCode, reason, null);
    }

    public BusinessException(StatusCodeEnum statusCode, String reason, Object data) {
        super(reason);
        this.status = statusCode;
        this.reason = reason;
        this.data = data;
    }
}
