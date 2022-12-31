package com.doudoudrive.common.cache;

import cn.hutool.cache.impl.TimedCache;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.CacheRefreshModel;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p>框架服务缓存信息通用处理配置</p>
 * <p>2022-04-22 15:09</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@Scope("singleton")
public class CacheManagerConfig implements RedisMessageSubscriber {

    /**
     * Redis客户端操作相关工具类
     */
    private RedisTemplateClient redisTemplateClient;

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    /**
     * 定义本地缓存超时时间(1小时)(毫秒)
     */
    private static final long CACHE_TIMEOUT = 3600000L;

    /**
     * 当前jvm的本地缓存Map
     */
    private static TimedCache<String, Object> TIMED_LOCAL_CACHE = null;

    /**
     * 获取缓存中的值，本地缓存不存在时会从redis中获取，并将redis的值同步到本地缓存中去
     *
     * @param key 指定的key
     * @param <T> 值的类型
     * @return 根据key从缓存中查找固定的值
     */
    public <T> T getCache(String key) {
        // 获取本地jvm缓存对象
        TimedCache<String, Object> localCacheMap = getLocalCacheMap();
        // 从jvm缓存中获取指定的缓存对象
        Object cache = localCacheMap.get(key);
        // 当本地缓存为空时，尝试从redis中获取
        if (cache == null) {
            cache = redisTemplateClient.get(key);
            // Redis查询不为空，将缓存对象存储到临时缓存中去
            Optional.ofNullable(cache).ifPresent(object -> localCacheMap.put(key, object));
        }

        // 如果从redis中也无法获取时，则通知其他服务同步删除此缓存
        if (cache == null) {
            // 将被删除的key同步到其他服务
            redisTemplateClient.publish(ConstantConfig.Cache.ChanelEnum.CHANNEL_CACHE, CacheRefreshModel.builder()
                    .cacheKey(key)
                    .build());
            return null;
        }

        // 获取当前缓存对象
        return ConvertUtil.convert(cache);
    }

    /**
     * 获取本地缓存中的值，只获取本地jvm缓存的值，不会从redis中读取
     *
     * @param key 指定的key
     * @param <T> 值的类型
     * @return 根据key从缓存中查找固定的值
     */
    public <T> T getCacheFromLocal(String key) {
        // 获取本地缓存Map对象
        TimedCache<String, Object> localCacheMap = getLocalCacheMap();
        return ConvertUtil.convert(localCacheMap.get(key));
    }

    /**
     * 批量获取缓存对象信息，本地缓存不存在时会从redis中获取，并将redis的值同步到本地缓存中去
     *
     * @param keys 指定的key
     * @param <T>  值的类型
     * @return 根据key从缓存中查找到的缓存对象集合
     */
    public <T> Collection<T> getCache(Set<String> keys) {
        List<T> list = new ArrayList<>();
        CollectionUtil.collectionCutting(keys, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(key -> {
            List<String> cacheKeyList = key.stream().filter(StringUtils::isNotBlank).toList();
            for (String cacheKey : cacheKeyList) {
                list.add(getCache(cacheKey));
            }
        });
        return list;
    }

    /**
     * 向缓存中插入值，会同步向jvm本地缓存，redis缓存中插入
     *
     * @param key    缓存中的key值
     * @param value  缓存对象
     * @param expire 缓存过期时间，为NULL时不设置过期时间(秒)
     */
    public void putCache(String key, Object value, Long expire) {
        if (!ObjectUtils.allNotNull(key, value)) {
            return;
        }

        // 将临时缓存对象存储到Jvm缓存中去
        this.putCacheFromLocal(key, value);
        // 向redis中插入缓存数据
        redisTemplateClient.set(key, value, expire);
    }

    /**
     * 只向本地jvm缓存中插入值
     *
     * @param key   缓存中的key值
     * @param value 缓存对象
     */
    public void putCacheFromLocal(String key, Object value) {
        if (!ObjectUtils.allNotNull(key, value)) {
            return;
        }

        // 获取本地缓存Map对象
        TimedCache<String, Object> localCacheMap = getLocalCacheMap();
        // 将临时缓存对象存储到Jvm缓存中去
        localCacheMap.put(key, value);
    }

    /**
     * 从缓存中删除数据，会同步删除本地、redis中的数据
     *
     * @param key 指定删除缓存中的key值
     * @param <T> 值的类型
     * @return 返回被删除的缓存对象，如果缓存对象不存在，则返回null
     */
    public <T> T removeCache(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 获取本地缓存Map对象
        TimedCache<String, Object> localCacheMap = getLocalCacheMap();
        Object cache = Optional.ofNullable(localCacheMap.get(key)).orElse(redisTemplateClient.get(key));

        // 如果从本地缓存、redis缓存中都无法获取到值时，直接返回null
        if (cache == null) {
            return null;
        }

        // 删除本地缓存对象
        localCacheMap.remove(key);
        // 删除redis中的缓存对象
        redisTemplateClient.delete(key);

        // 将被删除的key同步到其他服务
        redisTemplateClient.publish(ConstantConfig.Cache.ChanelEnum.CHANNEL_CACHE, CacheRefreshModel.builder()
                .cacheKey(key)
                .build());
        return ConvertUtil.convert(cache);
    }

    /**
     * 根据缓存前缀清空所有指定缓存(会清空所有本地缓存和所有符合缓存前缀的redis缓存)
     *
     * @param prefix 缓存前缀(xx*)
     */
    public void clear(String prefix) {
        // 清空本地缓存Map对象
        getLocalCacheMap().clear();

        // 同步清理其他服务中的缓存数据
        redisTemplateClient.publish(ConstantConfig.Cache.ChanelEnum.CHANNEL_CACHE, CacheRefreshModel.builder()
                .clear(Boolean.TRUE)
                .build());

        Set<String> keys = redisTemplateClient.scan(prefix);
        if (CollectionUtil.isEmpty(keys)) {
            return;
        }

        // 清空所有符合条件的redis缓存
        for (String key : keys) {
            redisTemplateClient.delete(key);
        }
    }

    /**
     * 获取缓存中所有实际的key(这里不包括jvm缓存内的key数量)
     *
     * @param prefix 缓存前缀(xx*)
     * @param <T>    值的类型
     * @return 缓存中所有实际的key
     */
    public <T> Set<T> keys(String prefix) {
        try {
            // 获取缓存中实际存在的key
            Set<String> keys = redisTemplateClient.scan(prefix);
            Set<T> convertedKeys = new HashSet<>();
            for (String key : keys) {
                convertedKeys.add(ConvertUtil.convert(key));
            }
            return convertedKeys;
        } catch (Exception e) {
            log.error("get keys error", e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取Redis客户端操作工具方法
     *
     * @return Redis客户端操作相关工具类
     */
    public RedisTemplateClient getRedisTemplateClient() {
        return redisTemplateClient;
    }

    /**
     * Redis消息订阅者收到的消息
     *
     * @param message redis消息体
     * @param channel 当前消息体对应的通道
     */
    @Override
    public void receiveMessage(byte[] message, String channel) {
        // 缓存删除同步时触发
        if (ConstantConfig.Cache.ChanelEnum.CHANNEL_CACHE.getChannel().equals(channel)) {
            // 获取需要刷新的缓存
            Optional.ofNullable((CacheRefreshModel) redisTemplateClient.getValueSerializer().deserialize(message)).ifPresent(cacheRefreshModel -> {
                // 获取本地缓存对象
                TimedCache<String, Object> localCacheMap = getLocalCacheMap();

                if (cacheRefreshModel.getClear()) {
                    // 清空所有本地缓存的对象
                    localCacheMap.clear();
                }
                if (StringUtils.isNotBlank(cacheRefreshModel.getCacheKey())) {
                    // 删除本地缓存对象
                    localCacheMap.remove(cacheRefreshModel.getCacheKey());
                }
            });
        }
    }

    // ==================================================== private ====================================================

    /**
     * 获取jvm的本地缓存Map对象，如果不存在，则会创建一个新的HashMap对象
     *
     * @return 本地缓存中的Map对象
     */
    private static TimedCache<String, Object> getLocalCacheMap() {
        if (TIMED_LOCAL_CACHE == null) {
            // 创建超时缓存
            TIMED_LOCAL_CACHE = new TimedCache<>(CACHE_TIMEOUT);
            // 启动一个定时任务，每5秒清理一次过期条目
            TIMED_LOCAL_CACHE.schedulePrune(NumberConstant.LONG_FIVE * NumberConstant.LONG_ONE_THOUSAND);
        }
        return TIMED_LOCAL_CACHE;
    }
}
