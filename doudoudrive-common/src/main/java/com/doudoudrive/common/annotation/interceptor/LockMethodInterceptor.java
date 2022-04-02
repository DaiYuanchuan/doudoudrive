package com.doudoudrive.common.annotation.interceptor;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.doudoudrive.common.annotation.LocalLock;
import com.doudoudrive.common.util.http.Result;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * <p>基于 本地缓存 防止表单重复提交</p>
 * <p>2021-03-07 23:23</p>
 *
 * @author Dan
 **/
@Aspect
@Configuration
public class LockMethodInterceptor {

    /**
     * 设置缓存的基础配置
     */
    public static final Cache<String, Object> CACHES = CacheBuilder.newBuilder()
            // 最大缓存 5000 个
            .maximumSize(5000)
            // 设置写缓存后 5 分钟过期
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @SneakyThrows
    @Around("execution(public * *(..)) && @annotation(com.doudoudrive.common.annotation.LocalLock)")
    public Object interceptor(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        LocalLock localLock = method.getAnnotation(LocalLock.class);
        String key = getKey(localLock.key(), pjp.getArgs());
        if (StringUtils.isNotEmpty(key)) {
            if (CACHES.getIfPresent(key) != null) {
                // 发生重复的提交(返回缓存中的数据)
                return JSONObject.parseObject(JSONObject.toJSONStringWithDateFormat(CACHES.getIfPresent(key),
                        DatePattern.NORM_DATETIME_PATTERN, SerializerFeature.WriteMapNullValue), Result.class);
            }
            // 如果是第一次请求,就将 key 当前对象压入缓存中
            CACHES.put(key, pjp.proceed());
        }
        return pjp.proceed();
    }

    /**
     * key 的生成策略,如果想灵活可以写成接口与实现类的方式
     *
     * @param keyExpress 表达式
     * @param args       参数
     * @return 生成的key
     */
    private String getKey(String keyExpress, Object[] args) {
        for (int i = 0; i < args.length; i++) {
            keyExpress = keyExpress.replace(String.format("arg[%d]", i), args[i].toString());
        }
        return keyExpress;
    }
}