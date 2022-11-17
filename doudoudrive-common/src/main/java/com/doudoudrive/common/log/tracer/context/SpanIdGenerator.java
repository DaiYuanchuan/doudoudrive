package com.doudoudrive.common.log.tracer.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.doudoudrive.common.constant.NumberConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>链路id生成器</p>
 * <p>2022-11-17 19:23</p>
 *
 * @author Dan
 **/
public class SpanIdGenerator {

    /**
     * 记录全局服务的链路id
     */
    private static final TransmittableThreadLocal<String> CURRENT_SPAN_ID = new TransmittableThreadLocal<>();

    /**
     * 记录当前服务的链路id
     */
    private static final TransmittableThreadLocal<AtomicLong> SPAN_INDEX = new TransmittableThreadLocal<>();

    /**
     * 链路id格式化
     */
    private static final String SPAN_ID_FORMAT = "%s.%s";

    /**
     * 设置当前服务的链路id
     *
     * @param spanId 链路id
     */
    public static void put(String spanId) {
        if (StringUtils.isBlank(spanId)) {
            // 如果没有传入链路id，则默认为0
            spanId = NumberConstant.STRING_ZERO;
        }

        CURRENT_SPAN_ID.set(spanId);
        // 当前服务的链路id从0开始
        SPAN_INDEX.set(new AtomicLong(NumberConstant.LONG_ZERO));
    }

    /**
     * 获取当前服务的链路id
     *
     * @return 链路id
     */
    public static String get() {
        return CURRENT_SPAN_ID.get();
    }

    /**
     * 删除当前服务的链路id
     */
    public static void remove() {
        CURRENT_SPAN_ID.remove();
    }

    /**
     * 生成下一个链路id
     *
     * @return 链路id
     */
    public static String generateNextSpanId() {
        return String.format(SPAN_ID_FORMAT, CURRENT_SPAN_ID.get(), SPAN_INDEX.get().incrementAndGet());
    }
}
