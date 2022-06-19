package com.doudoudrive.common.util.http;

import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>URL中查询字符串部分的封装测试</p>
 * <p>2022-06-06 14:58</p>
 *
 * @author Dan
 **/
@Slf4j
public class UrlQueryUtilTest {

    /**
     * <p>测试获取URL中查询字符串部分的封装</p>
     */
    @Test
    public void buildUrlQueryParamsTest() {
        String param = "userId=123456789&name=姓 名&email=邮件";
        Map<String, Object> paramMap = Maps.newLinkedHashMapWithExpectedSize(3);
        paramMap.put("userId", "123456789");
        paramMap.put("name", "姓 名");
        paramMap.put("email", "邮件");
        String query = UrlQueryUtil.buildUrlQueryParams(paramMap, false);
        log.info("query: {}", query);
        Assert.isTrue(param.equals(query), "buildUrlQueryParams error !");

        // 对参数的值进行URLEncoder编码
        query = UrlQueryUtil.buildUrlQueryParams(paramMap, true);
        log.info("query: {}", query);
        param = "userId=123456789&name=%E5%A7%93+%E5%90%8D&email=%E9%82%AE%E4%BB%B6";
        Assert.isTrue(param.equals(query), "buildUrlQueryParams error !");

        // 对参数进行排序
        paramMap.put("a", "排第一");
        paramMap.put("b", "排第二");
        paramMap.put("z", "排第二十六");
        query = UrlQueryUtil.buildUrlQueryParams(paramMap, false, true, StandardCharsets.UTF_8, null);
        param = "a=排第一&b=排第二&email=邮件&name=姓 名&userId=123456789&z=排第二十六";
        log.info("query: {}", query);
        Assert.isTrue(param.equals(query), "buildUrlQueryParams error !");
    }

    /**
     * 解析URL中的查询字符串，并将其转换为指定类型测试
     */
    @Test
    public void parseTest() {
        String param = "userId=1&name=&fileParentId=0&fileSize=1&fileMimeType=image%2Fjpeg&fileEtag=a&token=f&callbackUrl=&originalEtag=1&timestamp=1654134821213";
        CreateFileAuthModel createFile = UrlQueryUtil.parse(param, StandardCharsets.UTF_8, CreateFileAuthModel.class);
        Assert.notNull(createFile, "parse error !");
        Assert.isTrue(createFile.getFileMimeType().equals("image/jpeg"), "buildUrlQueryParams error !");
    }

    /**
     * URL 编码测试
     */
    @Test
    public void encodeTest() {
        String src = "中 文";
        String encode = UrlQueryUtil.encode(src, StandardCharsets.UTF_8, null);
        Assert.isTrue(encode.equals("%E4%B8%AD+%E6%96%87"), "encode error !");
        Map<Character, String> customSafe = Maps.newLinkedHashMapWithExpectedSize(3);
        customSafe.put('+', "%20");
        customSafe.put('*', "%2A");
        customSafe.put('~', "%7E");
        src = "中 文+a*~";
        Assert.isTrue(UrlQueryUtil.encode(src, StandardCharsets.UTF_8, customSafe).equals("%E4%B8%AD%20%E6%96%87%2Ba%2A%7E"), "encode error !");
    }

    /**
     * URL 解码测试
     */
    @Test
    public void decodeTest() {
        String src = "中 文+a*~";
        String decode = UrlQueryUtil.decode("%E4%B8%AD%20%E6%96%87%2Ba%2A%7E", StandardCharsets.UTF_8);
        Assert.isTrue(decode.equals(src), "decode error !");
    }
}
