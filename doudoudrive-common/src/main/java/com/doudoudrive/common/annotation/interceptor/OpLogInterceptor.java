package com.doudoudrive.common.annotation.interceptor;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.log.OpLogCompletionHandler;
import com.doudoudrive.common.model.dto.model.OpLogInfo;
import com.doudoudrive.common.model.dto.model.Region;
import com.doudoudrive.common.util.ip.IpUtils;
import com.doudoudrive.common.util.lang.SpiderUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * <p>操作日志的注解实现类</p>
 * <p>2022-03-14 22:17</p>
 *
 * @author Dan
 **/
@Slf4j
@Aspect
@Component
public class OpLogInterceptor implements ApplicationContextAware, InitializingBean {

    /**
     * 当前钩子挂载的所有插件
     */
    protected Map<String, OpLogCompletionHandler> plugins;

    /**
     * 应用程序上下文，通过Spring自动注入
     */
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        plugins = applicationContext.getBeansOfType(OpLogCompletionHandler.class);
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.doudoudrive.common.annotation.OpLog)")
    public void opLog() {
    }

    /**
     * 前置通知 在目标方法执行前触发
     *
     * @param joinPoint 切点
     */
    @Before(value = "opLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 获得注解信息
        OpLog opLog = getAnnotation(joinPoint);
        // 打印请求类、方法、参数等信息
        String className = formatClassName(joinPoint.getTarget().getClass().getName());
        String requestParameter = (opLog != null && opLog.isSaveRequestData()) ? getRequestParameter(joinPoint) : CharSequenceUtil.EMPTY;
        log.debug("开始调用--> {}.{} 参数:{}", className, joinPoint.getSignature().getName(), requestParameter);
    }

    /**
     * 后置通知，在将返回值返回时执行
     *
     * @param joinPoint 切点
     * @param result    响应参数
     */
    @AfterReturning(pointcut = "opLog()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            handleLog(joinPoint, null, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常信息
     */
    @AfterThrowing(value = "opLog()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        try {
            handleLog(joinPoint, e, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 处理日志信息
     *
     * @param joinPoint 切点
     * @param e         异常信息
     * @param result    响应参数信息
     */
    private void handleLog(final JoinPoint joinPoint, final Exception e, final Object result) {
        // 获得注解信息
        OpLog opLog = getAnnotation(joinPoint);
        if (opLog == null) {
            log.debug("Failed to get the annotation information correctly...");
            return;
        }

        // 初始化日志信息
        OpLogInfo opLogInfo = initInfo(opLog.title(), opLog.isGetIp(), e);
        // 设置业务类型、类名、方法名等
        opLogInfo.setBusinessType(opLog.businessType());
        opLogInfo.setClassName(formatClassName(joinPoint.getTarget().getClass().getName()));
        opLogInfo.setMethodName(joinPoint.getSignature().getName());

        // 是否需要保存URL的请求参数
        if (opLog.isSaveRequestData()) {
            opLogInfo.setParameter(getRequestParameter(joinPoint));
        }

        // 日志处理完成后执行任务回调
        callback(opLogInfo, result);
    }

    /**
     * 获取当前的请求参数信息
     *
     * @param joinPoint 连接点
     * @return 请求参数
     */
    private String getRequestParameter(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            // 如果获取到的请求参数为空
            if (MapUtil.isEmpty(requestAttributes.getRequest().getParameterMap())) {
                Object[] args = joinPoint.getArgs();
                // 判断是否为空
                if (ArrayUtil.isEmpty(args)) {
                    return CharSequenceUtil.EMPTY;
                } else {
                    List<Object> objectList = Arrays.asList(args);
                    String parameter;
                    try {
                        parameter = JSON.toJSONString(objectList.stream()
                                .filter(arg -> (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)))
                                .collect(Collectors.toList()));
                    } catch (Exception exception) {
                        // 如果序列化出现异常时 ，使用空的参数
                        parameter = CharSequenceUtil.EMPTY;
                    }
                    // 尝试获取过滤后body中的参数
                    return parameter;
                }
            } else {
                return JSONObject.toJSONString(requestAttributes.getRequest().getParameterMap());
            }
        } else {
            return "无法获取request信息";
        }
    }

    /**
     * 将日志信息通过回调函数接口抛出
     *
     * @param opLogInfo 注解获取到的操作日志信息
     * @param result    响应参数
     */
    private void callback(OpLogInfo opLogInfo, Object result) {
        // 格式化响应参数
        String responseParameter;
        try {
            responseParameter = JSONObject.toJSONStringWithDateFormat(result, DatePattern.NORM_DATETIME_PATTERN, SerializerFeature.WriteMapNullValue);
        } catch (Exception e) {
            responseParameter = CharSequenceUtil.EMPTY;
        }
        log.debug("结束调用<-- {}.{} 返回值:{}", opLogInfo.getClassName(), opLogInfo.getMethodName(), responseParameter);
        ThreadUtil.execAsync(() -> {
            try {
                // 异步回调所有实现了回调接口的类
                plugins.values().forEach(plugin -> plugin.complete(opLogInfo));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 判断是否存在注解 存在就获取注解信息
     *
     * @param joinPoint 切点信息
     * @return 如果存在则返回注解信息，否则返回NULL
     */
    private static OpLog getAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(OpLog.class);
        }
        return null;
    }

    /**
     * 初始化日志信息
     *
     * @param title   模块名称
     * @param isGetIp 是否需要获取用户IP的实际地理位置
     * @param e       异常信息
     * @return 返回带有默认数据的日志信息
     */
    private static OpLogInfo initInfo(final String title, final boolean isGetIp, final Exception e) {
        // 获取Request信息
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        // 初始化日志信息
        OpLogInfo opLogInfo = OpLogInfo.builder()
                .ip("127.0.0.1")
                .requestUri("")
                .location("0-0-内网IP 内网IP")
                .build();

        String userAgent = "无法获取User-Agent信息";

        if (requestAttributes != null) {
            userAgent = requestAttributes.getRequest().getHeader("User-Agent");
            opLogInfo.setIp(ServletUtil.getClientIP(requestAttributes.getRequest()));
            opLogInfo.setRequestUri(requestAttributes.getRequest().getRequestURI());
            opLogInfo.setMethod(requestAttributes.getRequest().getMethod());
            opLogInfo.setRequest(requestAttributes.getRequest());
        }

        Future<Region> future = null;
        if (isGetIp) {
            // 异步获取IP实际地理位置信息
            future = ThreadUtil.execAsync(() -> IpUtils.getIpLocationByBtree(opLogInfo.getIp()));
        }

        // 获取/赋值 浏览器、os系统等信息
        UserAgent ua = UserAgentUtil.parse(userAgent);
        if (ua != null) {
            opLogInfo.setBrowser(ua.getBrowser().toString());
            opLogInfo.setBrowserVersion(ua.getVersion());
            opLogInfo.setBrowserEngine(ua.getEngine().toString());
            opLogInfo.setBrowserEngineVersion(ua.getEngineVersion());
            opLogInfo.setIsMobile(ua.isMobile());
            opLogInfo.setOs(ua.getOs().toString());
            opLogInfo.setPlatform(ua.getPlatform().getName());
        }
        opLogInfo.setSpider(SpiderUtil.parseSpiderType(userAgent));
        opLogInfo.setUserAgent(userAgent);

        // 访问的模块、状态、时间等
        opLogInfo.setTitle(title);
        opLogInfo.setSuccess(e != null ? Boolean.FALSE : Boolean.TRUE);
        opLogInfo.setCreateTime(new Date());

        // 访问出现的异常信息
        if (e != null) {
            if (e.getCause() != null) {
                opLogInfo.setErrorCause(e.getCause().toString());
                opLogInfo.setErrorMsg(e.getCause().getMessage());
            } else {
                opLogInfo.setErrorCause(e.getLocalizedMessage());
                opLogInfo.setErrorMsg(e.getMessage());
            }
        } else {
            opLogInfo.setErrorCause(CharSequenceUtil.EMPTY);
            opLogInfo.setErrorMsg(CharSequenceUtil.EMPTY);
        }

        if (future != null) {
            try {
                Region region = future.get();
                opLogInfo.setLocation(region.getCountry() + "-"
                        + region.getProvince() + "-" + region.getCity() + " "
                        + region.getIsp());
            } catch (Exception e1) {
                opLogInfo.setErrorCause(e1.getCause().toString());
                opLogInfo.setErrorMsg(e1.getCause().getMessage());
                opLogInfo.setSuccess(Boolean.FALSE);
            }
        }
        return opLogInfo;
    }

    /**
     * 格式化类名字符串
     *
     * @param className 类名全路径
     * @return 只获取具体类名，不获取类的路径
     */
    private String formatClassName(String className) {
        return className.replaceAll(".*?.([^.]+)$", "$1");
    }
}
