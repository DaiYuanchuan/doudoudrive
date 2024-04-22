package com.doudoudrive.common.util.http;

import ch.qos.logback.classic.LoggerContext;
import com.doudoudrive.common.constant.NumberConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>Http Client 连接请求管理</p>
 * <p>2024-04-20 17:27</p>
 *
 * @author Dan
 **/
@Slf4j
public class HttpConnection {

    /**
     * Http 客户端连接参数相关配置信息
     */
    private static final HttpConnectConfig CONNECT_CONFIG = new HttpConnectConfig();

    /**
     * Keep-Alive 头部信息中的超时时间字段
     */
    private static final String HTTP_TIMEOUT = "timeout";

    /**
     * Http、Https 协议
     */
    private static final String HTTP_PROTOCOL = "http";
    private static final String HTTPS_PROTOCOL = "https";
    /**
     * 定义默认的连接保持策略，确定 HTTP 连接的持久性。
     * 它决定了客户端与服务器之间的连接是否应该保持活动状态以便进行重复使用，以及在何种情况下可以关闭连接
     * <pre>
     *     优点
     *     节省了服务端 CPU 和内存适用量
     *     降低拥塞控制 （TCP 连接减少）
     *     减少了后续请求的延迟（无需再进行握手）
     *
     *     缺点
     *     对于某些低频访问的资源 / 服务，一些不常访问的地址，每一次连接还保持就比较浪费了
     *     Keep-Alive 可能会非常影响性能，因为它在文件被请求之后还保持了不必要的连接很长时间，额外占用了服务端的连接数。
     * </pre>
     */
    private static final ConnectionKeepAliveStrategy DEFAULT_STRATEGY = (response, context) -> {
        HeaderElementIterator basicHeader = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (basicHeader.hasNext()) {
            HeaderElement header = basicHeader.nextElement();
            if (header.getValue() != null && header.getName().equalsIgnoreCase(HTTP_TIMEOUT)) {
                try {
                    return Long.parseLong(header.getValue()) * NumberConstant.INTEGER_ONE_THOUSAND;
                } catch (Exception e) {
                    log.error("format KeepAlive timeout exception, {}: {}", header.getName(), header.getValue());
                }
            }
        }
        return CONNECT_CONFIG.getKeepAliveTime();
    };
    /**
     * Http 客户端
     */
    private static CloseableHttpClient HTTP_CLIENT = null;
    /**
     * 当前 Http 连接实例对象
     */
    private static volatile HttpConnection instance;

    static {
        try {
            // 关闭 HttpClient 默认的DEBUG日志
            ((LoggerContext) LoggerFactory.getILoggerFactory())
                    .getLogger("org.apache.http").setLevel(ch.qos.logback.classic.Level.ERROR);
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(builder.build());
            // 配置同时支持 http 和 https
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP_PROTOCOL, PlainConnectionSocketFactory.getSocketFactory())
                    .register(HTTPS_PROTOCOL, sslConnectionFactory).build();

            // 初始化连接管理器，使用自定义的连接工厂
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connectionManager.setMaxTotal(CONNECT_CONFIG.getMaxTotal());
            // 设置最大路由
            connectionManager.setDefaultMaxPerRoute(CONNECT_CONFIG.getDefaultMaxPerRoute());
            // 初始化httpClient
            HTTP_CLIENT = HttpClients.custom()
                    // 设置连接池管理器
                    .setConnectionManager(connectionManager)
                    .setKeepAliveStrategy(DEFAULT_STRATEGY)
                    .setDefaultRequestConfig(getRequestConfig())
                    // 设置重试次数
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(NumberConstant.INTEGER_TWO, false))
                    .build();
            // 启动连接剔除器
            IdleConnectionEvictor ict = new IdleConnectionEvictor(connectionManager,
                    NumberConstant.INTEGER_FIVE, TimeUnit.SECONDS,
                    NumberConstant.INTEGER_THIRTY_TWO, TimeUnit.SECONDS);
            ict.start();
        } catch (Exception e) {
            log.error("init HttpclientUtil error: {}", e.getMessage());
        }
    }

    /**
     * 获取 Http 请求客户端配置信息
     *
     * @return Http 请求配置
     */
    public static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(CONNECT_CONFIG.getSocketTimeout())
                .setConnectTimeout(CONNECT_CONFIG.getConnectTimeout())
                .setConnectionRequestTimeout(CONNECT_CONFIG.getConnectionRequestTimeout())
                .setExpectContinueEnabled(false).build();
    }

    /**
     * 获取 Http 连接实例对象
     *
     * @return HttpConnection 实例对象
     */
    public static HttpConnection getInstance() {
        if (instance == null) {
            synchronized (HttpConnection.class) {
                if (instance == null) {
                    instance = new HttpConnection();
                }
            }
        }
        return instance;
    }

    /**
     * 获取 Http 客户端请求对象
     *
     * @return Http 客户端
     */
    public CloseableHttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    /**
     * Http 客户端默认的连接参数相关配置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HttpConnectConfig {
        /**
         * 最大连接数, 默认 200
         */
        private Integer maxTotal = 200;

        /**
         * 每个路由的最大连接数, 默认 100
         */
        private Integer defaultMaxPerRoute = 100;

        /**
         * 连接超时时间, 默认 30 秒
         */
        private Integer connectTimeout = 30000;

        /**
         * 读取、响应超时时间, 默认 30 秒
         */
        private Integer socketTimeout = 30000;

        /**
         * 从连接池获取连接的超时时间, 默认 30 秒
         */
        private Integer connectionRequestTimeout = 30000;

        /**
         * 保持连接时间, 默认 5 分钟
         */
        private Integer keepAliveTime = 300000;
    }
}
