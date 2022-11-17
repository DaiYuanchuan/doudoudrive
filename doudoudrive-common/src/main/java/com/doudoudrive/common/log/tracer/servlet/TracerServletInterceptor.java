package com.doudoudrive.common.log.tracer.servlet;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.log.tracer.context.TracerContextFactory;
import com.doudoudrive.common.model.dto.model.LogLabelModel;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * <p>日志追踪控制层拦截器</p>
 * <p>2022-11-17 20:17</p>
 *
 * @author Dan
 **/
public class TracerServletInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) {
        Optional.ofNullable(request).ifPresent(req -> TracerContextFactory.set(LogLabelModel.builder()
                .tracerId(req.getHeader(ConstantConfig.LogTracer.TRACER_ID))
                .spanId(req.getHeader(ConstantConfig.LogTracer.SPAN_ID))
                .build()));
        return true;
    }

    @Override
    public void afterCompletion(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler, Exception ex) {
        TracerContextFactory.clear();
    }
}
