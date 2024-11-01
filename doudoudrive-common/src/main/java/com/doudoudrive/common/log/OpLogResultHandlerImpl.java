package com.doudoudrive.common.log;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.convert.LogOpInfoConvert;
import com.doudoudrive.common.model.dto.model.OpLogInfo;
import com.doudoudrive.common.model.dto.model.auth.ShiroAuthenticationModel;
import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.function.OpLogCompletionHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * <p>操作日志信息处理完成后的实现，用于对消息的持久化处理</p>
 * <p>2022-03-15 13:31</p>
 *
 * @author Dan
 **/
@Component
public class OpLogResultHandlerImpl implements OpLogCompletionHandler {

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;

    private LogOpInfoConvert logOpInfoConvert;

    private CacheManagerConfig cacheManagerConfig;

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired(required = false)
    public void setLogOpInfoConvert(LogOpInfoConvert logOpInfoConvert) {
        this.logOpInfoConvert = logOpInfoConvert;
    }

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    /**
     * 获取请求体中的鉴权字段信息
     *
     * @param request 当前正在执行的http请求体
     * @return 返回查找到的sessionId内容，没有找到时返回NULL
     */
    private static String getSessionId(HttpServletRequest request) {
        // 从 cookie 中获取指定的鉴权字段
        String cookies = getAuthCookie(request);
        if (StringUtils.isNotBlank(cookies)) {
            return cookies;
        }

        // 从请求头中获取指定的鉴权字段
        String authorization = request.getHeader(ConstantConfig.HttpRequest.TOKEN);
        if (StringUtils.isNotBlank(authorization)) {
            return authorization;
        }

        return null;
    }

    /**
     * 用于截断字符串
     *
     * @param input 输入的字符串
     * @return 返回截断后的字符串
     */
    private static String truncateString(String input) {
        if (input.length() > NumberConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE) {
            return input.substring(NumberConstant.INTEGER_ZERO, NumberConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE);
        }
        return input;
    }

    /**
     * 返回请求中具有给定鉴权字段名称的cookie，如果没有改Cookie，则返回null
     *
     * @param request 当前正在执行的http请求。
     * @return 返回请求中查找到的cookie
     */
    private static String getAuthCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ConstantConfig.HttpRequest.TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 操作日志信息处理完成后自动回调该接口
     * 此接口主要用于用户将日志信息存入MySQL、Redis等等
     *
     * @param opLogInfo 处理完成后的 操作日志实体信息
     */
    @Override
    public void complete(OpLogInfo opLogInfo) {
        if (opLogInfo.getIp().equals(ConstantConfig.HttpRequest.IPV6_LOCAL_IP)) {
            opLogInfo.setIp(ConstantConfig.HttpRequest.IPV4_LOCAL_IP);
        }

        // 数据类型转换
        LogOp logOpInfo = logOpInfoConvert.logOpConvert(opLogInfo);

        // 避免 errorCause 为 null
        logOpInfo.setErrorCause(truncateString(Optional.ofNullable(logOpInfo.getErrorCause()).orElse(CharSequenceUtil.EMPTY)));

        // 避免 errorMsg 为 null
        logOpInfo.setErrorMsg(truncateString(Optional.ofNullable(logOpInfo.getErrorMsg()).orElse(CharSequenceUtil.EMPTY)));

        // 获取当前的请求体
        HttpServletRequest request = opLogInfo.getRequest();
        // 获取当前请求中的referer字段
        logOpInfo.setReferer(Optional.ofNullable(request.getHeader(ConstantConfig.HttpRequest.REFERER))
                .map(OpLogResultHandlerImpl::truncateString)
                .orElse(CharSequenceUtil.EMPTY));

        logOpInfo.setRequestUri(truncateString(logOpInfo.getRequestUri()));

        // 获取当前请求的sessionId
        Optional.ofNullable(getSessionId(request)).ifPresent(sessionId -> {
            // 构建缓存key
            String cacheKey = ConstantConfig.Cache.DEFAULT_CACHE_KEY_PREFIX + sessionId;
            // 通过sessionId从缓存中获取到shiro鉴权对象
            ShiroAuthenticationModel shiroAuthenticationModel = cacheManagerConfig.getCache(cacheKey);
            Optional.ofNullable(shiroAuthenticationModel)
                    .ifPresent(shiroAuthentication -> {
                        logOpInfo.setUserId(shiroAuthentication.getUserId());
                        logOpInfo.setUsername(shiroAuthentication.getUsername());
                    });
        });

        // 使用one-way模式发送消息，发送端发送完消息后会立即返回
        MessageBuilder.sendOneWay(ConstantConfig.Topic.LOG_RECORD, ConstantConfig.Tag.ACCESS_LOG_RECORD, logOpInfo, rocketmqTemplate);
    }
}
