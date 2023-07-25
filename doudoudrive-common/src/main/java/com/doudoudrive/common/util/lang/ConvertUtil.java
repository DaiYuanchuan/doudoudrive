package com.doudoudrive.common.util.lang;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

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
        } catch (Exception e) {
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
}
