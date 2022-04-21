package com.doudoudrive.auth.manager;

import com.doudoudrive.common.cache.RedisTemplateClient;
import org.apache.shiro.session.Session;

import java.util.Collection;
import java.util.Set;

/**
 * <p>shiro鉴权框架服务缓存信息通用业务处理层接口</p>
 * <p>2022-04-20 21:01</p>
 *
 * @author Dan
 **/
public interface ShiroCacheManager {

    /**
     * 获取缓存中的值
     *
     * @param key 指定的key
     * @param <T> 值的类型
     * @return 根据key从缓存中查找到的缓存对象
     */
    <T> T getCache(String key);

    /**
     * 批量获取缓存对象信息
     *
     * @param keys 指定的key
     * @param <T>  值的类型
     * @return 根据key从缓存中查找到的缓存对象集合
     */
    <T> Collection<T> getCache(Set<String> keys);

    /**
     * 向缓存中插入值
     *
     * @param key    缓存中的key值
     * @param value  缓存对象
     * @param expire 缓存过期时间，为NULL时不设置过期时间(秒)
     */
    void putCache(String key, Object value, Long expire);

    /**
     * 从缓存中删除数据
     *
     * @param key 指定删除缓存中的key值
     * @param <T> 值的类型
     * @return 返回被删除的缓存对象，如果缓存对象不存在，则返回null
     */
    <T> T removeCache(String key);

    /**
     * 根据缓存前缀清空所有指定缓存(会清空所有本地缓存)
     *
     * @param prefix 缓存前缀(xx*)
     */
    void clear(String prefix);

    /**
     * 获取缓存中所有实际的key(这里不包括jvm缓存内的key数量)
     *
     * @param prefix 缓存前缀(xx*)
     * @param <T>    值的类型
     * @return 缓存中所有实际的key
     */
    <T> Set<T> keys(String prefix);

    /**
     * 获取Redis客户端操作工具方法
     *
     * @return Redis客户端操作相关工具类
     */
    RedisTemplateClient getRedisTemplateClient();

    /**
     * 从当前session中获取存储与session的用户名
     *
     * @param session 当前用户会话
     * @return 返回用户登录的用户名信息，不存在返回空字符串
     */
    String getUsername(Session session);

}
