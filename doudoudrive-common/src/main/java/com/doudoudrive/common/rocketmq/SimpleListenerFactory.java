package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.model.SubscriptionGroup;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>RocketMQ的侦听器工厂</p>
 * <p>2022-03-13 13:10</p>
 *
 * @author Dan
 **/
@Slf4j
public class SimpleListenerFactory implements InitializingBean, ApplicationContextAware {

    private Map<String, RocketmqConsumerListener> allListeners;

    private final MethodResolver resolver;

    private ApplicationContext context;

    public SimpleListenerFactory() {
        this.resolver = new MethodResolver();
    }

    public Map<String, RocketmqConsumerListener> getAllListeners() {
        return allListeners;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, SubscriptionGroup> subscriptionGroups = this.resolver.getSubscriptionGroups();
        allListeners = Maps.newHashMapWithExpectedSize(subscriptionGroups.size());
        subscriptionGroups.forEach((topic, subscriptionGroup) -> allListeners.put(topic, createRocketmqConsumerListener(subscriptionGroup)));
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        this.resolver.setApplicationContext(applicationContext);
    }

    private RocketmqConsumerListener createRocketmqConsumerListener(SubscriptionGroup subscriptionGroup) {
        RocketmqListenerMethodAdapter adapter = new RocketmqListenerMethodAdapter(subscriptionGroup);
        adapter.setInvoker(context.getBean(MethodInvoker.class));
        return adapter;
    }

    private static class MethodResolver implements ApplicationContextAware {

        private ApplicationContext context;

        private final Map<String, SubscriptionGroup> subscriptionGroups = new HashMap<>();

        private boolean initSubscription = false;


        Map<String, SubscriptionGroup> getSubscriptionGroups() {
            if (!initSubscription) {
                resolveListenerMethod();
            }
            return subscriptionGroups;
        }

        public void resolveListenerMethod() {
            context.getBeansWithAnnotation(RocketmqListener.class).forEach((beanName, obj) -> {
                Map<Method, RocketmqTagDistribution> annotatedMethods = MethodIntrospector.selectMethods(obj.getClass(),
                        (MethodIntrospector.MetadataLookup<RocketmqTagDistribution>) method -> AnnotatedElementUtils
                                .findMergedAnnotation(method, RocketmqTagDistribution.class));
                initSubscriptionGroup(annotatedMethods, obj);
            });
            this.initSubscription = true;
        }

        @Override
        public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
            this.context = applicationContext;
        }

        private void initSubscriptionGroup(Map<Method, RocketmqTagDistribution> annotatedMethod, Object target) {
            if (!CollectionUtils.isEmpty(annotatedMethod)) {
                annotatedMethod.forEach((method, listener) -> {
                    validateMethod(method);
                    RocketmqListener rocketListeners = method.getDeclaringClass().getAnnotation(RocketmqListener.class);
                    String topic = rocketListeners.topic();
                    String tag = listener.tag();
                    if (subscriptionGroups.containsKey(topic)) {
                        subscriptionGroups.get(topic).putTagToGroup(tag, method);
                    } else {
                        SubscriptionGroup subscriptionGroup = new SubscriptionGroup(topic);
                        subscriptionGroup.putTagToGroup(tag, method);
                        subscriptionGroup.setTarget(target);
                        subscriptionGroups.put(topic, subscriptionGroup);
                    }
                });
            }
        }

        private void validateMethod(Method method) {
            if (method.getParameterCount() > NumberConstant.INTEGER_TWO) {
                throw new IllegalArgumentException("method: " + method + " 参数列表不被支持");
            }
            boolean typeSupport = Arrays.stream(method.getParameterTypes()).allMatch(parameterType -> parameterType.equals(method
                    .getAnnotation(RocketmqTagDistribution.class).messageClass()) || parameterType.equals(MessageContext.class));
            if (!typeSupport) {
                throw new IllegalArgumentException("方法参数中含有不被支持的类型");
            }
        }
    }
}
