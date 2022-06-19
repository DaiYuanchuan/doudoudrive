package com.doudoudrive.common.util.lang;

import com.doudoudrive.common.constant.NumberConstant;

/**
 * <p>摘选自myCat分片路由(PartitionByJumpConsistentHash)算法</p>
 * <p>跳增一致性哈希分片</p>
 * <p>思想源自Google公开论文，比传统一致性哈希更省资源速度更快数据迁移量更少</p>
 * <p>2022-06-13 10:06</p>
 *
 * @author Dan
 **/
public class PartitionByJumpConsistentHash {

    private static final long UNSIGNED_MASK = 0x7fffffffffffffffL;

    /**
     * 跳增步长
     */
    private static final long JUMP = 1L << 31;

    /**
     * 如果JDK >= 1.8，需使用 Long.parseUnsignedLong("2862933555777941757") 来代替。
     * 如果JDK < 1.8， 需使用 Long.parseLong("286293355577794175", 10) * 10 + 7 来代替。
     */
    private static final long CONSTANT = Long.parseUnsignedLong("2862933555777941757");

    /**
     * 常量 33
     */
    private static final long THIRTY_THREE = 33;

    /**
     * 计算分片数量
     *
     * @param columnValue 分片值
     * @param buckets     分片数量
     * @return 分片索引
     */
    public static Integer calculate(String columnValue, final int buckets) {
        return jumpConsistentHash(columnValue.hashCode(), buckets);
    }

    /**
     * 跳增一致性哈希算法
     *
     * @param key     分片值
     * @param buckets 分片数量
     * @return 分片索引
     */
    public static int jumpConsistentHash(final long key, final int buckets) {
        checkBuckets(buckets);
        long k = key;
        long b = NumberConstant.LONG_MINUS_ONE;
        long j = NumberConstant.LONG_ZERO;

        while (j < buckets) {
            b = j;
            k = k * CONSTANT + NumberConstant.LONG_ONE;
            j = (long) ((b + NumberConstant.LONG_ONE) * (JUMP / toDouble((k >>> THIRTY_THREE) + NumberConstant.LONG_ONE)));
        }
        return (int) b;
    }

    // ==================================================== private ====================================================

    private static void checkBuckets(final int buckets) {
        if (buckets < NumberConstant.INTEGER_ZERO) {
            throw new IllegalArgumentException("Buckets cannot be less than 0");
        }
    }

    private static double toDouble(final long n) {
        double d = n & UNSIGNED_MASK;
        if (n < NumberConstant.INTEGER_ZERO) {
            d += 0x1.0p63;
        }
        return d;
    }
}
