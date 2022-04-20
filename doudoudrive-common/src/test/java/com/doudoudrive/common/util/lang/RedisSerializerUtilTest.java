package com.doudoudrive.common.util.lang;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * <p>序列化工具测试</p>
 * <p>2022-04-19 12:16</p>
 *
 * @author Dan
 **/
@Slf4j
public class RedisSerializerUtilTest {

    /**
     * 字符串序列化测试
     */
    @Test
    public void serializeStrTest() {
        String str = "字符串序列化";
        RedisSerializerUtil<String> redisSerializerUtil = new RedisSerializerUtil<>();
        // 字符串序列化
        byte[] serialize = redisSerializerUtil.serialize(str);
        log.info("serialize: {}", str.equals(redisSerializerUtil.deserialize(serialize)));
    }

}
