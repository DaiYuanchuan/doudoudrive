package com.doudoudrive.auth.config;

import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.util.http.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * <p>重写shiro身份验证过滤器</p>
 * <p>取消原有的重定向配置 ，改为直接返回JSON的形式</p>
 * <p>2020-10-26 10:14</p>
 *
 * @author Dan
 **/
@Slf4j
public class ShiroAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (this.isLoginRequest(request, response)) {
            if (this.isLoginSubmission(request, response)) {
                if (log.isTraceEnabled()) {
                    log.trace("Login submission detected.  Attempting to execute login.");
                }

                return this.executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Login page view.");
                }

                return true;
            }
        } else {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            if (req.getMethod().equals(RequestMethod.OPTIONS.name())) {
                resp.setStatus(HttpStatus.OK.value());
                return true;
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Attempting to access a path which requires authentication.  Forwarding to the Authentication url [{}]", this.getLoginUrl());
                }
                resp.setStatus(200);
                resp.setContentType("application/json; charset=UTF-8");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.println(JSONObject.toJSONString(Result.refuse()));
                out.flush();
                out.close();
                return false;
            }
        }
    }
}