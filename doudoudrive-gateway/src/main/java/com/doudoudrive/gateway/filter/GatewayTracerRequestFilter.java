package com.doudoudrive.gateway.filter;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.log.tracer.context.SpanIdGenerator;
import com.doudoudrive.common.log.tracer.context.TracerContextFactory;
import com.doudoudrive.common.model.dto.model.LogLabelModel;
import com.doudoudrive.common.util.lang.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

/**
 * <p>Gateway全局请求拦截器</p>
 * <p>2022-11-17 22:13</p>
 *
 * @author Dan
 **/
@Configuration
public class GatewayTracerRequestFilter implements GlobalFilter {


    private static LogLabelModel getLogLabelModel(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        LogLabelModel logLabelModel = new LogLabelModel();

        // 获取请求头中的traceId
        List<String> tracerIdList = headers.get(ConstantConfig.LogTracer.TRACER_ID);
        if (CollectionUtil.isNotEmpty(tracerIdList)) {
            logLabelModel.setTracerId(tracerIdList.get(NumberConstant.INTEGER_ZERO));
        }

        // 获取请求头中的spanId
        List<String> spanIdList = headers.get(ConstantConfig.LogTracer.SPAN_ID);
        if (CollectionUtil.isNotEmpty(spanIdList)) {
            logLabelModel.setSpanId(spanIdList.get(NumberConstant.INTEGER_ZERO));
        }
        return logLabelModel;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取网关中的请求头
        LogLabelModel logLabelModel = getLogLabelModel(exchange);

        // 设置日志追踪内容
        TracerContextFactory.set(logLabelModel);

        // 如果请求头中没有traceId，则生成一个traceId
        if (StringUtils.isNotBlank(logLabelModel.getTracerId())) {
            Consumer<HttpHeaders> httpHeaders = httpHeader -> {
                httpHeader.set(ConstantConfig.LogTracer.TRACER_ID, logLabelModel.getTracerId());
                httpHeader.set(ConstantConfig.LogTracer.SPAN_ID, SpanIdGenerator.generateNextSpanId());
            };
            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders).build();

            exchange = exchange.mutate().request(serverHttpRequest).build();
        }

        return chain.filter(exchange).doFinally(signalType -> TracerContextFactory.clear());
    }
}
