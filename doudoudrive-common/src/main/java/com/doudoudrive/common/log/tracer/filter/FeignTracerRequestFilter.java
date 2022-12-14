package com.doudoudrive.common.log.tracer.filter;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.log.tracer.context.SpanIdGenerator;
import com.doudoudrive.common.log.tracer.context.TracerContextFactory;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <p>Feign请求拦截器</p>
 * <p>2022-11-17 22:00</p>
 *
 * @author Dan
 **/
public class FeignTracerRequestFilter implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, String> context = TracerContextFactory.get();
        String traceId = context.get(ConstantConfig.LogTracer.TRACER_ID);
        if (StringUtils.isNotBlank(traceId)) {
            requestTemplate.header(ConstantConfig.LogTracer.TRACER_ID, traceId);
            requestTemplate.header(ConstantConfig.LogTracer.SPAN_ID, SpanIdGenerator.generateNextSpanId());
        }
    }
}
