package com.doudoudrive.auth.manager.impl;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.auth.manager.ShiroCacheManager;
import com.doudoudrive.auth.model.dto.ShiroCache;
import com.doudoudrive.common.cache.RedisTemplateClient;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>shiro鉴权框架服务缓存信息通用业务处理层接口实现</p>
 * <p>2022-04-20 21:03</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("shiroCacheManager")
public class ShiroCacheManagerImpl implements ShiroCacheManager {

    /**
     * Redis客户端操作相关工具类
     */
    private RedisTemplateClient redisTemplateClient;

    @Autowired
    public void setRedisTemplateClient(RedisTemplateClient redisTemplateClient) {
        this.redisTemplateClient = redisTemplateClient;
    }

    /**
     * The session key that is used to store subject principals.
     */
    private static final String PRINCIPALS_SESSION_KEY = DefaultSubjectContext.class.getName() + "_PRINCIPALS_SESSION_KEY";

    /**
     * 当前jvm的本地缓存线程
     */
    private static final FastThreadLocal<TimedCache<String, ShiroCache>> LOCAL_CACHE = new FastThreadLocal<>();

    /**
     * 定义本地缓存超时时间(1小时)(毫秒)
     */
    private static final long CACHE_TIMEOUT = 3600000L;

    /**
     * 获取缓存中的值
     *
     * @param key 指定的key
     * @param <T> 值的类型
     * @return 根据key从缓存中查找固定的值
     */
    @Override
    public <T> T getCache(String key) {
        // 获取本地缓存Map对象
        TimedCache<String, ShiroCache> localCacheMap = getLocalCacheMap();

        // 从map中获取指定缓存对象
        ShiroCache shiroCache = localCacheMap.get(key, false);
        // 本地缓存为空时，从redis中获取
        if (shiroCache == null) {
            // 构建临时缓存对象
            shiroCache = ShiroCache.builder()
                    .cache(redisTemplateClient.get(key))
                    .createTime(System.currentTimeMillis())
                    .build();
            // 将缓存对象存储到临时缓存中去
            localCacheMap.put(key, shiroCache);
            return convert(shiroCache.getCache());
        }

        // 获取当前缓存对象
        return convert(shiroCache.getCache());
    }

    /**
     * 批量获取缓存对象信息
     *
     * @param keys 指定的key
     * @param <T>  值的类型
     * @return 根据key从缓存中查找到的缓存对象集合
     */
    @Override
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
     * 向缓存中插入值
     *
     * @param key    缓存中的key值
     * @param value  缓存对象
     * @param expire 缓存过期时间，为NULL时不设置过期时间(秒)
     */
    @Override
    public void putCache(String key, Object value, Long expire) {
        if (!ObjectUtils.allNotNull(key, value)) {
            return;
        }

        // 获取本地缓存Map对象
        TimedCache<String, ShiroCache> localCacheMap = getLocalCacheMap();
        // 将临时缓存对象存储到Jvm缓存中去
        localCacheMap.put(key, ShiroCache.builder()
                .cache(value)
                .createTime(System.currentTimeMillis())
                .build());

        // 向redis中插入缓存数据
        redisTemplateClient.set(key, value, expire);
    }

    /**
     * 从缓存中删除数据
     *
     * @param key 指定删除缓存中的key值
     * @param <T> 值的类型
     * @return 返回被删除的缓存对象，如果缓存对象不存在，则返回null
     */
    @Override
    public <T> T removeCache(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // 缓存对象
        Object cache;

        // 获取本地缓存Map对象
        TimedCache<String, ShiroCache> localCacheMap = getLocalCacheMap();
        ShiroCache shiroCache = localCacheMap.get(key, false);
        if (shiroCache != null) {
            // 从map中获取到本地缓存对象
            cache = shiroCache.getCache();
            localCacheMap.remove(key);
        } else {
            // 从redis中获取到缓存对象
            cache = redisTemplateClient.get(key);
        }

        // 删除redis中的缓存对象
        redisTemplateClient.delete(key);
        return convert(cache);
    }

    /**
     * 根据缓存前缀清空所有指定缓存(会清空所有本地缓存)
     *
     * @param prefix 缓存前缀(xx*)
     */
    @Override
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
    @Override
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
    @Override
    public RedisTemplateClient getRedisTemplateClient() {
        return redisTemplateClient;
    }

    /**
     * 从当前session中获取存储与session的用户名
     *
     * @param session 当前用户会话
     * @return 返回用户登录的用户名信息，不存在返回空字符串
     */
    @Override
    public String getUsername(Session session) {
        // 从当前session中获取PrincipalCollection对象
        Object principalsObject = session.getAttribute(PRINCIPALS_SESSION_KEY);
        if (ObjectUtils.isNotEmpty(principalsObject)) {
            try {
                PrincipalCollection principals = (PrincipalCollection) principalsObject;
                return principals.getPrimaryPrincipal().toString();
            } catch (Exception e) {
                return CharSequenceUtil.EMPTY;
            }
        }
        return CharSequenceUtil.EMPTY;
    }

    // ==================================================== private ====================================================

    /**
     * 获取jvm的本地缓存Map对象，如果不存在，则会创建一个新的HashMap对象
     *
     * @return 本地缓存中的Map对象
     */
    private static TimedCache<String, ShiroCache> getLocalCacheMap() {
        TimedCache<String, ShiroCache> localCacheMap = LOCAL_CACHE.get();
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
        return (T) object;
    }
}
