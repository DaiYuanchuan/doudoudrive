package com.doudoudrive.auth.shiro;

import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>Redis缓存管理器，重写了原有shiro的CacheManager</p>
 * <p>2022-04-17 16:27</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class RedisCacheManager implements CacheManager {

    /**
     * 服务框架缓存实现
     */
    private CacheManagerConfig cacheManagerConfig;

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    @Override
    public <k, V> Cache<k, V> getCache(String name) throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("get cache, name={}", name);
        }

        // 获取本地缓存中的值
        Cache<k, V> cache = cacheManagerConfig.getCacheFromLocal(name);
        if (cache == null) {
            cache = new RedisCache<>(cacheManagerConfig, ConstantConfig.Cache.DEFAULT_CACHE_REALM_PREFIX,
                    ConstantConfig.Cache.DEFAULT_EXPIRE, ConstantConfig.Cache.DEFAULT_PRINCIPAL_ID_FIELD_NAME);
            cacheManagerConfig.putCacheFromLocal(name, cache);
        }
        return cache;
    }
}
