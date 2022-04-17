package com.doudoudrive.common.cache;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.util.lang.SpringBeanFactoryUtils;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * <p>redis缓存配置</p>
 * <p>2022-04-10 13:48</p>
 *
 * @author Dan
 **/
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置键（key）的序列化采用StringRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 设置值（value）的序列化采用GenericFastJsonRedisSerializer
        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        redisTemplate.setHashValueSerializer(genericFastJsonRedisSerializer);
        redisTemplate.setValueSerializer(genericFastJsonRedisSerializer);

        //设置连接工厂
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * 基于模式匹配的topic
     *
     * @return redis 发布、订阅用的Topic
     */
    @Bean
    public PatternTopic patternTopic() {
        return new PatternTopic(ConstantConfig.Cache.CHANNEL_CONFIG);
    }

    /**
     * 消息监听器的初始化
     */
    @Bean
    public MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(SpringBeanFactoryUtils.getBean(Subscriber.class));
    }

    /**
     * 将订阅者注册到redis
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 订阅一个channel，可以添加多个messageListener，来订阅不同的channel
        container.addMessageListener(messageListener(), patternTopic());
        return container;
    }
}
