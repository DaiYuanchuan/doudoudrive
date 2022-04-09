package com.doudoudrive.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <p>网关请求异常处理器</p>
 * <p>2022-04-09 17:13</p>
 *
 * @author Dan
 **/
@Slf4j
@Order(0)
@Component
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * 拦截默认异常响应处理程序
     *
     * @param exchange  服务网络交换程序
     * @param throwable 异常
     * @return 重置默认的异常响应体
     */
    @NonNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable throwable) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(throwable);
        }

        // 是否响应状态异常，响应对应的状态码，不响应内容
        if (throwable instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) throwable).getStatus());
        }
        return response.writeWith(Mono.fromSupplier(() -> response.bufferFactory().wrap(new byte[0])));
    }
}
