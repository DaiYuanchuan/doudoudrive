package com.doudoudrive.commonservice.service.impl;

import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.model.dto.model.ThreadPoolModel;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.GlobalThreadPoolService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>全局线程池服务层实现</p>
 * <p>2022-06-20 23:10</p>
 *
 * @author Dan
 **/
@Scope("singleton")
@Service("globalThreadPoolService")
public class GlobalThreadPoolServiceImpl implements GlobalThreadPoolService, CommandLineRunner, Closeable {

    private DiskDictionaryService diskDictionaryService;

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    /**
     * 自定义的线程池名称
     */
    private static final String THREAD_POOL_NAME = "%s-thread-";

    /**
     * 全局线程池对象Map
     */
    private static final Map<String, ExecutorService> SYS_GLOBAL_THREAD_POOL = Maps.newHashMapWithExpectedSize(ConstantConfig.ThreadPoolEnum.values().length);

    /**
     * <p>执行有返回值的异步方法</p>
     * <p>Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞</p>
     *
     * @param threadPool 线程池类型枚举
     * @param task       执行的Task任务
     * @param <T>        执行的Task任务返回类型
     * @return Future
     */
    @Override
    public <T> Future<T> submit(ConstantConfig.ThreadPoolEnum threadPool, Callable<T> task) {
        // 获取线程池对象
        ExecutorService executorService = SYS_GLOBAL_THREAD_POOL.get(threadPool.getName());
        if (null == executorService || executorService.isShutdown()) {
            return null;
        }
        // 执行异步操作
        return executorService.submit(task);
    }

    /**
     * <p>执行有返回值的异步方法</p>
     * <p>Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞</p>
     *
     * @param threadPool 线程池类型枚举
     * @param runnable   可运行对象
     * @return Future
     */
    @Override
    public Future<?> submit(ConstantConfig.ThreadPoolEnum threadPool, Runnable runnable) {
        // 获取线程池对象
        ExecutorService executorService = SYS_GLOBAL_THREAD_POOL.get(threadPool.getName());
        if (null == executorService || executorService.isShutdown()) {
            return null;
        }
        // 执行异步操作
        return executorService.submit(runnable);
    }

    /**
     * 初始化全局线程池对象Map
     *
     * @param args 命令行参数
     */
    @Override
    public void run(String... args) {
        this.init();
    }

    /**
     * 关闭全局线程池对象Map
     */
    @Override
    public void close() throws IOException {
        // 循环关闭线程池对象
        SYS_GLOBAL_THREAD_POOL.forEach((key, value) -> Optional.ofNullable(value).ifPresent(ExecutorService::shutdown));
    }

    /**
     * 初始化全局线程池对象
     */
    private synchronized void init() {
        // 获取内部线程池配置信息
        List<ThreadPoolModel> threadPoolConfig = diskDictionaryService.getDictionaryArray(DictionaryConstant.THREAD_POOL_CONFIG, ThreadPoolModel.class);
        if (CollectionUtil.isEmpty(threadPoolConfig)) {
            return;
        }

        // 初始化全局线程池对象
        for (ThreadPoolModel threadPool : threadPoolConfig) {
            ExecutorService executorService = SYS_GLOBAL_THREAD_POOL.get(threadPool.getName());
            if (ObjectUtils.isEmpty(executorService)) {
                // 如果线程池对象不存在，则创建线程池对象(增加了对线程局部变量传递功能，这里主要用于链路追踪tracerId的传递)
                executorService = TtlExecutors.getTtlExecutorService(ExecutorBuilder.create()
                        .setCorePoolSize(threadPool.getCorePoolSize())
                        .setMaxPoolSize(threadPool.getMaxPoolSize())
                        .setWorkQueue(new LinkedBlockingQueue<>(threadPool.getQueueCapacity()))
                        .setAllowCoreThreadTimeOut(threadPool.getAllowCoreThreadTimeOut())
                        .setKeepAliveTime(threadPool.getKeepAliveTime())
                        .setHandler(ConstantConfig.ThreadPoolEnum.getExecutionHandler(threadPool.getName()))
                        .setThreadFactory(new CustomizableThreadFactory(String.format(THREAD_POOL_NAME, threadPool.getName())))
                        .build());
                SYS_GLOBAL_THREAD_POOL.put(threadPool.getName(), executorService);
            }
        }
    }
}
