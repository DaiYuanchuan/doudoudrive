package com.doudoudrive.commonservice.annotation.interceptor;

import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.config.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * <p>动态数据源切面注解</p>
 * <p>2022-03-04 10:28</p>
 *
 * @author Dan
 **/
@Slf4j
@Aspect
@Component
public class DataSourceAspect {

    /**
     * 定义业务 Mapper 相关切点
     */
    @Pointcut("execution(* com.doudoudrive.commonservice.dao.*Dao.*(..)))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object doBefore(ProceedingJoinPoint pjp) throws Throwable {
        return process(pjp);
    }

    private Object process(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = getDataSource(point);
        // 方法执行前开始设置当前线程数据源
        DataSourceContextHolder.push(dataSource.value().dataSource);
        Object object = point.proceed();
        // 方法执行后清空当前线程数据源
        DataSourceContextHolder.poll();
        return object;
    }

    /**
     * 获取需要切换的数据源注解
     *
     * @param point 连接点
     * @return 数据源注解
     */
    private DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        DataSource targetDataSource = AnnotationUtils.findAnnotation(method, DataSource.class);
        if (targetDataSource != null) {
            return targetDataSource;
        } else {
            Class<?> targetClass = point.getTarget().getClass();
            return AnnotationUtils.findAnnotation(targetClass, DataSource.class);
        }
    }
}