package com.doudoudrive.common.util.lang;

import com.doudoudrive.common.constant.NumberConstant;
import com.github.luben.zstd.Zstd;

/**
 * <p>集成压缩算法工具类</p>
 * <p>2022-11-11 12:12</p>
 *
 * @author Dan
 **/
public class CompressionUtil {

    /**
     * 字节压缩
     *
     * @param bytes 字节数组
     * @return 压缩后的字节
     */
    public static byte[] compress(byte[] bytes) {
        if (CollectionUtil.isEmpty(bytes)) {
            return new byte[NumberConstant.INTEGER_ZERO];
        }
        return Zstd.compress(bytes);
    }

    /**
     * 字节解压缩为字节数组
     *
     * @param bytes 字节数组
     * @return 解压后的字节数组
     */
    public static byte[] decompressBytes(byte[] bytes) {
        if (CollectionUtil.isEmpty(bytes)) {
            return new byte[NumberConstant.INTEGER_ZERO];
        }
        int size = (int) Zstd.decompressedSize(bytes);
        byte[] ob = new byte[size];
        Zstd.decompress(ob, bytes);
        return ob;
    }
}
