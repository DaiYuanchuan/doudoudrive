package com.doudoudrive.common.util.lang;

import com.doudoudrive.common.constant.NumberConstant;
import com.google.common.collect.Lists;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>集合相关工具类</p>
 * <p>2022-03-07 17:50</p>
 *
 * @author Dan
 **/
public class CollectionUtil extends CollectionUtils {

    /**
     * 集合是否为非空
     *
     * @param collection 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Map是否为非空
     *
     * @param map map数据
     * @return 是否为非空
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * list分割 分批次处理工具
     *
     * @param collections   需要分割的集合 (List LinkedHashSet TreeSet LinkedList)
     * @param maxBatchTasks 最大需要执行的任务数量
     * @param <T>           集合对象
     * @return 分割后的集合以集合嵌套的形式输出
     */
    public static <T> List<List<T>> collectionCutting(Collection<T> collections, Long maxBatchTasks) {
        if (isEmpty(collections)) {
            return new ArrayList<>();
        }
        // 集合数量小于最大任务数量时，直接返回
        if (collections.size() <= maxBatchTasks) {
            return new ArrayList<>(Collections.singletonList(new ArrayList<>(collections)));
        }

        // 计算切分次数
        long limit = (collections.size() + maxBatchTasks - 1) / maxBatchTasks;
        return Stream.iterate(0, n -> n + 1)
                .limit(limit)
                .parallel()
                .map(a -> collections.stream()
                        .skip(a * maxBatchTasks)
                        .limit(maxBatchTasks)
                        .parallel()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * 从队列中获取指定数量的数据，可用于批量处理
     * 每次获取指定数量的数据，如果队列中数据量不足，则等待一段时间后获取所有数据
     *
     * @param queue     队列
     * @param batchSize 批量大小
     * @param duration  持续时间
     * @param unit      超时时间单位
     * @param <T>       队列元素类型
     * @return 队列元素集合
     * @throws Exception 异常
     */
    public static <T> List<T> pollBatchOrWait(BlockingQueue<T> queue, int batchSize, long duration, TimeUnit unit) throws Exception {
        // 创建一个具有指定初始容量的空ArrayList实例，用于保存队列中的元素
        List<T> buffer = Lists.newArrayListWithExpectedSize(batchSize);

        // 持续时间
        long deadline = System.nanoTime() + unit.toNanos(duration);
        int added = NumberConstant.INTEGER_ZERO;

        while (added < batchSize) {
            // 从队列中删除指定数量的可用元素，并将它们添加到指定集合中
            added += queue.drainTo(buffer, batchSize - added);
            if (added < batchSize) {
                // 取出来队列第一个元素并删除，可等待指定的等待时间以使元素变为可用，如果队列为空，则返回null
                T element = queue.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
                if (element == null) {
                    break;
                }
                // 添加到集合中
                buffer.add(element);
                ++added;
            }
        }
        return buffer;
    }

    // ==================================================== 数组相关 ====================================================

    /**
     * 数组是否为空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 字节数组是否为空
     *
     * @param bytes 字节数组
     * @return 是否为空
     */
    public static boolean isEmpty(byte[] bytes) {
        return bytes == null || bytes.length == 0;
    }

    /**
     * 数组是否为非空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return (null != array && array.length != 0);
    }

    /**
     * 数组转为ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     * @return ArrayList对象
     */
    @SafeVarargs
    public static <T> List<T> toList(T... values) {
        if (isEmpty(values)) {
            return new ArrayList<>();
        }
        final List<T> arrayList = new ArrayList<>(values.length);
        Collections.addAll(arrayList, values);
        return arrayList;
    }

    // ==================================================== 文件相关 ====================================================

    /**
     * 文件是否为空，文件对象为null，文件长度为0
     *
     * @param file 文件对象
     * @return 是否为空
     */
    public static boolean isEmpty(File file) {
        return file == null || !file.exists() || file.length() == NumberConstant.INTEGER_ZERO;
    }
}
