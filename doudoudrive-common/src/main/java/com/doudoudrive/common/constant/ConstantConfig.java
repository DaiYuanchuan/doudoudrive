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
         * Cookie内容中的鉴权字段
         */
        String TOKEN = "token";

        /**
         * 请求头中的referer字段
         */
        String REFERER = "referer";

        /**
         * 请求头中的header字段
         */
        String HEADER = "header";

        /**
         * 浏览器UA标识
         */
        String USER_AGENT = "User-Agent";

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

        /**
         * SMS服务
         */
        String SMS_SERVICE = "SMS_SERVICE";
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

        /**
         * 邮件发送
         */
        String SEND_MAIL = "SEND_MAIL";
    }

    /**
     * RocketMQ消费者订阅组定义
     */
    interface ConsumerGroup {

        /**
         * 日志服务所属消费者组
         */
        String LOG = "LOG_CONSUMER_GROUP";

        /**
         * SMS服务所属消费者组
         */
        String SMS = "SMS_CONSUMER_GROUP";
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

        String ENGLISH_COLON = ":";
    }

    /**
     * 各模块分表后缀常量
     */
    interface TableSuffix {

        /**
         * 用户表依据 业务id 平均分 20 个表
         */
        Integer USERINFO = 20;

        /**
         * 用户与角色关联表依据 user_id 平均分 50 个表
         */
        Integer SYS_USER_ROLE = 50;
    }

    /**
     * 缓存相关的常量
     */
    interface Cache {

        /**
         * 用户信息缓存
         */
        String USERINFO_CACHE = "userInfo";

        /**
         * 用户角色信息缓存
         */
        String USER_ROLE_CACHE = "AuthorizationInfo";

        /**
         * 默认session内容缓存的key值前缀
         */
        String DEFAULT_CACHE_KEY_PREFIX = "shiro:cache:";

        /**
         * session内容缓存默认key值字段名称
         */
        String DEFAULT_PRINCIPAL_ID_FIELD_NAME = "id";

        /**
         * session内容缓存默认失效时间(10小时):秒
         */
        Long DEFAULT_EXPIRE = 36000L;

        /**
         * redis事件监听器类型枚举，所有通知以__keyevent@<db>__为前缀，这里的<db>可以用通配符*代替
         */
        enum KeyEventEnum {

            /**
             * 过期事件
             */
            EXPIRED("__keyevent@*__:expired"),

            /**
             * 新增 、修改事件
             */
            UPDATE("__keyevent@*__:set"),

            /**
             * 删除事件
             */
            DELETE("__keyevent@*__:del");

            /**
             * 监听的事件类型
             */
            public final String event;

            KeyEventEnum(String event) {
                this.event = event;
            }
        }

        /**
         * redis通道名称枚举(redis需要订阅的渠道名称)
         */
        enum ChanelEnum {
            /**
             * redis刷新配置专用通道名
             */
            CHANNEL_CONFIG("DOUDOU_CONFIG_CHANNEL"),

            /**
             * redis缓存刷新同步专用通道名(redis需要订阅的渠道名称)
             */
            CHANNEL_CACHE("DOUDOU_CACHE_CHANNEL");

            /**
             * 通道名称
             */
            public final String channel;

            ChanelEnum(String channel) {
                this.channel = channel;
            }
        }
    }

    /**
     * es索引名称相关常量
     */
    interface IndexName {
        String USERINFO = "userinfo";
    }

    /**
     * ik分词器相关常量
     */
    interface IkConstant {

        /**
         * 最粗粒度的拆分
         * <p>比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”，适合 Phrase 查询</p>
         */
        String IK_SMART = "ik_smart";

        /**
         * 最大化会将文本做最细粒度的拆分
         * <p>比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合，适合 Term Query；</p>
         */
        String IK_MAX_WORD = "ik_max_word";
    }

    /**
     * SMS消息发送状态枚举
     */
    enum SmsStatusEnum {

        /**
         * 待分发
         */
        WAIT("1"),

        /**
         * 发送成功
         */
        SUCCESS("2"),

        /**
         * 发送失败
         */
        FAIL("3");

        /**
         * SMS消息发送状态
         */
        public final String status;

        SmsStatusEnum(String status) {
            this.status = status;
        }
    }

    /**
     * 最多一次性处理的任务数量
     */
    Long MAX_BATCH_TASKS_QUANTITY = 400L;

}
