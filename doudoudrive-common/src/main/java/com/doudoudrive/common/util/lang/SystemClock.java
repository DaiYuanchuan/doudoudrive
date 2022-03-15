package com.doudoudrive.common.util.lang;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统时钟<br>
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多（具体耗时高出多少我还没测试过，有人说是100倍左右）
 * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道
 * 后台定时更新时钟，JVM退出时，线程自动回收
 * <p>2022-01-15 18:23</p>
 *
 * @author Dan
 **/
public class SystemClock {

    /**
     * 时钟更新间隔，单位毫秒
     */
    private final long period;

    /**
     * 现在时刻的毫秒数
     */
    private final AtomicLong nowTime;

    /**
     * 线程池中要保留的线程数
     */
    private static final Integer CORE_POOL_SIZE = 1;

    /**
     * 单线程调度执行器
     */
    private ScheduledExecutorService executorService;

    /**
     * 构造
     *
     * @param period 时钟更新间隔，单位毫秒
     */
    public SystemClock(long period) {
        this.period = period;
        this.nowTime = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    /**
     * 开启计时器线程
     */
    private void scheduleClockUpdating() {
        this.executorService = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, runnable -> {
            Thread thread = new Thread(runnable, "system-clock");
            thread.setDaemon(true);
            return thread;
        });
        executorService.scheduleAtFixedRate(() -> nowTime.set(Instant.now().toEpochMilli()),
                this.period, this.period, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
    }

    /**
     * executor服务的销毁
     */
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * @return 当前时间毫秒数
     */
    private long currentTimeMillis() {
        return nowTime.get();
    }

    //------------------------------------------------------------------------ static

    /**
     * 单例
     */
    private static class InstanceHolder {
        private static final SystemClock INSTANCE = new SystemClock(1);
        private static final AtomicLong LAST_TIMESTAMP = new AtomicLong(INSTANCE.currentTimeMillis());
    }

    /**
     * @return 当前时间
     */
    public static long now() {
        return InstanceHolder.INSTANCE.currentTimeMillis();
    }

    /**
     * 获取一个自增的时间戳，同一段时间内获取到的时间戳是自增的
     *
     * @return 一个自增的时间戳
     */
    public static long increasing() {
        // 当前时间戳小于最后更新的时间戳
        if (now() < InstanceHolder.LAST_TIMESTAMP.get()) {
            return InstanceHolder.LAST_TIMESTAMP.incrementAndGet();
        }
        InstanceHolder.LAST_TIMESTAMP.set(now());
        return InstanceHolder.LAST_TIMESTAMP.incrementAndGet();
    }

    /**
     * @return 当前时间字符串表现形式
     */
    public static String nowDate() {
        return new Timestamp(now()).toString();
    }
}
