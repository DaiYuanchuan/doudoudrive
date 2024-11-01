package com.doudoudrive.common.util.lang;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>对象类型转换工具</p>
 * <p>2022-04-25 19:08</p>
 *
 * @author Dan
 **/
@Slf4j
public class ConvertUtil {

    /**
     * Base64编码器
     */
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * 对象类型强制转换
     *
     * @param object 待转换的对象
     * @param <T>    转换强制的类型
     * @return 输出强制转换后的类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object object) {
        if (object == null) {
            return null;
        }

        try {
            return (T) object;
        } catch (Throwable e) {
            log.error("object cast exception:", e);
            return null;
        }
    }

    /**
     * 将字节数组转换为Base64字符串
     *
     * @param body 字节数组
     * @return Base64字符串
     */
    public static String convertBase64(byte[] body) {
        if (CollectionUtil.isEmpty(body)) {
            return StringUtils.EMPTY;
        }
        return ENCODER.encodeToString(body);
    }

    /**
     * Java Bean转为HashMap，key为属性名，value为属性值
     *
     * @param bean Java Bean
     * @param <T>  Java Bean类型
     * @return Map
     */
    public static <T> Map<String, Object> convertBeanToMap(T bean) {
        // bean转为Map
        BeanMap beanMap = BeanMap.create(bean);

        Map<Object, Object> fieldMap = Maps.newHashMapWithExpectedSize(beanMap.size());
        fieldMap.putAll(beanMap);
        return fieldMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .collect(Collectors.toMap(
                        entry -> {
                            if (entry.getKey() instanceof String) {
                                return (String) entry.getKey();
                            }

                            if (entry.getKey() instanceof byte[]) {
                                return new String((byte[]) entry.getKey(), StandardCharsets.UTF_8);
                            }

                            return entry.getKey().toString();
                        },
                        entry -> Optional.ofNullable(entry.getValue()).orElse(StringUtils.EMPTY)
                ));
    }

    /**
     * Map转为Java Bean，key为属性名，value为属性值
     *
     * @param objectMap Map
     * @param <T>       Java Bean类型
     * @return Java Bean
     */
    public static <T> T convertMapToBean(Class<T> clazz, Map<String, Object> objectMap) throws Exception {
        T bean = clazz.getDeclaredConstructor().newInstance();
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(objectMap);
        return bean;
    }
}
