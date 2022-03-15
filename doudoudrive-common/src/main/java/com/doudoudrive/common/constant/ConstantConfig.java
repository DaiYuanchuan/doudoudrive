package com.doudoudrive.common.constant;

import org.springframework.stereotype.Component;

/**
 * <p>基本常量配置</p>
 * <p>2020-10-12 15:50</p>
 *
 * @author Dan
 **/
@Component
public interface ConstantConfig {

    /**
     * 项目默认时区常量配置
     */
    interface TimeZone {

        /**
         * 上海时区
         */
        String DEFAULT_TIME_ZONE = "Asia/Shanghai";
    }

    /**
     * 布尔类型换算
     */
    interface BooleanType {

        /**
         * 正常、正确、成功状态数据.
         */
        String TRUE = "1";

        /**
         * 删除、错误、失败状态数据.
         */
        String FALSE = "0";
    }

    /**
     * HTTP响应状态码
     */
    interface HttpStatusCode {

        /**
         * 请求响应成功
         */
        Integer HTTP_STATUS_CODE_200 = 200;

        /**
         * URL永久性转移
         */
        Integer HTTP_STATUS_CODE_301 = 301;

        /**
         * URL暂时性转移
         */
        Integer HTTP_STATUS_CODE_302 = 302;

    }

    /**
     * HTTP请求相关常量
     */
    interface HttpRequest {

        /**
         * 请求头中的鉴权字段
         */
        String AUTHORIZATION = "Authorization";

        /**
         * 请求头中的referer字段
         */
        String REFERER = "referer";

        /**
         * 微信浏览器请求头中的userAgent标识
         */
        String[] WECHAT_BROWSER_USER_AGENT = {"MicroMessenger/", "WeChat", "Weixin"};

        /**
         * 支付浏览器(支付宝客户端)请求头中的userAgent标识
         */
        String[] ALIPAY_BROWSER_USER_AGENT = {"AlipayClient/", "Alipay", "AliApp"};

        /**
         * ipv6的本地ip地址
         */
        String IPV6_LOCAL_IP = "0:0:0:0:0:0:0:1";

        /**
         * IPV4的本地ip
         */
        String IPV4_LOCAL_IP = "127.0.0.1";
    }

    /**
     * RocketMQ主题相关常量
     */
    interface Topic {

        /**
         * 日志记录服务
         */
        String LOG_RECORD = "LOG_INFO_RECORD";
    }

    /**
     * RocketMQ消息标签，通常用于区分某个 Topic 下的消息分类
     */
    interface Tag {

        /**
         * 登录日志记录
         */
        String LOGIN_LOG_RECORD = "LOGIN_LOG_RECORD";

        /**
         * API接口访问日志记录
         */
        String ACCESS_LOG_RECORD = "ACCESS_LOG_RECORD";
    }

    /**
     * RocketMQ消费者组定义
     */
    interface ConsumerGroup {

        /**
         * 日志服务所属消费者组
         */
        String LOG = "LOG_CONSUMER_GROUP";
    }

    /**
     * RocketMQ生产者组定义
     */
    interface ProducerGroup {

        /**
         * 日志服务生产者
         */
        String LOG = "DOUDOU_MQ_PRODUCER_GROUP";

    }

    /**
     * 特殊符号
     */
    interface SpecialSymbols {

        String ASTERISK = "*";

        String OR = "||";

        String AND = "&&";
    }

    /**
     * 最多一次性处理的任务数量
     */
    Long MAX_BATCH_TASKS_QUANTITY = 400L;

}