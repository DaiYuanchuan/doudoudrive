package com.doudoudrive.common.model.dto.model;

import com.doudoudrive.common.constant.NumberConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>内部线程池常规配置项</p>
 * <p>2022-06-19 23:37</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThreadPoolModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 初始线程池的大小
     */
    private Integer corePoolSize;

    /**
     * 允许同时执行的最大线程数
     */
    private Integer maxPoolSize;

    /**
     * 线程等待队列容量
     */
    private Integer queueCapacity;

    /**
     * 线程执行超时后是否回收线程
     */
    private Boolean allowCoreThreadTimeOut;

    /**
     * 设置线程存活时间，即当池中线程多于初始大小时，多出的线程保留的时长，单位纳秒
     */
    private Long keepAliveTime;

    /**
     * 线程池名称、类型、场景(全局唯一)
     */
    private String name;

    /**
     * 获取初始线程池的大小，为空时默认为 CPU核心数 * 2
     * 最佳线程数目 = ((线程等待时间+线程CPU时间)/线程CPU时间) * CPU数目
     *
     * @return 初始线程池的大小
     */
    public Integer getCorePoolSize() {
        return Optional.ofNullable(corePoolSize).orElse(Runtime.getRuntime().availableProcessors() * NumberConstant.INTEGER_TWO);
    }

    /**
     * 获取同时执行的最大线程数，为空时默认为 CPU核心数 * 2
     * 默认与核心线程数大小保持一直
     *
     * @return 同时执行的最大线程数
     */
    public Integer getMaxPoolSize() {
        return Optional.ofNullable(maxPoolSize).orElse(Runtime.getRuntime().availableProcessors() * NumberConstant.INTEGER_TWO);
    }

    /**
     * 获取线程等待队列容量，为空时默认为1000
     *
     * @return 线程等待队列容量
     */
    public Integer getQueueCapacity() {
        return Optional.ofNullable(queueCapacity).orElse(NumberConstant.INTEGER_ONE_THOUSAND);
    }

    /**
     * 获取线程存活时间，为空时默认为50秒
     *
     * @return 线程存活时间，单位纳秒
     */
    public Long getKeepAliveTime() {
        return Optional.ofNullable(keepAliveTime).orElse(TimeUnit.SECONDS.toNanos(NumberConstant.INTEGER_FIFTY));
    }
}
