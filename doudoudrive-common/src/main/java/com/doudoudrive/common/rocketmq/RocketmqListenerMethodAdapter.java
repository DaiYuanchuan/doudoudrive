package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.annotation.RocketmqListener;
import com.doudoudrive.common.annotation.RocketmqTagDistribution;
import com.doudoudrive.common.config.ConsumerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.global.ConsumeException;
import com.doudoudrive.common.model.dto.model.MessageContext;
import com.doudoudrive.common.model.dto.model.SubscriptionGroup;
import com.google.common.collect.Maps;
import lombok.Setter;
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

    @Setter
    private MethodInvoker invoker;

    public RocketmqListenerMethodAdapter(SubscriptionGroup subscriptionGroup) {
        this.subscriptionGroup = subscriptionGroup;
        initConfig(subscriptionGroup);
    }

    /**
     * 讯息处理
     *
     * @param body           消息体
     * @param type           消费者接受的消息类型
     * @param messageContext 消息的上下文信息
     * @throws ConsumeException 抛出自定义消费者异常
     */
    @Override
    public void onMessage(byte[] body, Class<?> type, MessageContext messageContext) throws ConsumeException {
        String tag = messageContext.getMessageExt().getTags();
        Method method = this.subscriptionGroup.getMethod(tag);
        Object delegate = this.subscriptionGroup.getTarget();
        if (method != null) {
            try {
                invoker.invoke(delegate, method, body, type, messageContext);
            } catch (Exception e) {
                throw new ConsumeException(e);
            }
        } else {
            if (ConstantConfig.SpecialSymbols.ASTERISK.equals(tag.trim())) {
                invoker.invoke(delegate, this.subscriptionGroup.getAllMethods(), body, type, messageContext);
            } else {
                throw new ConsumeException(String.format("未找到相应的tag(%s)方法", tag));
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
