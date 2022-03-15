package com.doudoudrive.commonservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <p>线程上下文，用于保存、切换当前数据源</p>
 * <p>2022-03-03 22:56</p>
 *
 * @author Dan
 **/
@Slf4j
public class DataSourceContextHolder {

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<Deque<String>> LOOKUP_KEY_HOLDER = ThreadLocal.withInitial(ArrayDeque::new);

    private DataSourceContextHolder() {
    }

    /**
     * 获得当前线程数据源
     *
     * @return 数据源名称
     */
    public static String peek() {
        return LOOKUP_KEY_HOLDER.get().peek();
    }

    /**
     * 设置当前线程数据源
     * <p>
     * 如非必要不要手动调用，调用后确保最终清除
     * </p>
     *
     * @param dataSource 数据源名称
     */
    public static void push(String dataSource) {
        LOOKUP_KEY_HOLDER.get().push(StringUtils.isEmpty(dataSource) ? "" : dataSource);
    }

    /**
     * 清空当前线程数据源
     * <p>
     * 如果当前线程是连续切换数据源 只会移除掉当前线程的数据源名称
     * </p>
     */
    public static void poll() {
        Deque<String> deque = LOOKUP_KEY_HOLDER.get();
        deque.poll();
        if (deque.isEmpty()) {
            LOOKUP_KEY_HOLDER.remove();
        }
    }

    /**
     * 强制清空本地线程
     * <p>
     * 防止内存泄漏，如手动调用了push可调用此方法确保清除
     * </p>
     */
    public static void clear() {
        LOOKUP_KEY_HOLDER.remove();
    }

}
