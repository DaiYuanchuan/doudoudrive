package com.doudoudrive.common.util.http;

import com.doudoudrive.common.global.StatusCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * <p>状态返回类，数据返回响应的封装</p>
 * <p>通用面向对象基础返回数据封装</p>
 * 2019-10-30 02:00
 *
 * @author Dan
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 描述
     */
    private String message;

    /**
     * 对象
     */
    private T data;

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(Integer code, String message, T obj) {
        this.code = code;
        this.message = message;
        this.data = obj;
    }

    private Result(StatusCodeEnum statusCodeEnum) {
        this(statusCodeEnum.statusCode, statusCodeEnum.message);
    }

    private Result(StatusCodeEnum statusCodeEnum, T obj) {
        this(statusCodeEnum.statusCode, statusCodeEnum.message, obj);
    }

    /**
     * 判断返回是否为成功
     *
     * @return 是否成功(true : 成功的响应)
     */
    public boolean success() {
        return ObjectUtils.nullSafeEquals(StatusCodeEnum.SUCCESS.statusCode, this.code);
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isSuccess(@Nullable Result<?> result) {
        return Optional.ofNullable(result)
                .map(x -> ObjectUtils.nullSafeEquals(StatusCodeEnum.SUCCESS.statusCode, x.code))
                .orElse(Boolean.FALSE);
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isNotSuccess(@Nullable Result<?> result) {
        return !Result.isSuccess(result);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(StatusCodeEnum.SUCCESS, data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(StatusCodeEnum.SUCCESS);
    }

    public static <T> Result<T> authority() {
        return new Result<>(StatusCodeEnum.NOT_AUTHORIZED);
    }

    public static <T> Result<T> refuse() {
        return new Result<>(StatusCodeEnum.USER_UN_LOGIN);
    }

    public static <T> Result<T> error() {
        return new Result<>(StatusCodeEnum.SYSTEM_ERROR);
    }

    public static <T> Result<T> build(StatusCodeEnum statusCodeEnum) {
        return new Result<>(statusCodeEnum);
    }

    public static <T> Result<T> build(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> build(StatusCodeEnum statusCodeEnum, T data) {
        return new Result<>(statusCodeEnum, data);
    }

    public Integer getCode() {
        return code;
    }

    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}