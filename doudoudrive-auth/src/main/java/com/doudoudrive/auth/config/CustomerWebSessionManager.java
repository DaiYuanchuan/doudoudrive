package com.doudoudrive.auth.config;

import com.doudoudrive.common.constant.ConstantConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * <p>重写shiro原有的默认Web会话管理器</p>
 * <p>2021-10-22 21:16</p>
 *
 * @author Dan
 **/
@Slf4j
public class CustomerWebSessionManager extends DefaultWebSessionManager {

    public CustomerWebSessionManager() {
        super();
    }

    /**
     * 重写父类获取sessionID的方法 从请求头中取出指定鉴权字段
     * 如果不能冲请求头中获取指定字段 ，则使用父类方法从Cookie中获取鉴权字段
     *
     * @param request  请求头
     * @param response 响应体
     * @return 序列化接口
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        if (!(request instanceof HttpServletRequest)) {
            log.debug("Current request is not an HttpServletRequest - cannot get session ID cookie.  Returning null.");
            return null;
        }

        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        // 从请求头中获取指定的鉴权字段
        String authorization = httpRequest.getHeader(ConstantConfig.HttpRequest.TOKEN);
        if (StringUtils.isNotBlank(authorization)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ConstantConfig.HttpRequest.HEADER);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, authorization);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            // 始终设置重写标志
            request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, isSessionIdUrlRewritingEnabled());
            return authorization;
        }
        return super.getSessionId(request, response);
    }

    /**
     * 获取session，优化单次请求需要多次访问redis的问题
     * 重写了父类的retrieveSession方法，在 Web 下使用 shiro 时，可以把 session 对象放入当前请求对象中
     *
     * @param sessionKey 会话key值
     * @return 返回一个标准的会话
     * @throws UnknownSessionException 抛出未知会话异常
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);

        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }

        if (request != null && null != sessionId) {
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                if (log.isDebugEnabled()) {
                    log.debug("read session from request");
                }
                return (Session) sessionObj;
            }
        }

        Session session = super.retrieveSession(sessionKey);
        if (request != null && null != sessionId) {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }
}
