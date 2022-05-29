package com.doudoudrive.common.util.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>URL中查询字符串部分的封装，包含参数转换等</p>
 * <p>2022-05-28 17:24</p>
 *
 * @author Dan
 **/
public class UrlQueryUtil {

    /**
     * 字符 +
     */
    private static final char CORNER = '+';

    /**
     * 字符 %
     */
    private static final byte ESCAPE_CHAR = '%';

    /**
     * 字符 空格
     */
    private static final char SPACE = ' ';

    /**
     * 字符 =
     */
    private static final char EQ = '=';

    /**
     * 字符 &
     */
    private static final char AND = '&';

    /**
     * 字符 amp;
     */
    private static final String AMP = "amp;";

    /**
     * 常量 16
     */
    private static final Integer SIXTEEN = NumberConstant.INTEGER_TEN + NumberConstant.INTEGER_SIX;

    /**
     * 构建URL查询字符串，即将key-value键值对转换为{@code key1=v1&key2=v2&key3=v3}形式。<br>
     * 对于{@code null}处理规则如下：
     * <ul>
     *     <li>如果key为{@code null}，则这个键值对忽略</li>
     *     <li>如果value为{@code null}，只保留key，如key1对应value为{@code null}生成类似于{@code key1&key2=v2}形式</li>
     * </ul>
     *
     * @param paramMap key-value参数键值对
     * @param encode   是否对参数的值进行URLEncoder
     * @return URL查询字符串
     */
    public static String buildUrlQueryParams(Map<String, Object> paramMap, boolean encode) {
        return buildUrlQueryParams(paramMap, encode, false, StandardCharsets.UTF_8, null);
    }

    /**
     * 构建URL查询字符串，即将key-value键值对转换为{@code key1=v1&key2=v2&key3=v3}形式。<br>
     * 对于{@code null}处理规则如下：
     * <ul>
     *     <li>如果key为{@code null}，则这个键值对忽略</li>
     *     <li>如果value为{@code null}，只保留key，如key1对应value为{@code null}生成类似于{@code key1&key2=v2}形式</li>
     * </ul>
     *
     * @param paramMap   key-value参数键值对
     * @param encode     是否对参数的值进行URLEncoder
     * @param sort       排序
     * @param charset    encode编码
     * @param customSafe 自定义的安全字符
     * @return URL查询字符串
     */
    public static String buildUrlQueryParams(Map<String, Object> paramMap, boolean encode, boolean sort, Charset charset, Map<String, String> customSafe) {
        if (CollectionUtil.isEmpty(paramMap)) {
            return CharSequenceUtil.EMPTY;
        }

        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(paramMap.keySet());
        if (sort) {
            // 对Map进行排序
            Collections.sort(keys);
        }

        for (String key : keys) {
            if (StringUtils.isNotBlank(key)) {
                if (content.length() > NumberConstant.INTEGER_ZERO) {
                    content.append(ConstantConfig.SpecialSymbols.AMPERSAND);
                }
                content.append(key);
                Object value = paramMap.get(key);
                if (ObjectUtils.isEmpty(value)) {
                    continue;
                }
                content.append(ConstantConfig.SpecialSymbols.EQUALS).append(encode ? encode(toStr(value), charset, customSafe) : toStr(value));
            }
        }
        return content.toString();
    }

    /**
     * 解析URL中的查询字符串，并将其转换为指定类型<br>
     * 规则见：<a href="https://url.spec.whatwg.org/#urlencoded-parsing">https://url.spec.whatwg.org/#urlencoded-parsing</a>
     *
     * @param paramStr 查询字符串，类似于key1=v1&amp;key2=&amp;key3=v3
     * @param charset  decode编码
     * @param clazz    需要转换的类型
     * @param <T>      类型
     * @return 查询字符串转换指定类型的结果
     */
    public static <T> T parse(String paramStr, Charset charset, Class<T> clazz) {
        if (StringUtils.isBlank(paramStr)) {
            return null;
        }

        JSONObject jsonObject = new JSONObject();
        String name = null;
        // 未处理字符开始位置
        int pos = NumberConstant.INTEGER_ZERO;
        // 未处理字符结束位置
        int i;
        for (i = NumberConstant.INTEGER_ZERO; i < paramStr.length(); i++) {
            switch (paramStr.charAt(i)) {
                case EQ:
                    if (null == name) {
                        // name可以是 ""
                        name = paramStr.substring(pos, i);
                        // 开始位置从分节符后开始
                        pos = i + NumberConstant.INTEGER_ONE;
                    }
                    // 当=不作为分界符时，按照普通字符对待
                    break;
                case AND:
                    addParam(name, paramStr.substring(pos, i), jsonObject, charset);
                    name = null;
                    if (i + NumberConstant.INTEGER_FOUR < paramStr.length()
                            && AMP.equals(paramStr.substring(i + NumberConstant.INTEGER_ONE, i + NumberConstant.INTEGER_FIVE))) {
                        // &amp;转义为"&"
                        i += NumberConstant.INTEGER_FOUR;
                    }
                    // 开始位置从分节符后开始
                    pos = i + NumberConstant.INTEGER_ONE;
                    break;
                default:
                    break;
            }
        }

        // 处理结尾
        addParam(name, paramStr.substring(pos, i), jsonObject, charset);
        return jsonObject.toJavaObject(clazz);
    }

    /**
     * URL 编码，可以自定义需要转换的字符
     *
     * @param src        需要编码的字符串
     * @param charset    encode编码
     * @param customSafe 自定义的安全字符
     * @return 编码后的字符串
     */
    public static String encode(String src, Charset charset, Map<String, String> customSafe) {
        String encode = URLEncoder.encode(src, charset);
        if (CollectionUtil.isEmpty(customSafe)) {
            return encode;
        }

        for (Map.Entry<String, String> entry : customSafe.entrySet()) {
            encode = encode.replace(entry.getKey(), entry.getValue());
        }

        return encode;
    }

    /**
     * URL 解码，可以自定义需要转换的字符
     *
     * @param src        需要解码的字符串
     * @param charset    encode编码
     * @param customSafe 自定义的安全字符
     * @return 解码后的字符串
     */
    public static String decode(String src, Charset charset, Map<String, String> customSafe) {
        if (CollectionUtil.isNotEmpty(customSafe)) {
            for (Map.Entry<String, String> entry : customSafe.entrySet()) {
                src = src.replace(entry.getKey(), entry.getValue());
            }
        }

        return decode(src, charset);
    }

    /**
     * 解码<br>
     * 规则见：<a href="https://url.spec.whatwg.org/#urlencoded-parsing">https://url.spec.whatwg.org/#urlencoded-parsing</a>
     * <pre>
     *   1. 将+和%20转换为空格(" ");
     *   2. 将"%xy"转换为文本形式,xy是两位16进制的数值;
     *   3. 跳过不符合规范的%形式，直接输出
     * </pre>
     *
     * @param src     包含URL编码后的字符串
     * @param charset 编码
     * @return 解码后的字符串
     */
    public static String decode(String src, Charset charset) {
        return decode(src, charset, true);
    }

    /**
     * 解码
     * <pre>
     *   1. 将%20转换为空格 ;
     *   2. 将"%xy"转换为文本形式,xy是两位16进制的数值;
     *   3. 跳过不符合规范的%形式，直接输出
     * </pre>
     *
     * @param src           包含URL编码后的字符串
     * @param isPlusToSpace 是否+转换为空格
     * @param charset       编码
     * @return 解码后的字符串
     */
    public static String decode(String src, Charset charset, boolean isPlusToSpace) {
        if (StringUtils.isBlank(src)) {
            return CharSequenceUtil.EMPTY;
        }

        // 获取字符串的字节码
        byte[] bytes = charset == null ? src.getBytes() : src.getBytes(charset);

        // 获取解码后的数据
        byte[] decode = decode(bytes, isPlusToSpace);
        if (decode == null) {
            return CharSequenceUtil.EMPTY;
        }
        return charset == null ? new String(decode) : new String(decode, charset);
    }

    /**
     * 解码
     * <pre>
     *   1. 将%20转换为空格 ;
     *   2. 将"%xy"转换为文本形式,xy是两位16进制的数值;
     *   3. 跳过不符合规范的%形式，直接输出
     * </pre>
     *
     * @param bytes         url编码的bytes
     * @param isPlusToSpace 是否+转换为空格
     * @return 解码后的bytes
     */
    public static byte[] decode(byte[] bytes, boolean isPlusToSpace) {
        if (CollectionUtil.isEmpty(bytes)) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream(bytes.length);
        for (int i = NumberConstant.INTEGER_ZERO; i < bytes.length; i++) {
            int b = bytes[i];
            switch (b) {
                case CORNER -> buffer.write(isPlusToSpace ? SPACE : b);
                case ESCAPE_CHAR -> {
                    if (i + NumberConstant.INTEGER_ONE < bytes.length) {
                        // 获取给定字符的16进制数值
                        final int digit = Character.digit(bytes[i + NumberConstant.INTEGER_ONE], SIXTEEN);
                        if (digit >= NumberConstant.INTEGER_ZERO && i + NumberConstant.INTEGER_TWO < bytes.length) {
                            final int l = Character.digit(bytes[i + NumberConstant.INTEGER_TWO], SIXTEEN);
                            if (l >= NumberConstant.INTEGER_ZERO) {
                                buffer.write((char) ((digit << NumberConstant.INTEGER_FOUR) + l));
                                i += NumberConstant.INTEGER_TWO;
                                continue;
                            }
                        }
                    }
                    // 跳过不符合规范的%形式
                    buffer.write(b);
                }
                default -> buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }

    // ==================================================== private ====================================================

    /**
     * 对象转换为字符串，用于URL的Query中
     *
     * @param value 值
     * @return 字符串
     */
    private static String toStr(Object value) {
        if (value instanceof Iterable) {
            return CollUtil.join((Iterable<?>) value, ConstantConfig.SpecialSymbols.COMMA);
        }

        if (value instanceof Iterator) {
            return IterUtil.join((Iterator<?>) value, ConstantConfig.SpecialSymbols.COMMA);
        }

        return Convert.toStr(value);
    }

    /**
     * 将键值对加入到值为JSONObject对象中,，情况如下：
     * <pre>
     *     1、key和value都不为null，类似于 "a=1"或者"=1"，直接put
     *     2、key不为null，value为null，类似于 "a="，值传""
     *     3、key为null，value不为null，类似于 "1"
     *     4、key和value都为null，忽略之，比如&&
     * </pre>
     *
     * @param key        key，为null则value作为key
     * @param value      value，为null且key不为null时传入""
     * @param jsonObject 存储介质
     * @param charset    编码
     */
    private static void addParam(String key, String value, JSONObject jsonObject, Charset charset) {
        if (StringUtils.isNotBlank(key)) {
            jsonObject.put(decode(key, charset, Boolean.TRUE), decode(value, charset, Boolean.TRUE));
            return;
        }

        // name为空，value作为name，value赋值null
        if (StringUtils.isNotBlank(value)) {
            jsonObject.put(decode(value, charset, Boolean.TRUE), null);
        }
    }
}