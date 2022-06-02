package com.doudoudrive.common.constant;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.model.pojo.DiskUserAttr;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        /**
         * UTC时区
         */
        String UTC = "GMT+:08:00";
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
         * 请求头中的 host 字段
         */
        String HOST = "host";

        /**
         * JSON格式类型
         */
        String CONTENT_TYPE_JSON = "application/json";

        /**
         * format类型
         */
        String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

        /**
         * 文件类型
         */
        String CONTENT_TYPE_MULTIPART = "multipart/form-data";

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

        /**
         * 文件服务
         */
        String FILE_SERVICE = "FILE_SERVICE";
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

        /**
         * 创建文件服务
         */
        String CREATE_FILE = "CREATE_FILE";
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

        /**
         * FILE服务所属消费者组
         */
        String FILE = "FILE_CONSUMER_GROUP";
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
     * MQ消息发送状态类型枚举
     */
    enum MqMessageSendStatus {

        /**
         * 发送成功
         */
        SEND_OK(SendStatus.SEND_OK, "1"),

        /**
         * 刷新磁盘超时
         */
        FLUSH_DISK_TIMEOUT(SendStatus.FLUSH_DISK_TIMEOUT, "2"),

        /**
         * 刷新从属超时
         */
        FLUSH_SLAVE_TIMEOUT(SendStatus.FLUSH_SLAVE_TIMEOUT, "3"),

        /**
         * 从属服务器不可用
         */
        SLAVE_NOT_AVAILABLE(SendStatus.SLAVE_NOT_AVAILABLE, "4");

        /**
         * 消息发送状态枚举值
         */
        public final SendStatus sendStatus;

        /**
         * 消息发送状态枚举映射
         */
        public final String status;

        MqMessageSendStatus(SendStatus sendStatus, String status) {
            this.sendStatus = sendStatus;
            this.status = status;
        }

        /**
         * 通过消息发送状态枚举值获取到枚举值的映射
         *
         * @param sendStatus 消息发送状态枚举值
         * @return 消息发送状态枚举映射
         */
        public static String getStatusValue(SendStatus sendStatus) {
            return Stream.of(MqMessageSendStatus.values())
                    .filter(anEnum -> anEnum.sendStatus.equals(sendStatus))
                    .map(anEnum -> anEnum.status).findFirst().orElse(CharSequenceUtil.EMPTY);
        }
    }

    /**
     * 特殊符号
     */
    interface SpecialSymbols {
        String DOT = ".";
        String ASTERISK = "*";
        String OR = "||";
        String QUESTION_MARK = "?";
        String COMMA = ",";
        String AND = "&&";
        String AMPERSAND = "&";
        String EQUALS = "=";
        String PLUS_SIGN = "+";
        String TILDE = "~";
        String ENGLISH_COLON = ":";
        String COMMENT_SIGN = "#";
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

        /**
         * 用户属性表依据 user_id 平均分 50 个表
         */
        Integer DISK_USER_ATTR = 50;

        /**
         * 用户文件模块依据 user_id 分 500 个表
         */
        Integer DISK_FILE = 500;

        /**
         * OSS文件对象存储模块依据 file_etag 平均分 300 个表
         */
        Integer OSS_FILE = 300;
    }

    /**
     * 日期时间单位，每个单位都是以毫秒为基数
     */
    enum DateUnit {
        /**
         * 一毫秒 = 0.001 秒
         */
        MS(1L, null),

        /**
         * 一秒的毫秒数 = 1秒
         */
        SECOND(1000L, 1L),

        /**
         * 一分钟的毫秒数 = 60秒
         */
        MINUTE(SECOND.ms * 60, SECOND.s * 60),

        /**
         * 一小时的毫秒数 = 3600秒
         */
        HOUR(MINUTE.ms * 60, MINUTE.s * 60),

        /**
         * 一天的毫秒数 = 86400秒
         */
        DAY(HOUR.ms * 24, HOUR.s * 24),

        /**
         * 一周的毫秒数 = 604800 秒
         */
        WEEK(DAY.ms * 7, DAY.s * 7);

        /**
         * 毫秒数
         */
        public final Long ms;

        /**
         * 秒数
         */
        public final Long s;

        DateUnit(Long ms, Long s) {
            this.ms = ms;
            this.s = s;
        }
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
         * 用户机密信息的缓存
         */
        String USER_CONFIDENTIAL = "userConfidential";

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
         * 缓存内容默认失效时间(10小时):秒
         */
        Long DEFAULT_EXPIRE = 36000L;

        /**
         * 邮箱验证码缓存前缀
         */
        String MAIL_VERIFICATION_CODE = "MAIL:VERIFICATION_CODE:";

        /**
         * 用户文件信息缓存
         */
        String DISK_FILE_CACHE = "DISK_FILE_CACHE:";

        /**
         * OSS文件信息缓存
         */
        String OSS_FILE_CACHE = "OSS_FILE_CACHE:";

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

        String DISK_FILE = "disk_file";
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
     * 消息记录类型枚举
     */
    enum SmsTypeEnum {
        /**
         * 邮箱
         */
        MAIL("1"),

        /**
         * 短信
         */
        SMS("2");

        /**
         * 消息记录类型
         */
        public final String type;

        SmsTypeEnum(String type) {
            this.type = type;
        }
    }

    /**
     * 用户属性枚举
     */
    enum UserAttrEnum {

        /**
         * 当前可用的磁盘总容量(单位:字节)，默认100GB
         */
        TOTAL_DISK_CAPACITY("totalDiskCapacity", "107374182400"),

        /**
         * 当前已经使用的磁盘容量(单位:字节)
         */
        USED_DISK_CAPACITY("usedDiskCapacity", "0"),

        /**
         * 当前可用总流量(单位:字节)，默认1GB
         */
        TOTAL_TRAFFIC("totalTraffic", "1073741824"),

        /**
         * 当前已经使用的流量(单位:字节)
         */
        USED_TRAFFIC("usedTraffic", "0");

        /**
         * 用户属性参数名称
         */
        public final String param;

        /**
         * 用户属性参数的默认值
         */
        public final String defaultValue;

        UserAttrEnum(String param, String defaultValue) {
            this.param = param;
            this.defaultValue = defaultValue;
        }

        /**
         * @return 构建初始化map
         */
        public static Map<String, String> builderMap() {
            // 枚举值转换为TreeMap
            return Stream.of(values()).collect(Collectors.toMap(value -> value.param, value -> value.defaultValue, (key1, key2) -> key2, TreeMap::new));
        }

        /**
         * 构建初始化List
         *
         * @param userId 用户标识
         * @return 初始化用户属性List集合
         */
        public static List<DiskUserAttr> builderList(String userId) {
            final List<DiskUserAttr> userAttrList = new ArrayList<>();
            for (UserAttrEnum attrEnum : values()) {
                userAttrList.add(DiskUserAttr.builder()
                        .userId(userId)
                        .attributeName(attrEnum.param)
                        .attributeValue(new BigDecimal(attrEnum.defaultValue))
                        .build());
            }
            return userAttrList;
        }
    }

    /**
     * 文件记录动作相关常量
     */
    interface FileRecordAction {

        /**
         * 文件记录-动作
         */
        enum ActionEnum {

            /**
             * 文件状态
             */
            FILE("0"),

            /**
             * 文件内容
             */
            FILE_CONTENT("1");

            /**
             * 状态标识
             */
            public final String status;

            ActionEnum(String status) {
                this.status = status;
            }
        }

        /**
         * 文件记录-动作类型
         */
        enum ActionTypeEnum {

            // Action为0时对应的动作类型

            /**
             * 被删除
             */
            BE_DELETED("0"),

            // Action为1时对应的动作类型

            /**
             * 待审核
             */
            REVIEWED("0"),

            /**
             * 待删除
             */
            TO_DELETE("1");

            /**
             * 状态标识
             */
            public final String status;

            ActionTypeEnum(String status) {
                this.status = status;
            }
        }
    }

    /**
     * oss文件当前状态枚举类型
     */
    enum OssFileStatusEnum {

        /**
         * 正常，默认值
         */
        NORMAL("0"),

        /**
         * 待审核，图片、视频增量审查
         */
        PENDING_REVIEW("1"),

        /**
         * 审核失败，同步的用户文件被禁止访问，源文件待删除
         */
        AUDIT_FAILURE("2"),

        /**
         * 源文件已删除
         */
        SOURCE_FILE_DELETED("3");

        public final String status;

        OssFileStatusEnum(String status) {
            this.status = status;
        }

        /**
         * 判断文件当前状态是否是被禁止的
         *
         * @param fileStatus 文件状态
         * @return true:文件被禁止访问 false:文件是正常的
         */
        public static boolean forbidden(String fileStatus) {
            // 规定不可操作类型
            List<String> inoperableType = Arrays.asList(AUDIT_FAILURE.status, SOURCE_FILE_DELETED.status);
            return inoperableType.contains(fileStatus);
        }
    }

    /**
     * 七牛云相关常量配置
     */
    interface QiNiuConstant {
        /**
         * 七牛云请求鉴权的前缀(QBox)
         */
        String QBOX_AUTHORIZATION_PREFIX = "QBox ";

        /**
         * 七牛云请求鉴权的前缀(Qiniu)
         */
        String QI_NIU_AUTHORIZATION_PREFIX = "Qiniu ";

        /**
         * 七牛云上传回调时请求头中的请求id
         */
        String QI_NIU_CALLBACK_REQUEST_ID = "X-Reqid";

        /**
         * 七牛云上传回调-获得上传的目标空间名
         */
        String QI_NIU_CALLBACK_BUCKET = "$(bucket)";

        /**
         * 七牛云上传回调-获得文件保存在空间中的资源名
         */
        String QI_NIU_CALLBACK_KEY = "$(key)";

        /**
         * 七牛云上传回调-上传的原始文件名
         */
        String QI_NIU_CALLBACK_FILE_NAME = "$(fname)";

        /**
         * 七牛云上传回调-资源尺寸，单位为字节
         */
        String QI_NIU_CALLBACK_FILE_SIZE = "$(fsize)";

        /**
         * 七牛云上传回调-资源类型，例如JPG图片的资源类型为image/jpg
         */
        String QI_NIU_CALLBACK_FILE_MIME_TYPE = "$(mimeType)";

        /**
         * 七牛云上传回调-上传时指定的endUser字段，通常用于区分不同终端用户的请求
         */
        String QI_NIU_CALLBACK_END_USER = "$(endUser)";

        /**
         * 七牛云上传回调-音视频转码持久化的进度查询ID
         */
        String QI_NIU_CALLBACK_PERSISTENT_ID = "$(persistentId)";

        /**
         * 七牛云上传回调-获取上传图片的Exif信息，该变量包含子字段
         */
        String QI_NIU_CALLBACK_EXIF = "$(exif)";

        /**
         * 获取所上传图片的基本信息，该变量包含子字段，例如对$(imageInfo.width)取值将得到该图片的宽度
         */
        String QI_NIU_CALLBACK_IMAGE_INFO = "$(imageInfo)";

        /**
         * 音视频资源的元信息
         */
        String QI_NIU_CALLBACK_AV_INFO = "$(avinfo)";

        /**
         * 图片主色调
         */
        String QI_NIU_CALLBACK_IMAGE_AVE = "$(imageAve)";

        /**
         * 七牛云上传回调-文件etag
         */
        String QI_NIU_CALLBACK_FILE_ETAG = "$(etag)";
    }

    /**
     * 最多一次性处理的任务数量
     */
    Long MAX_BATCH_TASKS_QUANTITY = 400L;

}
