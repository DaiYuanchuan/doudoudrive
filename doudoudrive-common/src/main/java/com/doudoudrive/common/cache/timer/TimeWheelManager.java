package com.doudoudrive.common.cache.timer;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * <p>时间轮定时器通用服务层接口</p>
 * <p>2023-05-27 17:19</p>
 *
 * @author Dan
 **/
public interface TimeWheelManager {

    /**
     * 创建一个定时任务
     *
     * @param task  任务
     * @param delay 延迟时间
     * @param unit  时间单位
     * @return 定时任务
     */
    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);

}
