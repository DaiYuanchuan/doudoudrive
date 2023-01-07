package com.doudoudrive.common.constant;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.DiskUserAttr;
import com.doudoudrive.common.model.pojo.FileShare;
import com.doudoudrive.common.util.lang.ReflectUtil;
import lombok.Getter;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
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
         * 请求内容中的鉴权字段
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
         * utf-8编码
         */
        String UTF8 = "utf-8";

        /**
         * JSON格式类型
         */
        String CONTENT_TYPE_JSON = "application/json";

        /**
         * JSON格式类型(utf-8)
         */
        String CONTENT_TYPE_JSON_UTF8 = "application/json;charset=UTF-8";

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
         * 业务接口的请求日志记录服务(业务日志入MySQL库)
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

        /**
         * 文件搜索服务
         */
        String FILE_SEARCH_SERVICE = "FILE_SEARCH_SERVICE";
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

        /**
         * 创建文件失败时异步回滚服务
         */
        String CREATE_FILE_ROLLBACK = "CREATE_FILE_ROLLBACK";

        /**
         * 删除文件服务
         */
        String DELETE_FILE = "DELETE_FILE";

        /**
         * 复制文件服务
         */
        String COPY_FILE = "COPY_FILE";

        /**
         * 删除文件ES服务
         */
        String DELETE_FILE_ES = "DELETE_FILE_ES";

        /**
         * 保存文件ES服务
         */
        String SAVE_FILE_ES = "SAVE_FILE_ES";
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

        /**
         * FILE搜索服务所属消费者组
         */
        String FILE_SEARCH = "FILE_SEARCH_CONSUMER_GROUP";
    }

    /**
     * MQ消息发送状态类型枚举
     */
    @Getter
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
        private final SendStatus sendStatus;

        /**
         * 消息发送状态枚举映射
         */
        private final String status;

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
     * 日志链路追踪通用常量
     */
    interface LogTracer {

        /**
         * 日志链路追踪id
         */
        String TRACER_ID = "tracerId";

        /**
         * 日志链路追踪调度id
         */
        String SPAN_ID = "spanId";

    }

    /**
     * 特殊符号
     */
    interface SpecialSymbols {
        String DOT = ".";
        String ELLIPSIS = "...";
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
        String HYPHEN = "-";
        String UNDERLINE = "_";
        String CURLY_BRACES = "{}";
        String LEFT_BRACKET = "(";
        String RIGHT_BRACKET = ")";
        String ENTER_LINUX = "\n";
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

        /**
         * 文件分享信息模块 依据 user_id 平均分20个表
         */
        Integer FILE_SHARE = 20;

        /**
         * 文件分享记录详情模块 依据 share_id 平均分40个表
         */
        Integer FILE_SHARE_DETAIL = 40;
    }

    /**
     * 日期时间单位，每个单位都是以毫秒为基数
     */
    @Getter
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
        private final Long ms;

        /**
         * 秒数
         */
        private final Long s;

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
         * 默认session内容缓存的key值前缀
         */
        String DEFAULT_CACHE_REALM_PREFIX = "shiro:realm:";

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
         * 文件分享信息缓存
         */
        String FILE_SHARE_CACHE = "FILE_SHARE_CACHE:";

        /**
         * redis事件监听器类型枚举，所有通知以__keyevent@<db>__为前缀，这里的<db>可以用通配符*代替
         */
        @Getter
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
            private final String event;

            KeyEventEnum(String event) {
                this.event = event;
            }
        }

        /**
         * redis通道名称枚举(redis需要订阅的渠道名称)
         * redis 通过命令 PUBLISH channel message 来发布信息
         */
        @Getter
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
            private final String channel;

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

        String DISK_SHARE_FILE = "file_share";
    }

    /**
     * 用于创建索引时指定索引的索引存储类型
     */
    interface StoreType {
        /**
         * 默认文件系统实现。这将根据操作环境选择最佳的实现，当前所有支持的系统上都是MMapFS，但可能会发生变化。
         */
        String FS = "fs";

        /**
         * SimpleFS类型是使用随机访问文件直接实现文件系统存储（映射到Lucene SimpleFsDirectory）。
         * 此实现的并发性能较差（多线程将成为瓶颈）。当您需要索引持久性时，通常最好使用NioFs。
         */
        String SIMPLE_FS = "simplefs";

        /**
         * NIOFS类型使用NIO在文件系统上存储碎片索引（映射到Lucene NIOFSDirectory）。
         * 它允许多个线程同时读取同一文件。由于SUN Java实现中存在错误，因此不建议在Windows上使用。
         */
        String NIO_FS = "niofs";

        /**
         * MMapFS类型通过将文件映射到内存（MMap）来在文件系统上存储碎片索引（映射到Lucene MMapDirectory）。
         * 内存映射占用了进程中虚拟内存地址空间的一部分，其大小等于要映射的文件的大小。在使用这个类之前，请确保您已经允许了足够的虚拟地址空间。
         */
        String MMAP_FS = "mmapfs";
    }

    /**
     * 分词器相关常量
     */
    interface Tokenizer {

        /**
         * ik分词器
         * 最粗粒度的拆分
         * <p>比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”，适合 Phrase 查询</p>
         */
        String IK_SMART = "ik_smart";

        /**
         * ik分词器
         * 最大化会将文本做最细粒度的拆分
         * <p>比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合，适合 Term Query；</p>
         */
        String IK_MAX_WORD = "ik_max_word";
    }

    /**
     * SMS消息发送状态枚举
     */
    @Getter
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
        private final String status;

        SmsStatusEnum(String status) {
            this.status = status;
        }
    }

    /**
     * 消息记录类型枚举
     */
    @Getter
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
        private final String type;

        SmsTypeEnum(String type) {
            this.type = type;
        }
    }

    /**
     * 用户属性枚举
     */
    @Getter
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
        private final String param;

        /**
         * 用户属性参数的默认值
         */
        private final String defaultValue;

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
        @Getter
        enum ActionEnum {

            /**
             * 文件状态
             */
            FILE("0"),

            /**
             * 文件内容状态
             */
            FILE_CONTENT("1"),

            /**
             * 文件复制任务
             */
            COPY("2"),

            /**
             * 文件删除任务
             */
            DELETE("3");

            /**
             * 状态标识
             */
            private final String status;

            ActionEnum(String status) {
                this.status = status;
            }
        }

        /**
         * 文件记录-动作类型
         */
        @Getter
        enum ActionTypeEnum {

            // Action为0时对应的动作类型

            /**
             * 文件被删除
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
            TO_DELETE("1"),

            // Action为2、3时对应的动作类型

            /**
             * 任务待处理
             */
            TASK_BE_PROCESSED("0"),

            /**
             * 任务处理中
             */
            TASK_PROCESSING("1");

            /**
             * 状态标识
             */
            private final String status;

            ActionTypeEnum(String status) {
                this.status = status;
            }
        }
    }

    /**
     * oss文件当前状态枚举类型
     */
    @Getter
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

        private final String status;

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
     * 文件分享自增字段枚举
     */
    @Getter
    enum FileShareIncreaseEnum {

        /**
         * 浏览次数
         */
        BROWSE_COUNT(ReflectUtil.propertyToUnderline(FileShare::getBrowseCount)),

        /**
         * 保存、转存次数
         */
        SAVE_COUNT(ReflectUtil.propertyToUnderline(FileShare::getSaveCount)),

        /**
         * 下载次数
         */
        DOWNLOAD_COUNT(ReflectUtil.propertyToUnderline(FileShare::getSaveCount));

        /**
         * 自增字段对应的字段名称
         */
        private final String fieldName;

        FileShareIncreaseEnum(String fieldName) {
            this.fieldName = fieldName;
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
     * 文件搜索请求中指定支持排序的字段
     */
    @Getter
    enum DiskFileSearchOrderBy {

        /**
         * 业务标识
         */
        BUSINESS_ID(ReflectUtil.property(DiskFile::getBusinessId)),

        /**
         * 文件大小
         */
        FILE_SIZE(ReflectUtil.property(DiskFile::getFileSize)),

        /**
         * 创建时间
         */
        CREATE_TIME(ReflectUtil.property(DiskFile::getCreateTime)),

        /**
         * 更新时间
         */
        UPDATE_TIME(ReflectUtil.property(DiskFile::getUpdateTime));

        /**
         * 用户属性参数的默认值
         */
        private final String fieldName;

        DiskFileSearchOrderBy(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * 判断字段名是否存在于枚举中
         *
         * @param fieldName 指定的字段名
         * @return true:不存在，false:存在
         */
        public static boolean noneMatch(String fieldName) {
            return Stream.of(DiskFileSearchOrderBy.values()).noneMatch(anEnum -> anEnum.fieldName.equals(fieldName));
        }
    }

    /**
     * 排序字段
     */
    @Getter
    enum OrderDirection {
        /**
         * 正序
         */
        ASC("ASC"),

        /**
         * 倒叙
         */
        DESC("DESC");

        /**
         * 排序方向
         */
        private final String direction;

        OrderDirection(String direction) {
            this.direction = direction;
        }

        /**
         * 判断字段名是否存在于枚举中
         *
         * @param direction 指定的排序字段
         * @return true:不存在，false:存在
         */
        public static boolean noneMatch(String direction) {
            return Stream.of(OrderDirection.values()).noneMatch(anEnum -> anEnum.direction.equals(direction));
        }
    }

    /**
     * 线程池类型、使用场景相关配置
     */
    @Getter
    enum ThreadPoolEnum {
        /**
         * 文件第三方回调线程池配置，设置线程拒绝策略，丢弃队列中最旧的任务
         */
        THIRD_PARTY_CALLBACK("third-party-callback-pool", new ThreadPoolExecutor.CallerRunsPolicy()),

        /**
         * 文件任务递归线程池配置，设置线程拒绝策略，丢弃队列中最旧的任务
         */
        TASK_RECURSION_EXECUTOR("task-recursion-pool", new ThreadPoolExecutor.CallerRunsPolicy()),

        /**
         * 全局线程池配置，任意地点使用，设置线程拒绝策略，丢弃队列中最旧的任务
         */
        GLOBAL_THREAD_POOL("global-thread-pool", new ThreadPoolExecutor.CallerRunsPolicy());

        /**
         * 线程池名称
         */
        private final String name;

        /**
         * 线程阻塞（block）时的异常处理器，所谓线程阻塞即线程池和等待队列已满，无法处理线程时采取的策略
         */
        private final RejectedExecutionHandler handler;

        ThreadPoolEnum(String name, RejectedExecutionHandler handler) {
            this.name = name;
            this.handler = handler;
        }

        /**
         * 根据线程池名称获取对应的异常处理器，没有获取到时响应null
         *
         * @param name 线程池名称
         * @return 线程阻塞（block）时的异常处理器，所谓线程阻塞即线程池和等待队列已满，无法处理线程时采取的策略
         */
        public static RejectedExecutionHandler getExecutionHandler(String name) {
            return Stream.of(values())
                    .filter(anEnum -> anEnum.name.equals(name))
                    .map(anEnum -> anEnum.handler).findFirst().orElse(null);
        }
    }

    /**
     * 文件分享状态类型美枚举
     */
    @Getter
    enum FileShareStatusEnum {
        /**
         * 正常
         */
        NORMAL("0"),

        /**
         * 关闭
         */
        CLOSE("1");

        /**
         * 文件分享状态
         */
        private final String status;

        FileShareStatusEnum(String status) {
            this.status = status;
        }
    }

    /**
     * 最多一次性处理的任务数量
     */
    Long MAX_BATCH_TASKS_QUANTITY = 400L;

}
