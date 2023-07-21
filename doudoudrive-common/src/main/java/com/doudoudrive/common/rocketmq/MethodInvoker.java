package com.doudoudrive.common.rocketmq;

import cn.hutool.core.util.ReflectUtil;
import com.doudoudrive.common.global.ConsumeException;
import com.doudoudrive.common.model.dto.model.MessageContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * <p>方法执行器，并实现InterceptorHookAware接口，通过set注入hook，实现方法执行前后的动态扩展</p>
 * <p>2022-03-13 12:16</p>
 *
 * @author Dan
 **/
@Slf4j
public class MethodInvoker implements ApplicationContextAware, InitializingBean {

    /**
     * 当前钩子挂载的所有插件
     */
    protected Map<String, InterceptorHook> plugins;

    /**
     * 应用程序上下文，通过Spring自动注入
     */
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        plugins = applicationContext.getBeansOfType(InterceptorHook.class);
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 对多个目标方法进行调用,调用策略为循环按顺序调用
     *
     * @param delegate       目标类
     * @param methods        目标方法
     * @param message        接收到的消息
     * @param body           消息体
     * @param messageContext 消息的上下文信息
     */
    public void invoke(Object delegate, Collection<Method> methods, Object message, byte[] body, MessageContext messageContext) {
        methods.forEach(method -> invoke(delegate, method, message, body, messageContext));
    }

    /**
     * 对目标方法进行调用
     *
     * @param delegate       方法所在对象
     * @param method         对应方法
     * @param message        接收到的消息
     * @param body           消息体
     * @param messageContext 消息的上下文信息
     */
    public void invoke(Object delegate, final Method method, Object message, byte[] body, MessageContext messageContext) {
        try {
            plugins.values().forEach(plugin -> plugin.preHandle(body, messageContext));
        } catch (Exception e) {
            handleHookException(e);
            return;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 检查方法中是否有MessageContext参数
        boolean hasContext = Arrays.asList(parameterTypes).contains(MessageContext.class);
        try {
            if (hasContext) {
                ReflectUtil.invoke(delegate, method, message, messageContext);
            } else {
                ReflectUtil.invoke(delegate, method, message);
            }
        } catch (Exception e) {
            plugins.values().forEach(plugin -> plugin.nextHandle(false, body, messageContext));
            throw new ConsumeException(e);
        }
        try {
            plugins.values().forEach(plugin -> plugin.nextHandle(true, body, messageContext));
        } catch (Exception e) {
            handleHookException(e);
        }
    }

    private void handleHookException(Exception e) {
        log.error(e.getMessage(), e);
    }
}
