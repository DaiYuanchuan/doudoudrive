package com.doudoudrive.common.util.lang;

import com.doudoudrive.common.constant.NumberConstant;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Protostuff 对象序列化工具</p>
 * <p>2022-11-11 16:39</p>
 *
 * @author Dan
 **/
public class ProtostuffUtil<T> {

    /**
     * 缓存Schema
     */
    private final Map<Class<T>, Schema<T>> SCHEMA_CACHE = new ConcurrentHashMap<>();

    /**
     * 序列化方法，把指定对象序列化成字节数组
     *
     * @param object 需要序列化的对象
     * @return 序列化后的字节数组
     */
    public byte[] serialize(T object) {
        Schema<T> schema = getSchema(ConvertUtil.convert(object.getClass()));
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        byte[] data;

        try {
            data = ProtobufIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
            data = new byte[NumberConstant.INTEGER_ZERO];
        } finally {
            buffer.clear();
        }

        return data;
    }

    /**
     * 反序列化方法，将字节数组反序列化成指定Class类型
     *
     * @param data  需要反序列化的字节数组
     * @param clazz 反序列化后的对象类型
     * @return 反序列化后的对象
     */
    public T deserialize(byte[] data, Class<T> clazz) {
        if (CollectionUtil.isEmpty(data)) {
            return null;
        }
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtobufIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

    /**
     * 从缓存中获取Schema
     *
     * @param clazz 类
     * @return Schema
     */
    private Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = SCHEMA_CACHE.get(clazz);
        if (Objects.isNull(schema)) {
            // 这个schema通过RuntimeSchema进行懒创建并缓存，所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                SCHEMA_CACHE.put(clazz, schema);
            }
        }
        return schema;
    }
}
