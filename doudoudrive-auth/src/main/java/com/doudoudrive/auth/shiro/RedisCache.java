package com.doudoudrive.auth.shiro;

import com.doudoudrive.auth.manager.ShiroCacheManager;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessException;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.util.lang.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * <p>重写shiro的缓存获取配置，使用Redis 设置、获取 授权信息</p>
 * <p>2022-04-17 16:22</p>
 *
 * @author Dan
 **/
@Slf4j
public class RedisCache<k, V> implements Cache<k, V> {

    /**
     * shiro鉴权服务框架缓存实现
     */
    private final ShiroCacheManager shiroCacheManager;

    /**
     * 缓存前缀
     */
    private String keyPrefix = ConstantConfig.Cache.DEFAULT_CACHE_KEY_PREFIX;

    /**
     * 缓存过期时间
     */
    private final Long expire;

    /**
     * 缓存默认key值字段名称
     */
    private String principalIdFieldName = ConstantConfig.Cache.DEFAULT_PRINCIPAL_ID_FIELD_NAME;

    /**
     * RedisCache 默认构造方法
     *
     * @param shiroCacheManager    shiro鉴权服务框架缓存实现
     * @param prefix               缓存前缀
     * @param expire               缓存过期时间
     * @param principalIdFieldName 缓存默认key值字段名称
     */
    public RedisCache(ShiroCacheManager shiroCacheManager, String prefix, Long expire, String principalIdFieldName) {
        if (ObjectUtils.isEmpty(shiroCacheManager)) {
            throw new IllegalArgumentException("shiroCacheManager cannot be null.");
        }
        this.shiroCacheManager = shiroCacheManager;
        if (StringUtils.isNotBlank(prefix)) {
            this.keyPrefix = prefix;
        }
        this.expire = expire;
        if (StringUtils.isNotBlank(principalIdFieldName)) {
            this.principalIdFieldName = principalIdFieldName;
        }
    }

    /**
     * 从缓存中查找指定缓存缓存对象
     *
     * @param key 指定的key值
     * @return 返回key值对应的value
     * @throws CacheException 抛出获取缓存异常
     */
    @Override
    public V get(k key) throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("get key, [{}]", key);
        }

        if (key == null) {
            return null;
        }

        try {
            return shiroCacheManager.getCache(getRedisCacheKey(key));
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * 向缓存中插入值
     *
     * @param key   指定的key值
     * @param value 需要插入的value
     * @return 返回插入的value
     * @throws CacheException 抛出插入缓存异常
     */
    @Override
    public V put(k key, V value) throws CacheException {
        if (ObjectUtils.isEmpty(key)) {
            log.warn("Saving a null key is meaningless, return value directly without call Redis.");
            return value;
        }

        try {
            if (log.isDebugEnabled()) {
                log.debug("put key, [{}]", key);
            }

            shiroCacheManager.putCache(getRedisCacheKey(key), value, expire);
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * 从redis中删除指定缓存
     *
     * @param key 指定的key值
     * @return 返回被删除的value值
     * @throws CacheException 抛出删除缓存异常
     */
    @Override
    public V remove(k key) throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("remove key, [{}]", key);
        }

        if (ObjectUtils.isEmpty(key)) {
            return null;
        }

        try {
            // 删除缓存对象，同时返回被删除的值
            return shiroCacheManager.removeCache(getRedisCacheKey(key));
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * 清空缓存
     *
     * @throws CacheException 抛出删除缓存异常
     */
    @Override
    public void clear() throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("clear cache");
        }

        try {
            // 清空所有缓存
            shiroCacheManager.clear(this.keyPrefix + ConstantConfig.SpecialSymbols.ASTERISK);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * 获取所有 有授权的 key 值数量
     *
     * @return 有授权的 key 值数量
     */
    @Override
    public int size() {
        if (log.isDebugEnabled()) {
            log.debug("get key size");
        }

        try {
            return shiroCacheManager.getRedisTemplateClient().scanSize(this.keyPrefix + ConstantConfig.SpecialSymbols.ASTERISK).intValue();
        } catch (Exception e) {
            log.error("get key error:{}", e.getMessage());
            return NumberConstant.INTEGER_ZERO;
        }
    }

    /**
     * 获取缓存中所有实际的key
     *
     * @return 缓存中所有实际的key的Set集合
     */
    @Override
    public Set<k> keys() {
        if (log.isDebugEnabled()) {
            log.debug("get keys");
        }
        return shiroCacheManager.keys(this.keyPrefix + ConstantConfig.SpecialSymbols.ASTERISK);
    }

    /**
     * 获取缓存中所有实际的value
     *
     * @return 缓存中所有实际的value的集合
     */
    @Override
    public Collection<V> values() {
        if (log.isDebugEnabled()) {
            log.debug("get values");
        }

        try {
            // 获取所有的key
            Set<String> keys = shiroCacheManager.getRedisTemplateClient().scan(this.keyPrefix + ConstantConfig.SpecialSymbols.ASTERISK);
            if (CollectionUtils.isEmpty(keys)) {
                return Collections.emptySet();
            }

            // 根据key获取缓存中的值
            return shiroCacheManager.getCache(keys);
        } catch (Exception e) {
            log.error("get values error", e);
            return Collections.emptySet();
        }
    }

    // ==================================================== private ====================================================

    /**
     * 获取完整的Redis key值，包括key的前缀
     *
     * @param key key值
     * @return redis缓存key值
     */
    private String getRedisCacheKey(k key) {
        if (ObjectUtils.isEmpty(key)) {
            return null;
        }
        // 获取缓存key值字符串
        String stringRedisKey = key instanceof PrincipalCollection ? getRedisKeyFromPrincipalIdField((PrincipalCollection) key) : key.toString();
        // 缓存key值拼接
        return this.keyPrefix + stringRedisKey;
    }

    /**
     * 从PrincipalCollection中获取缓存key值
     *
     * @param key 一个PrincipalCollection实例
     * @return 从PrincipalCollection中获取到的缓存key值
     */
    private String getRedisKeyFromPrincipalIdField(PrincipalCollection key) {
        Object principalObject = key.getPrimaryPrincipal();
        if (principalObject == null) {
            throw new BusinessException(StatusCodeEnum.PRINCIPAL_INSTANCE_EXCEPTION);
        }

        // 如果是字符串类型，直接返回toString
        if (principalObject instanceof String) {
            return principalObject.toString();
        }

        if (StringUtils.isBlank(this.principalIdFieldName)) {
            throw new BusinessException(StatusCodeEnum.CACHE_MANAGER_PRINCIPAL_ID_NOT_ASSIGNED_EXCEPTION);
        }
        // 获取方法名
        String principalIdMethodName = ReflectUtil.GET
                + this.principalIdFieldName.substring(NumberConstant.INTEGER_ZERO, NumberConstant.INTEGER_ONE).toUpperCase()
                + this.principalIdFieldName.substring(NumberConstant.INTEGER_ONE);
        try {
            // 获取Principal方法对象
            Method principalIdGetter = principalObject.getClass().getMethod(principalIdMethodName);
            Object object = principalIdGetter.invoke(principalObject);
            if (ObjectUtils.isEmpty(object)) {
                throw new BusinessException(StatusCodeEnum.PRINCIPAL_INSTANCE_EXCEPTION);
            }
            return object.toString();
        } catch (Exception e) {
            log.error("{} must has getter for field: {}", principalObject.getClass(), this.principalIdFieldName);
            throw new BusinessException(StatusCodeEnum.PRINCIPAL_INSTANCE_EXCEPTION);
        }
    }
}
