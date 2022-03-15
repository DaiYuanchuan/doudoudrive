package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.config.ConsumerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.google.common.collect.Maps;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>MQ消费者工厂类</p>
 * <p>2022-03-13 13:09</p>
 *
 * @author Dan
 **/
public class MqPushConsumerFactory implements InitializingBean, ApplicationContextAware {

    private final String nameServer;

    private SimpleListenerFactory listenerFactory;

    private ApplicationContext applicationContext;

    private Map<String, DefaultMQPushConsumer> pushConsumerMap;

    private List<DefaultMQPushConsumer> pushConsumers;

    private int consumeThreadMin;

    private int consumeThreadMax;

    public MqPushConsumerFactory(String nameServer) {
        this.nameServer = nameServer;
    }

    public Map<String, DefaultMQPushConsumer> getPushConsumerMap() {
        return pushConsumerMap;
    }

    public List<DefaultMQPushConsumer> getAllMqPushConsumer() {
        return pushConsumers;
    }

    public SimpleListenerFactory getListenerFactory() {
        return listenerFactory;
    }

    @Override
    public void afterPropertiesSet() {
        pushConsumers = new ArrayList<>();
        pushConsumerMap = Maps.newHashMapWithExpectedSize(16);
        if (listenerFactory == null) {
            listenerFactory = new SimpleListenerFactory();
            listenerFactory.setApplicationContext(this.applicationContext);
            listenerFactory.afterPropertiesSet();
        }
        listenerFactory.getAllListeners().forEach((topic, consumerListener) -> {
            DefaultMQPushConsumer pushConsumer = createDefaultMqPushConsumer(consumerListener);
            pushConsumers.add(pushConsumer);
            pushConsumerMap.put(topic, pushConsumer);
        });
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    private DefaultMQPushConsumer createDefaultMqPushConsumer(RocketmqConsumerListener rocketmqConsumerListener) {
        ConsumerConfig config = rocketmqConsumerListener.getConsumerConfig();
        DefaultMQPushConsumer defaultMqPushConsumer = new DefaultMQPushConsumer();
        defaultMqPushConsumer.setNamesrvAddr(nameServer);
        defaultMqPushConsumer.setConsumerGroup(config.getConsumerGroup());
        Map<String, Class<?>> tags = config.getTags();
        StringBuilder tagBuilder = new StringBuilder();
        List<String> tmpTags = new ArrayList<>(tags.keySet());
        for (int i = 0; i < tmpTags.size(); i++) {
            if (tmpTags.contains(ConstantConfig.SpecialSymbols.ASTERISK) && tmpTags.size() > 1) {
                throw new IllegalArgumentException("订阅的tag不合法");
            }
            tagBuilder.append(tmpTags.get(i));
            if (tmpTags.size() > i + 1) {
                tagBuilder.append(ConstantConfig.SpecialSymbols.OR);
            }
        }
        try {
            defaultMqPushConsumer.subscribe(config.getTopic(), tagBuilder.toString());
            defaultMqPushConsumer.subscribe(config.getTopic(), ConstantConfig.SpecialSymbols.ASTERISK);
        } catch (MQClientException e) {
            throw new IllegalArgumentException("订阅语法错误", e);
        }
        defaultMqPushConsumer.setMessageModel(config.getMessageModel());
        if (config.getConsumeThreadMax() == 0) {
            defaultMqPushConsumer.setConsumeThreadMax(this.consumeThreadMax);
        } else {
            defaultMqPushConsumer.setConsumeThreadMax(config.getConsumeThreadMax());
        }
        if (config.getConsumeThreadMin() == 0) {
            defaultMqPushConsumer.setConsumeThreadMin(this.consumeThreadMin);
        } else {
            defaultMqPushConsumer.setConsumeThreadMin(config.getConsumeThreadMin());
        }
        return defaultMqPushConsumer;
    }
}