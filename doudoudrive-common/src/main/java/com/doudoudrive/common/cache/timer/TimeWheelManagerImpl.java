package com.doudoudrive.common.cache.timer;

import com.doudoudrive.common.constant.NumberConstant;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * <p>时间轮定时器通用服务层实现</p>
 * <p>2023-05-27 17:12</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("timeWheelManager")
public class TimeWheelManagerImpl implements TimeWheelManager, CommandLineRunner, Closeable {

    /**
     * 看门狗的时间轮的刻度数量
     */
    private static final Integer TICKS_PER_WHEEL = 1024;
    /**
     * 看门狗的时间轮线程池名称
     */
    private static final String POOL_NAME = "time-wheel";
    /**
     * 看门狗，时间轮定时器
     */
    private HashedWheelTimer timer;

    /**
     * 初始化看门狗，时间轮定时器，刻度持续时间为100毫秒，刻度数量为1024
     *
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        // 初始化时间轮定时器
        timer = new HashedWheelTimer(new DefaultThreadFactory(POOL_NAME), NumberConstant.INTEGER_HUNDRED, TimeUnit.MILLISECONDS, TICKS_PER_WHEEL, Boolean.FALSE);
    }

    /**
     * 关闭看门狗，时间轮定时器
     */
    @Override
    public void close() {
        timer.stop();
    }

    @Override
    public Timeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
        return timer.newTimeout(task, delay, unit);
    }
}
