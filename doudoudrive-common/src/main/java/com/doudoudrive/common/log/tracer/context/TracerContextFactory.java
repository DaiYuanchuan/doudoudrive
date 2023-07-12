package com.doudoudrive.common.log.tracer.context;

import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.LogLabelModel;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Optional;

/**
 * <p>日志追踪内容上下文工厂配置</p>
 * <p>2022-11-17 18:47</p>
 *
 * @author Dan
 **/
public class TracerContextFactory {

    /**
     * 记录全局服务的MDC内容
     */
    private static final TransmittableThreadLocal<Map<String, String>> TTL_MDC = new TransmittableThreadLocal<>() {

        /**
         * 在多线程数据传递的时候，需要将数据复制一份给MDC
         */
        @Override
        public void beforeExecute() {
            // 获取当前线程的局部变量的副本的值
            Map<String, String> contextMap = get();
            contextMap.forEach(MDC::put);
        }

        /**
         * 任务对象(TtlRunnable/TtlCallable)执行后的回调方法，用于清理MDC
         */
        @Override
        public void afterExecute() {
            MDC.clear();
        }

        /**
         * 如果变量对当前线程没有值，则会先执行此方法获取返回的值
         *
         * @return 返回一个初始值
         */
        @Override
        public Map<String, String> initialValue() {
            return Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_FIVE);
        }
    };

    /**
     * 设置追踪内容
     *
     * @param context 系统日志标签数据，值为空时将生成默认值
     */
    public static void set(LogLabelModel context) {
        // 获取tracerId，如果没有获取到就生成一个新的tracerId
        if (StringUtils.isBlank(context.getTracerId())) {
            context.setTracerId(IdUtil.fastSimpleUUID());
        }

        // 往日志上下文里放当前获取到的spanId，如果spanId为空，会放入初始值
        SpanIdGenerator.put(context.getSpanId());

        MDC.put(ConstantConfig.LogTracer.TRACER_ID, context.getTracerId());
        MDC.put(ConstantConfig.LogTracer.SPAN_ID, SpanIdGenerator.get());
        // 这里需要将记录提交给TransmittableThreadLocal
        TTL_MDC.get().put(ConstantConfig.LogTracer.TRACER_ID, context.getTracerId());
        TTL_MDC.get().put(ConstantConfig.LogTracer.SPAN_ID, SpanIdGenerator.get());
    }

    /**
     * 获取追踪内容map
     *
     * @return 日志追踪内容的map
     */
    public static Map<String, String> get() {
        return Optional.ofNullable(MDC.getCopyOfContextMap()).orElse(Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ZERO));
    }

    /**
     * 清空追踪内容
     */
    public static void clear() {
        SpanIdGenerator.remove();
        MDC.clear();
        TTL_MDC.get().clear();
        TTL_MDC.remove();
    }
}
