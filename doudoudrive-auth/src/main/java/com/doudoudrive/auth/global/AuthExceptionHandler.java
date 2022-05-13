package com.doudoudrive.auth.global;

import com.doudoudrive.common.util.http.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>权限相关异常处理</p>
 * <p>2022-04-04 20:46</p>
 *
 * @author Dan
 **/
@Slf4j
@Configuration
@RestControllerAdvice
public class AuthExceptionHandler {

    /**
     * 拦截捕捉无权限异常
     *
     * @param exception 错误信息集合
     * @return 错误的状态信息
     */
    @ResponseBody
    @ExceptionHandler(value = AuthorizationException.class)
    public Result<?> parameterTypeExceptionHandler(AuthorizationException exception) {
        log.error(exception.getCause().getLocalizedMessage());
        return Result.authority();
    }

}
