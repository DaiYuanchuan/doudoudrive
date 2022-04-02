package com.doudoudrive.common.util.lang;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * <p>分析 User-Agent 获取蜘蛛类型工具类</p>
 * <p>2022-03-14 21:18</p>
 *
 * @author Dan
 **/
public class SpiderUtil {

    /**
     * 目前已知的蜘蛛列表MAP
     */
    private static final Map<String, String> SPIDER = Maps.newHashMapWithExpectedSize(62);

    static {
        SPIDER.put("Baiduspider-image", "百度图片搜索");
        SPIDER.put("Baiduspider-video", "百度视频搜索");
        SPIDER.put("Baiduspider-news", "百度新闻搜索");
        SPIDER.put("Baiduspider-favo", "百度搜藏");
        SPIDER.put("Baiduspider-cpro", "百度联盟");
        SPIDER.put("Baiduspider-sfkr", "百度竞价蜘蛛");
        SPIDER.put("Baiduspider-ads", "百度商务搜索");
        SPIDER.put("Baidu-YunGuanCe", "百度云观测");
        SPIDER.put("Baiduspider", "百度");
        SPIDER.put("www.baidu.com", "百度");
        SPIDER.put("baidu.com", "百度");
        SPIDER.put("Google Web Preview", "谷歌");
        SPIDER.put("Google Search Console", "谷歌站长工具");
        SPIDER.put("Google-Site-Verification", "谷歌站长验证");
        SPIDER.put("Googlebot-Mobile", "谷歌手机搜索");
        SPIDER.put("Googlebot-Image", "谷歌图片搜索");
        SPIDER.put("AppEngine-Google", "谷歌");
        SPIDER.put("Mediapartners", "谷歌");
        SPIDER.put("FeedBurner", "谷歌");
        SPIDER.put("Googlebot", "谷歌");
        SPIDER.put("Google", "谷歌");
        SPIDER.put("google.com", "谷歌");
        SPIDER.put("YoudaoBot", "网易有道");
        SPIDER.put("YodaoBot", "网易有道");
        SPIDER.put("360Spider", "360");
        SPIDER.put("bingbot", "必应");
        SPIDER.put("Yahoo", "雅虎");
        SPIDER.put("Sosospider", "腾讯搜搜");
        SPIDER.put("Sosoimagespider", "搜索图片");
        SPIDER.put("Sogou", "搜狗蜘蛛");
        SPIDER.put("msnbot", "MSN蜘蛛");
        SPIDER.put("YisouSpider", "一搜蜘蛛");
        SPIDER.put("ia_archiver", "Alexa蜘蛛");
        SPIDER.put("EasouSpider", "宜sou蜘蛛");
        SPIDER.put("JikeSpider", "即刻蜘蛛");
        SPIDER.put("EtaoSpider", "一淘网蜘蛛");
        SPIDER.put("AdsBot", "Adwords");
        SPIDER.put("Speedy", "entireweb");
        SPIDER.put("YandexBot", "YandexBot");
        SPIDER.put("AhrefsBot", "AhrefsBot");
        SPIDER.put("ezooms.bot", "ezooms.bot");
        SPIDER.put("Java", "Java程序");
        SPIDER.put("Mnogosearch", "MnoGoSearch搜索引擎（PHP）");
        SPIDER.put("Morfeus Fucking Scanner", "PHP漏洞扫描器");
        SPIDER.put("project25499", "Project 25499扫描器");
        SPIDER.put("25499", "Project 25499扫描器");
        SPIDER.put("James BOT", "JamesBOT搜索引擎");
        SPIDER.put("cognitiveseo", "JamesBOT搜索引擎");
        SPIDER.put("Iframely", "URL Meta Debugger插件");
        SPIDER.put("muhstik-scan", "僵尸网络-挖矿软件");
        SPIDER.put("muhstik", "僵尸网络-挖矿软件");
        SPIDER.put("SEMrushBot", "站点分析蜘蛛");
        SPIDER.put("python-requests", "python爬虫");
        SPIDER.put("python", "python爬虫");
        SPIDER.put("Test Certificate Info", "测试证书信息");
        SPIDER.put("w3m/0.5.3+git20180125", "w3m");
        SPIDER.put("wget", "wget");
        SPIDER.put("curl", "curl");
        SPIDER.put("gnu.org/gnu/wget", "wget");
        SPIDER.put("WinHTTP", "WinHTTP");
        SPIDER.put("WordPress", "WordPress");
        SPIDER.put("Xenu Link Sleuth", "死链接检测工具");
        SPIDER.put("Postman", "Postman");
    }

    /**
     * 分析蜘蛛类型
     *
     * @param userAgent 用户请求的 userAgent
     * @return 返回分析结果 ， 未能查到结果时返回 空字符串
     */
    public static String parseSpiderType(String userAgent) {
        if (StringUtils.isBlank(userAgent)) {
            return "";
        }
        for (Map.Entry<String, String> entry : SPIDER.entrySet()) {
            String spiderSign = entry.getKey();
            if (userAgent.contains(spiderSign)
                    || userAgent.equalsIgnoreCase(spiderSign)
                    || userAgent.toLowerCase().contains(spiderSign.toLowerCase())) {
                return entry.getValue();
            }
        }
        return "";
    }
}
