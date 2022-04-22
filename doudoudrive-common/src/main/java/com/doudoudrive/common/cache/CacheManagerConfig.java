package com.doudoudrive.common.cache;

import cn.hutool.cache.impl.TimedCache;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import io.netty.util.concurrent.FastThreadLocal;
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
public class CacheManagerConfig {

    /**
     * Redis客户端操作相关工具类
     */
    private RedisTemplateClient redisTemplateClient;

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    /**
     * 当前jvm的本地缓存线程
     */
    private static final FastThreadLocal<TimedCache<String, Object>> LOCAL_CACHE = new FastThreadLocal<>();

    /**
     * 定义本地缓存超时时间(1小时)(毫秒)
     */
    private static final long CACHE_TIMEOUT = 3600000L;

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
        Object cache = localCacheMap.get(key, false);
        // 当本地缓存为空时，尝试从redis中获取
        if (cache == null) {
            cache = redisTemplateClient.get(key);
            // 将缓存对象存储到临时缓存中去
            localCacheMap.put(key, cache);
        }

        // 获取当前缓存对象
        return convert(cache);
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
        return convert(localCacheMap.get(key, false));
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
        Object cache = Optional.ofNullable(localCacheMap.get(key, false)).orElse(redisTemplateClient.get(key));

        // 如果从本地缓存、redis缓存中都无法获取到值时，直接返回null
        if (cache == null) {
            return null;
        }

        // 删除本地缓存对象
        localCacheMap.remove(key);
        // 删除redis中的缓存对象
        redisTemplateClient.delete(key);
        return convert(cache);
    }

    /**
     * 根据缓存前缀清空所有指定缓存(会清空所有本地缓存和所有符合缓存前缀的redis缓存)
     *
     * @param prefix 缓存前缀(xx*)
     */
    public void clear(String prefix) {
        // 清空本地缓存Map对象
        getLocalCacheMap().clear();

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
                convertedKeys.add(convert(key));
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

    // ==================================================== private ====================================================

    /**
     * 获取jvm的本地缓存Map对象，如果不存在，则会创建一个新的HashMap对象
     *
     * @return 本地缓存中的Map对象
     */
    private static TimedCache<String, Object> getLocalCacheMap() {
        TimedCache<String, Object> localCacheMap = LOCAL_CACHE.get();
        if (localCacheMap == null) {
            // 创建超时缓存
            localCacheMap = new TimedCache<>(CACHE_TIMEOUT);
            // 启动一个定时任务，每5秒清理一次过期条目
            localCacheMap.schedulePrune(NumberConstant.LONG_FIVE * NumberConstant.LONG_ONE_THOUSAND);
            LOCAL_CACHE.set(localCacheMap);
        }
        return localCacheMap;
    }

    /**
     * 对象类型强制转换
     *
     * @param object 待转换的对象
     * @param <T>    转换强制的类型
     * @return 输出强制转换后的类型
     */
    @SuppressWarnings("unchecked")
    private static <T> T convert(Object object) {
        try {
            return (T) object;
        } catch (Exception e) {
            log.error("object cast exception:", e);
            return null;
        }
    }
}
