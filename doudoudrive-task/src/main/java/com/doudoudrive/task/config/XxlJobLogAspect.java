package com.doudoudrive.task.config;

import com.doudoudrive.common.log.tracer.context.TracerContextFactory;
import com.doudoudrive.common.model.dto.model.LogLabelModel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * <p>xxl-job 日志切面</p>
 * <p>2022-11-17 22:58</p>
 *
 * @author Dan
 **/
@Slf4j
@Aspect
@Component
public class XxlJobLogAspect {

    @Pointcut("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        try {
            TracerContextFactory.set(new LogLabelModel());
            return jp.proceed();
        } finally {
            TracerContextFactory.clear();
        }
    }
}
