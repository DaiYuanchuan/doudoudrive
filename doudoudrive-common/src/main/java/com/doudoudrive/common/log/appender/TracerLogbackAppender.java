package com.doudoudrive.common.log.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.dto.model.SysLogMessage;
import com.doudoudrive.common.util.ip.IpUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.common.util.lang.TracerLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Logback Appender组件，可以实现将Logback日志信息发送到远程</p>
 * <p>2022-11-10 18:06</p>
 *
 * @author Dan
 **/
public class TracerLogbackAppender extends AppenderBase<ILoggingEvent> {

    /**
     * 获取本机网卡IP地址，这个地址为所有网卡中非回路地址的第一个
     */
    private static final String CURR_IP = Optional.ofNullable(IpUtils.getLocalhostStr()).orElse(StringUtils.EMPTY);

    /**
     * StringBuilder日志异常消息字符初始容量
     */
    private static final Integer STRING_INITIAL_CAPACITY = 128;

    /**
     * 应用名(从记录器上下文中获取)
     */
    private static final String APP_NAME = "appName";

    /**
     * Logback 实时日志处理事件
     *
     * @param iLoggingEvent 日志事件
     */
    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        try {
            // 获取日志信息
            SysLogMessage sysLogMessage = this.buildSysLogMessage(iLoggingEvent);
            // 发送日志信息
            TracerLogger.offerLogger(sysLogMessage);
        } catch (Exception ignored) {
        }
    }

    /**
     * 构建系统日志消息实例对象
     *
     * @param event 日志事件
     * @return 系统日志消息实例对象
     */
    private SysLogMessage buildSysLogMessage(ILoggingEvent event) {
        // MDC值的映射Map
        Map<String, String> propertyMap = event.getMDCPropertyMap();

        // 堆栈跟踪信息
        StackTraceElement stackTraceElement = event.getCallerData()[NumberConstant.INTEGER_ZERO];

        // 获取日志行信息
        String line = stackTraceElement.getFileName() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + stackTraceElement.getLineNumber();
        // 行信息与方法名结合
        String methodName = stackTraceElement.getMethodName() + ConstantConfig.SpecialSymbols.LEFT_BRACKET + line + ConstantConfig.SpecialSymbols.RIGHT_BRACKET;

        // 构建系统日志消息实例对象
        return SysLogMessage.builder()
                .businessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_LOGBACK))
                .tracerId(propertyMap.getOrDefault(ConstantConfig.LogTracer.TRACER_ID, StringUtils.EMPTY))
                .spanId(propertyMap.getOrDefault(ConstantConfig.LogTracer.SPAN_ID, StringUtils.EMPTY))
                .content(getLogbackBody(event))
                .level(event.getLevel().toString())
                .appName(event.getLoggerContextVO().getPropertyMap().getOrDefault(APP_NAME, StringUtils.EMPTY))
                .currIp(CURR_IP)
                .className(event.getLoggerName())
                .methodName(methodName)
                .threadName(event.getThreadName())
                .timestamp(new Date())
                .build();
    }

    /**
     * 获取日志正文信息
     *
     * @param logEvent 日志事件
     * @return 日志正文信息
     */
    private String getLogbackBody(ILoggingEvent logEvent) {
        if (Level.ERROR == logEvent.getLevel()) {
            if (logEvent.getThrowableProxy() != null) {
                ThrowableProxy throwableProxy = (ThrowableProxy) logEvent.getThrowableProxy();
                // 获取异常堆栈信息
                String errorStack = logEvent.getFormattedMessage()
                        + ConstantConfig.SpecialSymbols.ENTER_LINUX
                        + errorStackTrace(throwableProxy.getThrowable()).toString();
                return packageMessage(ConstantConfig.SpecialSymbols.CURLY_BRACES, new String[]{errorStack});
            }

            // 获取参数数组
            Object[] args = logEvent.getArgumentArray();
            if (CollectionUtil.isNotEmpty(args)) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Throwable) {
                        args[i] = errorStackTrace(args[i]);
                    }
                }
                return packageMessage(logEvent.getMessage(), args);
            }
        }
        return logEvent.getFormattedMessage();
    }

    /**
     * 获取堆栈信息字符串工具
     *
     * @param throwable 异常堆栈
     * @return 堆栈信息字符串
     */
    private static Object errorStackTrace(Object throwable) {
        if (throwable instanceof Exception exception) {
            try (StringWriter stringWriter = new StringWriter();
                 PrintWriter printWriter = new PrintWriter(stringWriter)) {
                exception.printStackTrace(printWriter);
                return stringWriter.toString();
            } catch (Exception e) {
                return throwable;
            }
        }
        return throwable;
    }

    /**
     * 获取包装信息
     *
     * @param message 日志信息
     * @param args    参数
     * @return 包装信息
     */
    private String packageMessage(String message, Object[] args) {
        if (StringUtils.isNotBlank(message)
                && StringUtils.contains(message, ConstantConfig.SpecialSymbols.CURLY_BRACES)) {
            return MessageFormatter.arrayFormat(message, args).getMessage();
        }

        StringBuilder builder = new StringBuilder(STRING_INITIAL_CAPACITY);
        builder.append(message);
        for (Object arg : args) {
            builder.append(ConstantConfig.SpecialSymbols.ENTER_LINUX).append(arg);
        }
        return builder.toString();
    }
}
