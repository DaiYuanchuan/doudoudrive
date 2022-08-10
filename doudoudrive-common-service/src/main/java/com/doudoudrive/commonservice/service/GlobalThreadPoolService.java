package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.constant.ConstantConfig;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * <p>全局线程池服务层接口</p>
 * <p>2022-06-20 23:09</p>
 *
 * @author Dan
 **/
public interface GlobalThreadPoolService {

    /**
     * <p>执行有返回值的异步方法</p>
     * <p>Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞</p>
     *
     * @param threadPool 线程池类型枚举
     * @param task       执行的Task任务
     * @param <T>        执行的Task任务返回类型
     * @return Future
     */
    <T> Future<T> submit(ConstantConfig.ThreadPoolEnum threadPool, Callable<T> task);

    /**
     * <p>执行有返回值的异步方法</p>
     * <p>Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞</p>
     *
     * @param threadPool 线程池类型枚举
     * @param runnable   可运行对象
     * @return Future
     */
    Future<?> submit(ConstantConfig.ThreadPoolEnum threadPool, Runnable runnable);

}
