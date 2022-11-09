package com.doudoudrive.common.global;

import com.doudoudrive.common.util.http.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>业务异常工具类</p>
 * <p>2022-03-21 17:36</p>
 *
 * @author Dan
 **/
@Slf4j
public class BusinessExceptionUtil {

    /**
     * 抛出业务异常
     *
     * @param status 错误码状态
     */
    public static void throwBusinessException(StatusCodeEnum status) {
        throw new BusinessException(status);
    }

    /**
     * 抛出业务异常
     *
     * @param result 通用面向对象基础返回数据类
     */
    public static void throwBusinessException(Result<Object> result) {
        throw new BusinessException(result);
    }

    /**
     * 抛出业务异常，支持自定义错误信息
     *
     * @param status 错误码状态
     * @param reason 异常原因
     */
    public static void throwBusinessException(StatusCodeEnum status, String reason) {
        throw new BusinessException(status, reason);
    }

    /**
     * 抛出业务异常，支持自定义data数据
     *
     * @param status 错误码状态
     * @param data   任务对象
     */
    public static void throwBusinessException(StatusCodeEnum status, Object data) {
        throw new BusinessException(status, status.message, data);
    }
}
