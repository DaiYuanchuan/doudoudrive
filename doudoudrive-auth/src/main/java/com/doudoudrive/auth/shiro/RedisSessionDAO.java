package com.doudoudrive.auth.shiro;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.ShiroAuthenticationModel;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.RedisSerializerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * <p>Redis会话管理器，针对请求Session的ShiroSession的CRUD操作</p>
 * <p>这里主要是在组件之间提供了一个透明的缓存层，使用它和底层的session会话存储系统隔离，无法从本地缓存层中查询时，会走Redis查询</p>
 * <p>2022-04-17 19:27</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class RedisSessionDAO extends AbstractSessionDAO {

    /**
     * 负责缓存会话的Cache实例
     */
    private CacheManagerConfig cacheManagerConfig;

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    /**
     * 序列化工具
     */
    private static final RedisSerializerUtil<ShiroSession> SERIALIZER = new RedisSerializerUtil<>();

    /**
     * The session key that is used to store subject principals.
     */
    private static final String PRINCIPALS_SESSION_KEY = DefaultSubjectContext.class.getName() + "_PRINCIPALS_SESSION_KEY";


    /**
     * 更新当前会话
     *
     * @param session the Session to update
     * @throws UnknownSessionException 未知会话异常
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        try {
            if (session instanceof ValidatingSession validatingSession && !validatingSession.isValid()) {
                return;
            }

            if (session instanceof ShiroSession shiroSession) {
                // 如果没有主要字段(除lastAccessTime以外其他字段)发生改变
                if (!shiroSession.isChanged()) {
                    return;
                }
                // 如果没有返回 证明有调用 setAttribute往redis 放的时候永远设置为false
                shiroSession.setChanged(false);
            }

            this.saveSession(session);
        } catch (Exception e) {
            log.error("update Session is failed:{}", e.getMessage());
            throw new UnknownSessionException(e);
        }
    }

    /**
     * 会话删除
     *
     * @param session the session to delete.
     */
    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        try {
            this.deleteSessionFromCache(session.getId().toString());
        } catch (Exception e) {
            log.error("delete session error. session id:{} error:{}", session.getId(), e.getMessage());
        }
    }

    /**
     * 获取EIS中被视为活动的所有会话，即尚未停止/过期的会话
     *
     * @return 返回当前活动的所有会话
     */
    @Override
    public Collection<Session> getActiveSessions() {
        // 获取缓存中所有的key
        Set<String> keys = cacheManagerConfig.keys(ConstantConfig.Cache.DEFAULT_CACHE_KEY_PREFIX + ConstantConfig.SpecialSymbols.ASTERISK);
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        Collection<ShiroSession> shiroSessionCollection = new ArrayList<>(keys.size());
        for (String key : keys) {
            shiroSessionCollection.add(this.getSessionFromCache(key));
        }

        if (CollectionUtil.isEmpty(shiroSessionCollection)) {
            return Collections.emptySet();
        }
        return new HashSet<>(shiroSessionCollection);
    }

    /**
     * 创建一个会话，获取一个会话id
     *
     * @param session the Session instance to persist to the EIS.
     * @return 一个会话id
     */
    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            log.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    /**
     * 通过会话id查询当前会话
     *
     * @param sessionId the id of the <tt>Session</tt> to retrieve.
     * @return 一个会话
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            log.warn("session id is null");
            return null;
        }

        // 从缓存中获取指定的session对象
        return this.getSessionFromCache(sessionId.toString());
    }

    // ==================================================== private ====================================================

    /**
     * 保存session方法
     *
     * @param session 需要保存的会话对象
     * @throws UnknownSessionException 抛出未知会话异常
     */
    private void saveSession(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }

        // 向缓存中插入值
        this.putSessionFromCache(session.getId().toString(), (ShiroSession) session);
    }

    /**
     * 从缓存中获取指定的session对象，根据会话标识
     *
     * @param sessionId 需要获取session对象的会话标识
     * @return 通过会话标识查找到的session对象
     */
    private ShiroSession getSessionFromCache(String sessionId) {
        // 从缓存中获取到shiro鉴权对象
        ShiroAuthenticationModel shiroAuthenticationModel = cacheManagerConfig.getCache(ConstantConfig.Cache.DEFAULT_CACHE_KEY_PREFIX + sessionId);
        if (shiroAuthenticationModel == null) {
            return null;
        }
        // 反序列化其中的session对象
        return SERIALIZER.deserialize(shiroAuthenticationModel.getSession());
    }

    /**
     * 根据会话标识删除指定会话缓存对象
     *
     * @param sessionId 会话标识
     */
    private void deleteSessionFromCache(String sessionId) {
        cacheManagerConfig.removeCache(ConstantConfig.Cache.DEFAULT_CACHE_KEY_PREFIX + sessionId);
    }

    /**
     * 向缓存中存入指定的session对象信息
     *
     * @param sessionId    会话标识
     * @param shiroSession 需要往缓存中存入的session对象数据
     */
    private void putSessionFromCache(String sessionId, ShiroSession shiroSession) {
        // 构建shiro鉴权对象
        ShiroAuthenticationModel shiroAuthenticationModel = ShiroAuthenticationModel.builder()
                .sessionId(sessionId)
                // 从当前session中获取到当前登录的用户名
                .username(this.getUsername(shiroSession))
                // 序列化当前session对象
                .session(SERIALIZER.serialize(shiroSession))
                .build();
        // 往缓存中插入session对象
        cacheManagerConfig.putCache(ConstantConfig.Cache.DEFAULT_CACHE_KEY_PREFIX + sessionId, shiroAuthenticationModel, ConstantConfig.Cache.DEFAULT_EXPIRE);
    }

    /**
     * 从当前session中获取存储与session的用户名
     *
     * @param session 当前用户会话
     * @return 返回用户登录的用户名信息，不存在返回空字符串
     */
    private String getUsername(Session session) {
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
}

