package com.doudoudrive.common.util.lang;

import cn.hutool.core.lang.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * <p>获取文件的mimeType类型工具类单元测试</p>
 * <p>2022-05-22 18:08</p>
 *
 * @author Dan
 **/
@Slf4j
public class MimeTypesTest {

    /**
     * 根据文件名获取文件 mime 类型
     */
    @Test
    public void getMimeTypesTest() {
        String mime = MimeTypes.getInstance().getMimeTypes("aa.sxw");
        String sxw = "application/vnd.sun.xml.writer";
        Assert.isTrue(sxw.equals(mime), "类型匹配错误");

        // 添加新类型
        MimeTypes.getInstance().setMimeTypes("types", "text/plain");

        final ClassPathResource tempResource = Singleton.get(ClassPathResource.class, "/data/mime.types");
        try {
            String fileMime = MimeTypes.getInstance().getMimeTypes(tempResource.getFile());
            Assert.isTrue("text/plain".equals(fileMime), "类型匹配错误");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
