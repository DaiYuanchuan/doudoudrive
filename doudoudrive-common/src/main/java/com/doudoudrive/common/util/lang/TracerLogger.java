package com.doudoudrive.common.util.lang;

import cn.hutool.http.HttpRequest;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.SysLogMessage;
import com.doudoudrive.common.model.dto.model.TracerLogbackModel;
import com.doudoudrive.common.model.dto.model.WorkerTcpProperties;
import com.doudoudrive.common.model.dto.model.WorkerUdpProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.integration.ip.udp.UnicastSendingMessageHandler;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>日志追踪记录器</p>
 * <p>2022-11-11 14:58</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
@EnableConfigurationProperties(value = {WorkerUdpProperties.class, WorkerTcpProperties.class})
public class TracerLogger implements CommandLineRunner {

    private UnicastSendingMessageHandler sendingMessageHandler;

    /**
     * 获取配置类中worker模块的udp通信参数配置
     */
    private WorkerUdpProperties workerUdpProperties;

    /**
     * 获取配置类中worker模块的tcp通信参数配置
     */
    private WorkerTcpProperties workerTcpProperties;

    @Autowired
    public void setSendingMessageHandler(UnicastSendingMessageHandler sendingMessageHandler) {
        this.sendingMessageHandler = sendingMessageHandler;
    }

    @Autowired
    public void setWorkerUdpProperties(WorkerUdpProperties workerUdpProperties) {
        this.workerUdpProperties = workerUdpProperties;
    }

    @Autowired
    public void setWorkerTcpProperties(WorkerTcpProperties workerTcpProperties) {
        this.workerTcpProperties = workerTcpProperties;
    }

    /**
     * 线程池中要保留的线程数
     */
    private static final Integer CORE_POOL_SIZE = 1;

    /**
     * 日志队列容量
     */
    private static final Integer LOGGER_QUEUE_CAPACITY = 50000;

    /**
     * 每次上传、获取的日志数量
     */
    private static final Integer ELEMENTS_PER_LOG = 500;

    /**
     * 本地队列满了后丢弃的数量
     */
    private static final AtomicLong FAIL_OFFER_COUNT = new AtomicLong();

    /**
     * 本地logger日志队列，已写入的总数量
     */
    private static final AtomicLong SUCCESS_LOGGER_OFFER_COUNT = new AtomicLong();

    /**
     * 日志集中营，最多积压5万条
     */
    private static final BlockingQueue<SysLogMessage> LOG_BEAN_QUEUE = new LinkedBlockingQueue<>(LOGGER_QUEUE_CAPACITY);

    /**
     * 序列化工具
     */
    private static final ProtostuffUtil<TracerLogbackModel> SERIALIZER = new ProtostuffUtil<>();

    /**
     * 线程池用于异步推送系统日志
     */
    private ScheduledExecutorService executorService;

    /**
     * 私有化方法，防止外部实例化
     */
    private TracerLogger() {
    }

    /**
     * 写入日志队列
     *
     * @param sysLogMessage 组装好的系统日志对象
     */
    public static void offerLogger(SysLogMessage sysLogMessage) {
        // 返回容量是否已满
        boolean success = LOG_BEAN_QUEUE.offer(sysLogMessage);
        if (success) {
            if (SUCCESS_LOGGER_OFFER_COUNT.incrementAndGet() % NumberConstant.INTEGER_ONE_THOUSAND == NumberConstant.INTEGER_ZERO) {
                log.warn("用户跟踪队列已写入：{}条，当前队列积压数量：{}", SUCCESS_LOGGER_OFFER_COUNT.get(), LOG_BEAN_QUEUE.size());
            }
            return;
        }

        // 队列已满，丢弃日志
        if (FAIL_OFFER_COUNT.incrementAndGet() % NumberConstant.INTEGER_TEN == NumberConstant.INTEGER_ZERO) {
            log.warn("用户跟踪队列已满，当前丢弃的数量为：{}", FAIL_OFFER_COUNT.get());
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化线程池
        this.executorService = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, runnable -> {
            Thread thread = new Thread(runnable, "logback-up-thread");
            thread.setDaemon(true);
            return thread;
        });

        // 开启日志推送线程
        this.executorService.scheduleAtFixedRate(this::uploadWorker, NumberConstant.INTEGER_ONE, NumberConstant.INTEGER_ONE, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
    }

    /**
     * executor服务的销毁
     */
    private void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * 系统日志上次到worker处理
     */
    private void uploadWorker() {
        try {
            // 从队列中获取500条日志，不足500时等待1秒，汇总上报给worker一次
            List<SysLogMessage> arrayList = CollectionUtil.pollBatchOrWait(LOG_BEAN_QUEUE, ELEMENTS_PER_LOG, NumberConstant.LONG_ONE, TimeUnit.SECONDS);
            if (CollectionUtil.isEmpty(arrayList)) {
                return;
            }

            // 将日志集合序列化
            byte[] bytes = SERIALIZER.serialize(TracerLogbackModel.builder()
                    .logMessageList(arrayList)
                    .build());

            // 压缩字节流
            byte[] compressBytes = CompressionUtil.compress(bytes);

            // 判断字节流压缩完后是否过大，过大走http接口请求worker
            if (workerTcpProperties.getEnable() && compressBytes.length >= workerUdpProperties.getMaxCompressBytes()) {
                // 请求最终构建的url,获取请求body
                try (cn.hutool.http.HttpResponse execute = HttpRequest.post(workerTcpProperties.getUrl())
                        .body(compressBytes)
                        .contentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON)
                        .charset(StandardCharsets.UTF_8)
                        .timeout(NumberConstant.INTEGER_MINUS_ONE)
                        .execute()) {
                    if (execute.getStatus() != HttpStatus.SC_OK) {
                        // 系统日志上报失败
                        log.error("failed to report the user trace log, and the worker returned the status code：{}", execute.getStatus());
                    }
                }
                return;
            }

            if (workerUdpProperties.getEnable()) {
                // 发送udp请求
                sendingMessageHandler.handleMessage(org.springframework.messaging.support.MessageBuilder.withPayload(compressBytes).build());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
