package com.doudoudrive.auth.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>重写Shiro鉴权框架会话侦听器配置</p>
 * <p>2022-04-21 15:12</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class ShiroSessionListener implements SessionListener {

    private RedisSessionDAO redisSessionDAO;

    @Autowired
    public void setRedisSessionDAO(RedisSessionDAO redisSessionDAO) {
        this.redisSessionDAO = redisSessionDAO;
    }

    /**
     * 会话创建时触发
     *
     * @param session the session that has started.
     */
    @Override
    public void onStart(Session session) {
        if (log.isDebugEnabled()) {
            log.debug("Create session {} ", session.getId());
        }
    }

    /**
     * 会话被停止时触发
     *
     * @param session the session that has stopped.
     */
    @Override
    public void onStop(Session session) {
        if (log.isDebugEnabled()) {
            log.debug("Stop session {} ", session.getId());
        }
        this.redisSessionDAO.delete(session);
    }

    /**
     * 会话过期时触发
     *
     * @param session the session that has expired.
     */
    @Override
    public void onExpiration(Session session) {
        if (log.isDebugEnabled()) {
            log.debug("Expiration session {} ", session.getId());
        }
        this.redisSessionDAO.delete(session);
    }
}
