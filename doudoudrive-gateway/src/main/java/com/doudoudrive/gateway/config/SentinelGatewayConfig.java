package com.doudoudrive.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.util.http.Result;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * <p>自定义SentinelGateway阻塞返回方法</p>
 * <p>2022-03-04 23:05</p>
 *
 * @author Dan
 **/
@Component
public class SentinelGatewayConfig {

    /**
     * 网关限流了请求，就会调用此回调
     */
    public SentinelGatewayConfig() {
        GatewayCallbackManager.setBlockHandler((serverWebExchange, throwable) -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(JSON.toJSONString(Result.build(StatusCodeEnum.TOO_MANY_REQUESTS))), String.class));
    }
}
