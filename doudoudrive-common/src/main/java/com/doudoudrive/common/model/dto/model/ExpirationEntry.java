package com.doudoudrive.common.model.dto.model;

import com.doudoudrive.common.constant.NumberConstant;
import com.google.common.collect.Maps;
import io.netty.util.Timeout;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * <p>记录锁的过期条目</p>
 * <p>2023-02-09 22:08</p>
 *
 * @author Dan
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationEntry {

    /**
     * 线程id -> 锁的数量
     */
    private final Map<Long, Integer> THREAD_IDS = Maps.newLinkedHashMap();

    /**
     * 指定的延迟之后执行的任务对象
     * volatile 保证成员变量Timeout的修改其他线程立即可见
     */
    private volatile Timeout timeout;

    /**
     * 添加当前获取锁的线程id
     *
     * @param threadId 线程id
     */
    public synchronized void addThreadId(long threadId) {
        THREAD_IDS.compute(threadId, (thread, counter) -> {
            // 如果不存在则设置为0
            counter = Optional.ofNullable(counter).orElse(NumberConstant.INTEGER_ZERO);
            counter++;
            return counter;
        });
    }

    /**
     * 判断线程对象是否为空
     *
     * @return true: 空; false: 非空
     */
    public synchronized boolean hasNoThreads() {
        return THREAD_IDS.isEmpty();
    }

    /**
     * 获取第一个线程Id
     *
     * @return 线程Id
     */
    public synchronized Long getFirstThreadId() {
        if (THREAD_IDS.isEmpty()) {
            return null;
        }
        return THREAD_IDS.keySet().iterator().next();
    }

    public synchronized Map<Long, Integer> getThreadIds() {
        return THREAD_IDS;
    }

    /**
     * 删除一个线程id
     *
     * @param threadId 线程id
     */
    public synchronized void removeThreadId(long threadId) {
        THREAD_IDS.compute(threadId, (thread, counter) -> {
            if (counter == null) {
                return null;
            }
            counter--;
            if (counter.equals(NumberConstant.INTEGER_ZERO)) {
                return null;
            }
            return counter;
        });
    }
}
