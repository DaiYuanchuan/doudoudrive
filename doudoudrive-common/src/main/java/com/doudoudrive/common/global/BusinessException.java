package com.doudoudrive.common.global;

import com.doudoudrive.common.util.http.Result;
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
     * 业务异常状态码
     */
    private Integer statusCode;

    /**
     * 业务异常的原因
     */
    private String reason;

    public BusinessException(StatusCodeEnum statusCode) {
        this(statusCode, statusCode.message);
    }

    public BusinessException(Result<?> result) {
        super(result.getMessage());
        this.statusCode = result.getCode();
        this.reason = result.getMessage();
    }

    public BusinessException(StatusCodeEnum statusCode, String reason) {
        super(reason);
        this.statusCode = statusCode.statusCode;
        this.reason = reason;
    }
}
