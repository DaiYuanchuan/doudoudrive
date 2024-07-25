package com.doudoudrive.common.global;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.util.StrUtil;
import com.doudoudrive.common.util.http.Result;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * <p>配置全局异常处理</p>
 * <p>2022-03-21 12:16</p>
 *
 * @author Dan
 **/
@Slf4j
@Configuration
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 常量 @Valid 类型匹配异常代号
     */
    private static final String VALID_TYPE_MISMATCH = "typeMismatch";

    /**
     * 校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = BindException.class)
    public Result<?> methodArgumentNotValidHandler(BindException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors() && bindingResult.getFieldError() != null &&
                bindingResult.getFieldError().getCode() != null) {
            if (bindingResult.getFieldError().getCode().equals(VALID_TYPE_MISMATCH)) {
                log.error(exception.getMessage());
                return Result.build(StatusCodeEnum.PARAM_INVALID).message("参数类型错误");
            }
            if (bindingResult.getFieldError().isBindingFailure()) {
                return Result.build(StatusCodeEnum.PARAM_INVALID).message("参数绑定失败");
            }
            return Result.build(StatusCodeEnum.PARAM_INVALID).message(bindingResult.getFieldError().getDefaultMessage());
        }
        return Result.build(StatusCodeEnum.PARAM_INVALID).message("请填写正确的信息");
    }

    /**
     * 处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
     *
     * @param constraintViolationException 错误信息集合
     * @return 错误的状态信息
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> constraintViolationExceptionHandler(ConstraintViolationException constraintViolationException) {
        return Result.build(StatusCodeEnum.PARAM_INVALID)
                .message(constraintViolationException.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining()));
    }

    /**
     * 处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
     *
     * @param methodArgumentNotValidException 错误信息集合
     * @return 错误的状态信息
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException) {
        if (methodArgumentNotValidException.getBindingResult().hasErrors() && methodArgumentNotValidException.getBindingResult().getFieldError() != null &&
                methodArgumentNotValidException.getBindingResult().getFieldError().getCode() != null) {
            if (methodArgumentNotValidException.getBindingResult().getFieldError().getCode().equals(VALID_TYPE_MISMATCH)) {
                return Result.build(StatusCodeEnum.PARAM_INVALID).message("参数类型错误");
            }
            if (methodArgumentNotValidException.getBindingResult().getFieldError().isBindingFailure()) {
                return Result.build(StatusCodeEnum.PARAM_INVALID).message("参数绑定失败");
            }
            return Result.build(StatusCodeEnum.PARAM_INVALID)
                    .message(methodArgumentNotValidException.getBindingResult().getFieldError().getDefaultMessage());
        }
        return Result.build(StatusCodeEnum.PARAM_INVALID).message("请填写正确的信息");
    }

    /**
     * 非法数据异常[判断错误有时会抛出此异常]
     *
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<?> illegalArgumentExceptionHandler(IllegalArgumentException i) {
        // 抛出验证失败时返回的状态码
        return Result.build(StatusCodeEnum.PARAM_INVALID).message(i.getMessage());
    }

    /**
     * 校验失败时抛出的异常
     *
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = ValidateException.class)
    public Result<?> validateExceptionHandler(ValidateException v) {
        // 抛出验证失败时返回的状态码
        return Result.build(StatusCodeEnum.PARAM_INVALID).message(v.getMessage());
    }

    /**
     * 数字格式异常处理
     *
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = NumberFormatException.class)
    public Result<?> numberFormatExceptionExceptionHandler(NumberFormatException e) {
        log.error(e.getMessage(), e);
        return Result.build(StatusCodeEnum.PARAM_INVALID).message("不被接受的请求:数字格式异常");
    }

    /**
     * 方法参数类型不匹配异常
     *
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public Result<?> methodArgumentTypeMismatchExceptionHandler() {
        return Result.build(StatusCodeEnum.PARAM_INVALID).message("方法参数类型不匹配");
    }

    /**
     * http媒体类型不支持异常处理程序
     *
     * @param e 异常信息
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public Result<?> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException e) {
        log.error(StrUtil.format("错误的媒体类型:'{}'", e.getContentType()));
        return Result.build(StatusCodeEnum.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * 邮件发送失败时触发此类型异常
     *
     * @param e 异常信息
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = cn.hutool.extra.mail.MailException.class)
    public Result<?> mailExceptionHandler(cn.hutool.extra.mail.MailException e) {
        log.error(e.getMessage());
        return Result.build(StatusCodeEnum.ABNORMAL_MAIL_SENDING);
    }

    /**
     * 不支持Http请求方法异常，通常为请求方式错误
     *
     * @return 错误的状态信息
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result<?> requestMethodHandler() {
        return Result.build(StatusCodeEnum.METHOD_NOT_ALLOWED);
    }

    /**
     * 当 RequestBody 请求体为空时会触发此异常
     *
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = org.springframework.http.converter.HttpMessageNotReadableException.class)
    public Result<?> httpMessageNotReadableException() {
        return Result.build(StatusCodeEnum.PARAM_INVALID).message("需要的请求主体丢失");
    }

    /**
     * 缺少Servlet请求部分异常处理程序
     * 通常为文件上传时缺少文件异常
     *
     * @param exception 异常信息
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = org.springframework.web.multipart.support.MissingServletRequestPartException.class)
    public Result<?> missingServletRequestPartExceptionHandler(MissingServletRequestPartException exception) {
        return Result.build(StatusCodeEnum.PARAM_INVALID).message(exception.getMessage());
    }

    /**
     * sql完整性约束冲突异常处理程序
     *
     * @param exception 异常信息
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = java.sql.SQLIntegrityConstraintViolationException.class)
    public Result<?> sqlIntegrityConstraintViolationExceptionHandler(java.sql.SQLIntegrityConstraintViolationException exception) {
        // 写入日志文件
        log.error(exception.getMessage());
        return Result.error();
    }

    /**
     * 多部分异常处理程序(通常表现为当前上传的文件大小超过指定大小)
     *
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = org.springframework.web.multipart.MultipartException.class)
    public Result<?> multipartExceptionHandler() {
        return Result.build(StatusCodeEnum.PAYLOAD_TOO_LARGE);
    }

    /**
     * 业务异常处理
     *
     * @param exception 异常信息
     * @return 错误的状态信息
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException exception) {
        log.info(exception.getMessage(), exception);
        return Result.build(exception.getStatusCode(), exception.getReason(), exception.getData());
    }

    /**
     * feign客户端异常处理
     *
     * @param exception 异常信息
     * @return 错误的状态信息
     */
    @ExceptionHandler(FeignException.class)
    public Result<?> feignExceptionHandler(FeignException exception) {
        return Result.build(StatusCodeEnum.INTERFACE_INTERNAL_EXCEPTION).data(exception.getMessage());
    }

    /**
     * 全局异常处理
     *
     * @param throwable 异常信息集合
     * @return 错误的状态信息
     */
    @ExceptionHandler(value = Throwable.class)
    public Result<?> handleThrowableHandler(Throwable throwable) {
        // 写入日志文件
        log.error(throwable.getMessage());
        log.error("", throwable);
        return Result.error();
    }

    /**
     * 处理 404 500 400 页面
     *
     * @return 错误的状态码对应的页面
     */
    @Bean
    public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {
        return (container -> {
            ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/404");
            ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
            container.addErrorPages(error404Page, error400Page);
        });
    }
}
