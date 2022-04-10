package com.doudoudrive.common.cache;

import cn.hutool.core.thread.ThreadUtil;
import com.doudoudrive.common.util.lang.SpringBeanFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>Redis缓存订阅者的默认实现</p>
 * <p>主要用于各个服务的配置刷新</p>
 * <p>2022-04-10 18:32</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class Subscriber implements MessageListener, InitializingBean {

    /**
     * 当前钩子挂载的所有插件
     */
    protected Map<String, RedisMessageSubscriber> plugins;

    @Override
    public void afterPropertiesSet() {
        plugins = SpringBeanFactoryUtils.getBeansOfType(RedisMessageSubscriber.class);
    }

    /**
     * 消息内容默认回调处理
     *
     * @param message 消息体 + ChannelName
     * @param pattern 订阅的 pattern, ChannelName 的模式匹配
     */
    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        // 获取消息体和channelName
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        ThreadUtil.execAsync(() -> {
            try {
                // 异步回调所有实现了回调接口的类
                plugins.values().forEach(plugin -> plugin.receiveMessage(body, channel));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
