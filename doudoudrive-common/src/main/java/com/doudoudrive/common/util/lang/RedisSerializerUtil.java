package com.doudoudrive.common.util.lang;

import cn.hutool.core.util.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.Serializable;

/**
 * <p>Redis序列化工具</p>
 * <p>2022-04-19 11:53</p>
 *
 * @author Dan
 **/
@Slf4j
public class RedisSerializerUtil<T> implements RedisSerializer<T> {

    /**
     * 序列化接口，对象必须实现Serializable接口
     *
     * @param object object to serialize. Can be {@literal null}.
     * @return 序列化后的字节码
     * @throws SerializationException 抛出序列化失败的异常
     */
    @Override
    public byte[] serialize(T object) throws SerializationException {
        // 构造空的序列化数组
        byte[] result = new byte[0];
        if (ObjectUtils.isEmpty(object)) {
            return result;
        }

        // 对象必须实现Serializable接口
        if (!(object instanceof Serializable)) {
            throw new SerializationException("requires a Serializable payload "
                    + "but received an object of type [" + object.getClass().getName() + "]");
        }

        // 序列化对象
        return SerializeUtil.serialize(object);
    }

    /**
     * 反序列化，对象必须实现Serializable接口<br>
     *
     * @param bytes object binary representation. Can be {@literal null}.
     * @return 反序列化后的对象
     * @throws SerializationException 抛出序列化失败的异常
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        // 反序列化对象
        return SerializeUtil.deserialize(bytes);
    }
}
