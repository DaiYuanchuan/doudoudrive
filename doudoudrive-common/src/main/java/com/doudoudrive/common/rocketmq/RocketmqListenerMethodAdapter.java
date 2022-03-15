package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.config.ConsumerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.global.ConsumeException;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.model.SubscriptionGroup;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>RocketMQ侦听器方法的适配器</p>
 * <p>2022-03-13 13:11</p>
 *
 * @author Dan
 **/
@Slf4j
public class RocketmqListenerMethodAdapter implements RocketmqConsumerListener {

    private final SubscriptionGroup subscriptionGroup;

    private ConsumerConfig consumerConfig;

    private MethodInvoker invoker;

    public RocketmqListenerMethodAdapter(SubscriptionGroup subscriptionGroup) {
        this.subscriptionGroup = subscriptionGroup;
        initConfig(subscriptionGroup);
    }

    /**
     * 讯息处理
     *
     * @param message        接收到的消息
     * @param body           消息体
     * @param messageContext 消息的上下文信息
     * @throws ConsumeException 抛出自定义消费者异常
     */
    @Override
    public void onMessage(Object message, byte[] body, MessageContext messageContext) throws ConsumeException {
        if (log.isDebugEnabled()) {
            log.debug("received message:{}", message.toString());
        }
        String tag = messageContext.getMessageExt().getTags();
        Method method = this.subscriptionGroup.getMethod(tag);
        Object delegate = this.subscriptionGroup.getTarget();
        if (method != null) {
            try {
                invoker.invoke(delegate, method, message, body, messageContext);
            } catch (Exception e) {
                throw new ConsumeException(e);
            }
        } else {
            if (ConstantConfig.SpecialSymbols.ASTERISK.equals(tag.trim())) {
                invoker.invoke(delegate, this.subscriptionGroup.getAllMethods(), message, body, messageContext);
            } else {
                throw new ConsumeException("未找到相应tag的方法");
            }
        }
    }

    /**
     * 获取消费者配置信息
     *
     * @return consumer的基本配置
     */
    @Override
    public ConsumerConfig getConsumerConfig() {
        return this.consumerConfig;
    }

    public void setInvoker(MethodInvoker invoker) {
        this.invoker = invoker;
    }

    private void initConfig(SubscriptionGroup subscriptionGroup) {
        RocketmqListener rocketListeners = subscriptionGroup.getTarget().getClass().getAnnotation(RocketmqListener.class);
        consumerConfig = ConsumerConfig.builder()
                .consumerGroup(rocketListeners.consumerGroup())
                .messageModel(rocketListeners.messageModel())
                .orderlyMessage(rocketListeners.orderly())
                .topic(rocketListeners.topic())
                .consumeThreadMax(rocketListeners.consumeThreadMax())
                .consumeThreadMin(rocketListeners.consumeThreadMin())
                .build();
        Map<String, Class<?>> tags = Maps.newHashMapWithExpectedSize(subscriptionGroup.getTagList().size());
        subscriptionGroup.getTagList().forEach(tag -> {
            RocketmqTagDistribution rocketMqListener = subscriptionGroup.getMethod(tag).getAnnotation(RocketmqTagDistribution.class);
            tags.put(tag, rocketMqListener.messageClass());
        });
        consumerConfig.setTags(tags);
    }
}
