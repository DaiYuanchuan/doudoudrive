package com.doudoudrive.common.rocketmq;

import com.doudoudrive.common.global.ConsumeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>RocketMQ消息监听器配置</p>
 * <p>2022-03-13 13:08</p>
 *
 * @author Dan
 **/
@Data
@Slf4j
public class RocketmqMessageListenerContainer implements InitializingBean, DisposableBean,
        SmartLifecycle, ApplicationContextAware, ConsumerOperator {

    /**
     * rocketMQ的 name server 地址，格式为：`host:port;host:port`
     */
    private String nameServer;

    /**
     * 消费者端最小线程数
     */
    private int consumeThreadMin;

    /**
     * 消费者端最大线程数
     */
    private int consumeThreadMax;

    /**
     * 初始化监视对象
     */
    private final Object monitor = new Object();

    /**
     * 运行时监视对象
     */
    private final Object mapMonitor = new Object();

    /**
     * 当前实例是否在运行
     */
    private volatile boolean running = false;

    /**
     * 当前实例是否完成初始化
     */
    private volatile boolean initialized = false;

    private final List<DefaultMQPushConsumer> pushConsumers = new CopyOnWriteArrayList<>();
    private final Map<String, DefaultMQPushConsumer> pushConsumerMap = new ConcurrentHashMap<>();
    private final Map<String, DefaultMQPushConsumer> removedMap = new ConcurrentHashMap<>();
    private final Map<String, DefaultMQPushConsumer> runningMap = new ConcurrentHashMap<>();
    private final Map<String, Map.Entry<DefaultMQPushConsumer, String>> startErrMap = new ConcurrentHashMap<>();

    private MqPushConsumerFactory consumerFactory;

    private ApplicationContext applicationContext;

    @Override
    public void start() {
        if (!isRunning()) {
            synchronized (monitor) {
                if (!isRunning()) {
                    running = true;

                    // 对消息监听器注册
                    SimpleListenerFactory listenerFactory = consumerFactory.getListenerFactory();
                    pushConsumers.addAll(consumerFactory.getAllMqPushConsumer());
                    pushConsumerMap.putAll(consumerFactory.getPushConsumerMap());
                    Map<String, RocketmqConsumerListener> listenerMap = listenerFactory.getAllListeners();
                    pushConsumerMap.forEach((topic, consumer) -> {
                        RocketmqConsumerListener listener = listenerMap.get(topic);
                        if (listener.getConsumerConfig().isOrderlyMessage()) {
                            consumer.registerMessageListener(new MessageListenerOrderlyImpl(listener));
                        } else {
                            consumer.registerMessageListener(new MessageListenerConcurrentlyImpl(listener));
                        }
                    });

                    // 启动所有的监听器
                    pushConsumerMap.forEach((topic, consumer) -> {
                        try {
                            consumer.start();
                            runningMap.put(topic, consumer);
                        } catch (MQClientException e) {
                            log.error(e.getErrorMessage());
                            Map.Entry<DefaultMQPushConsumer, String> errEntry = new AbstractMap.SimpleEntry<>(consumer, e.getErrorMessage());
                            startErrMap.put(topic, errEntry);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            running = false;
            pushConsumers.forEach(DefaultMQPushConsumer::shutdown);
        }
        if (log.isDebugEnabled()) {
            log.debug("Stopped RocketMessageListenerContainer");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void destroy() {
        this.initialized = false;
        stop();
    }

    @Override
    public void afterPropertiesSet() {
        this.consumerFactory = new MqPushConsumerFactory(this.nameServer);
        this.consumerFactory.setApplicationContext(applicationContext);
        this.consumerFactory.setConsumeThreadMax(this.consumeThreadMax);
        this.consumerFactory.setConsumeThreadMin(this.consumeThreadMin);
        this.consumerFactory.afterPropertiesSet();
        this.initialized = true;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void suspendConsumer(String topic) {
        analyzeResult(topic, OperationType.SUSPEND, () -> {
            DefaultMQPushConsumer consumer = runningMap.get(topic);
            consumer.suspend();
            runningMap.remove(topic);
            removedMap.put(topic, consumer);
        });
    }

    @Override
    public void resumeConsumer(String topic) {
        analyzeResult(topic, OperationType.RESUME, () -> {
            DefaultMQPushConsumer consumer = removedMap.get(topic);
            consumer.resume();
            removedMap.remove(topic);
            runningMap.put(topic, consumer);
        });
    }

    /**
     * 对消费者启动结果进行分析
     *
     * @param topic         topic主题
     * @param operationType 操作类型枚举
     * @param runnable      运行线程
     */
    private void analyzeResult(String topic, OperationType operationType, Runnable runnable) {
        if (initialized) {
            if (!pushConsumerMap.containsKey(topic)) {
                throw new ConsumeException("未找到对应的消费者");
            } else {
                if (startErrMap.containsKey(topic)) {
                    throw new ConsumeException("启动出现异常");
                }
                if (runningMap.containsKey(topic)) {
                    if (!operationType.equals(OperationType.SUSPEND)) {
                        throw new ConsumeException("该消费者正在运行中");
                    }
                    runnable.run();
                    return;
                }
                if (removedMap.containsKey(topic)) {
                    if (!operationType.equals(OperationType.RESUME)) {
                        throw new ConsumeException("该消费者正在暂停中");
                    }
                    synchronized (mapMonitor) {
                        runnable.run();
                    }
                    return;
                }
            }
            throw new ConsumeException("该消费者启动异常");
        }
        throw new ConsumeException("容器尚未初始化");
    }

    /**
     * 操作类型枚举
     */
    private enum OperationType {
        /**
         * 恢复
         */
        RESUME,

        /**
         * 暂停
         */
        SUSPEND
    }
}
