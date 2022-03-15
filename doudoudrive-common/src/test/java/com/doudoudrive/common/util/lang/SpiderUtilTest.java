package com.doudoudrive.common.util.lang;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * <p>蜘蛛类型工具类单元测试</p>
 * <p>2022-03-14 21:21</p>
 *
 * @author Dan
 **/
public class SpiderUtilTest {

    private static final String USER_AGENT = "Mozilla/5.0 (compatible; Baiduspider/2.0; +https://www.baidu.com/search/spider.html)";

    @Test
    public void parseSpiderTypeTest() {
        Assert.isTrue("百度".equals(SpiderUtil.parseSpiderType(USER_AGENT)), "parseSpiderType error !");
    }

}
