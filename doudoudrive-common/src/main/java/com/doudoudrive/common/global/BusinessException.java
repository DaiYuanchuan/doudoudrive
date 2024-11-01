package com.doudoudrive.common.global;

import com.doudoudrive.common.util.http.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * <p>业务自定义异常</p>
 * <p>2022-03-21 17:29</p>
 *
 * @author Dan
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 401050577231711465L;

    /**
     * 业务异常错误码
     */
    private Integer statusCode;

    /**
     * 业务异常的原因
     */
    private String reason;

    /**
     * 任务对象
     */
    private Object data;

    public BusinessException(Result<?> result) {
        this(result.getCode(), result.getMessage(), result.getData());
    }

    public BusinessException(StatusCodeEnum statusCode) {
        this(statusCode, statusCode.getMessage());
    }

    public BusinessException(StatusCodeEnum statusCode, String reason) {
        this(statusCode, reason, null);
    }

    public BusinessException(StatusCodeEnum statusCode, String reason, Object data) {
        this(statusCode.getStatusCode(), reason, data);
    }

    public BusinessException(Integer statusCode, String reason, Object data) {
        super(reason);
        this.statusCode = statusCode;
        this.reason = reason;
        this.data = data;
    }
}
