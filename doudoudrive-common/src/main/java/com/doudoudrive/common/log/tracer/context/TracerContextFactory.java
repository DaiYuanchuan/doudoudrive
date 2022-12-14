package com.doudoudrive.common.log.tracer.context;

import cn.hutool.core.util.IdUtil;
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
    }
}
