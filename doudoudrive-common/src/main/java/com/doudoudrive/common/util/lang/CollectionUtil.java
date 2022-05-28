package com.doudoudrive.common.util.lang;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
}
