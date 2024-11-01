package com.doudoudrive.common.constant;

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
import java.util.concurrent.TimeUnit;
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

        /**
         * 是、正确、成功状态数据.
         */
        String YES = "yes";
        /**
         * 否定、错误、失败状态数据.
         */
        String NO = "no";
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
         * 请求头中的 content-type 字段
         */
        String CONTENT_TYPE = "Content-Type";

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

        /**
         * 请求头中用于标识当前服务端接收到的请求时间
         */
        String REQUEST_TIME = "requestTime";

        /**
         * Http 协议
         */
        String HTTP_PROTOCOL = "http";

        /**
         * Https 协议
         */
        String HTTPS_PROTOCOL = "https";

        /**
         * Http GET 请求，获取资源
         */
        String GET = "GET";

        /**
         * Http HEAD 请求，获取资源的元信息
         */
        String HEAD = "HEAD";

        /**
         * Http POST 请求，向资源提交数据
         */
        String POST = "POST";

        /**
         * Http PUT 请求，向服务器端发送数据，从而改变信息
         */
        String PUT = "PUT";

        /**
         * Http PATCH 请求，请求更新部分资源
         */
        String PATCH = "PATCH";

        /**
         * Http DELETE 请求，删除资源
         */
        String DELETE = "DELETE";

        /**
         * Http OPTIONS 请求，列出可对资源实行的方法
         */
        String OPTIONS = "OPTIONS";

        /**
         * Http TRACE 请求，追踪请求 - 响应的传输路径
         */
        String TRACE = "TRACE";
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

        /**
         * 延迟消息队列服务
         */
        String DELAY_MESSAGE_QUEUE_SERVICE = "DELAY_MESSAGE_QUEUE_SERVICE";

        /**
         * CDN访问日志服务
         */
        String CDN_ACCESS_LOG_SERVICE = "CDN_ACCESS_LOG_SERVICE";
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

        /**
         * 外部回调延迟任务
         */
        String EXTERNAL_CALLBACK_TASK = "EXTERNAL_CALLBACK_TASK";

        /**
         * 订单超时延迟任务
         */
        String ORDER_TIMEOUT_TASK = "ORDER_TIMEOUT_TASK";

        /**
         * CDN访问日志服务
         */
        String CDN_ACCESS_LOG_RECORD = "CDN_ACCESS_LOG_RECORD";
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

        /**
         * 延迟消息队列服务所属消费者组
         */
        String DELAY_MESSAGE_QUEUE = "DELAY_MESSAGE_QUEUE_CONSUMER_GROUP";

        /**
         * CDN访问日志服务所属消费者组
         */
        String CDN_ACCESS_LOG_CONSUMER_GROUP = "CDN_ACCESS_LOG_CONSUMER_GROUP";
    }

    /**
     * AWS S3签名所需常量
     */
    interface AwsSigner {

        /**
         * AWS签名v4版本
         */
        String AWS4 = "AWS4";

        /**
         * HMAC-SHA256签名算法
         */
        String HMAC_SHA256 = "HMAC-SHA256";

        /**
         * 签名算法的版本。对于AWS签名v4版本来说，该值为 AWS4.
         */
        String AWS4_HMAC_SHA256 = AWS4 + "-" + HMAC_SHA256;

        /**
         * 使用的签名算法。对于AWS签名v4版本来说，该值为 AWS4-HMAC-SHA256.
         */
        String X_AMZ_ALGORITHM = "X-Amz-Algorithm";

        /**
         * 签名密钥使用的范围信息
         * <your-access-key-id>/<date>/<aws-region>/<aws-service>/aws4_request
         */
        String X_AMZ_CREDENTIAL = "X-Amz-Credential";

        /**
         * (AWS签名v4版本)安全策略的HMAC-SHA256哈希
         */
        String X_AMZ_SIGNATURE = "X-Amz-Signature";

        /**
         * 它是ISO8601格式的日期值，例如20130728T000000Z，该日期必须在创建用于签名计算的签名密钥时使用的日期相同
         */
        String X_AMZ_DATE = "X-Amz-Date";

        /**
         * 预签名 URL 有效期（以秒为单位），此值是一个整数，可以设置的最小值为 1，并且 最大值为 604800（七天）
         */
        String X_AMZ_EXPIRES = "X-Amz-Expires";

        /**
         * 列出用于计算签名的标头，以下标头是 签名计算中需要：
         * HTTP 头 Host，x-amz-*前缀的标头是 AWS 用于计算签名的标头
         */
        String X_AMZ_SIGNED_HEADERS = "X-Amz-SignedHeaders";

        /**
         * 对请求进行身份验证时，此标头提供了请求有效负载的哈希。
         * 当分块上传对象时，将该值设置为STREAMING-AWS4-HMAC-SHA256-PAYLOAD以指示签名仅包含标头并且没有有效负载
         */
        String X_AMZ_CONTENT_SHA256 = "X-Amz-Content-SHA256";

        /**
         * 请求体为空时，默认的SHA256哈希值
         */
        String EMPTY_BODY_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        /**
         * 区域代码为空时的默认值
         */
        String REGION_DEFAULT_VALUE = "us-east-1";

        /**
         * 服务名称为空时的默认值
         */
        String SERVICE_DEFAULT_VALUE = "s3";

        /**
         * 签名终结者，固定值
         * 使用密钥和数据调用哈希函数，并将签名计算结果作为密钥，而“aws4_request”作为数据
         */
        String TERMINATOR = "aws4_request";

        /**
         * 授权字符串中：访问凭证字段
         */
        String CREDENTIAL = "Credential";

        /**
         * 授权字符串中：用于签名的请求头字段
         */
        String SIGNED_HEADERS = "SignedHeaders";

        /**
         * 授权字符串中：签名字段
         */
        String SIGNATURE = "Signature";

        /**
         * 创建预签名 URL 时，并不知道有效负载的内容，因为 URL 用于上传任意负载
         * 所以这里使用 UNSIGNED-PAYLOAD 作为有效负载的哈希值
         */
        String UNSIGNED_PAYLOAD = "UNSIGNED-PAYLOAD";

        /**
         * 指示是否绕过对象存储桶中的管理保留策略
         * 管理保留策略允许存储桶所有者将对象设置为不可删除状态，防止意外或故意的删除
         */
        String X_AMZ_BYPASS_GOVERNANCE_RETENTION = "x-amz-bypass-governance-retention";

        /**
         * 通常用于请求头中的内容MD5值
         */
        String CONTENT_MD5 = "Content-MD5";

        /**
         * MD5值为空时的默认值
         */
        String EMPTY_CONTENT_MD5 = "1B2M2Y8AsgTpgAmY7PhCfg==";

        /**
         * 获取存储桶所在区域
         */
        interface GetBucketLocation {
            /**
             * 本地化区域
             */
            String LOCATION = "location";

            /**
             * 默认的区域
             */
            String DEFAULT_LOCATION = "us-east-1";
        }

        /**
         * 创建分片上传
         */
        interface CreateMultipartUpload {
            /**
             * 创建分片上传时默认参数
             */
            String UPLOADS = "uploads";
        }

        /**
         * 上传指定的分片信息
         */
        interface UploadPart {
            /**
             * 上传Id
             */
            String UPLOAD_ID = "uploadId";

            /**
             * 分片号
             */
            String PART_NUMBER = "partNumber";

            /**
             * 文件内容的Etag值
             */
            String ETAG = "ETag";
        }

        /**
         * 列出指定上传Id的所有已上传的分片信息
         */
        interface ListParts {
            /**
             * 上传Id
             */
            String UPLOAD_ID = "uploadId";

            /**
             * 设置要返回的最大分片数量
             */
            String MAX_PARTS = "max-parts";

            /**
             * 分片编号标记，指定列出应在其后开始的部分，可以用来进行分页查询
             */
            String PART_NUMBER_MARKER = "part-number-marker";
        }

        /**
         * 复制一个已存在的对象
         */
        interface CopyObject {
            /**
             * 复制
             */
            String COPY = "COPY";

            /**
             * 替换、取代
             */
            String REPLACE = "REPLACE";

            /**
             * 源对象的版本号
             */
            String VERSION_ID = "versionId";
        }

        /**
         * 上传一个对象、将对象添加到存储桶中
         */
        interface PutObject {
            /**
             * 文件内容的Etag值
             */
            String ETAG = "ETag";

            /**
             * 如果存储桶启用版本控制，会自动生成唯一的版本 ID
             */
            String VERSION_ID = "x-amz-version-id";
        }

        /**
         * 获取对象
         */
        interface GetObject {
            /**
             * 对象的版本号
             */
            String VERSION_ID = "versionId";
        }
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
                    .map(anEnum -> anEnum.status).findFirst().orElse(NumberConstant.STRING_FOUR);
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
        String LEFT_BRACE = "{";
        String RIGHT_BRACE = "}";
        String LEFT_BRACKET = "(";
        String RIGHT_BRACKET = ")";
        String ENTER_LINUX = "\n";
        String SEMICOLON = ";";
        String SLASH = "/";
        String BACKSLASH = "\\";
        String VERTICAL_LINE = "|";
        String DOUBLE_QUOTATION_MARKS = "\"";
        String SINGLE_QUOTATION_MARKS = "'";
        String PERCENT = "%";
        String DOLLAR = "$";
        String AT = "@";
        String EXCLAMATION_MARK = "!";
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
         * 文件复制专用线程池配置，设置线程拒绝策略，丢弃队列中最旧的任务
         */
        FILE_COPY_EXECUTOR("copy-file-pool", new ThreadPoolExecutor.CallerRunsPolicy()),

        /**
         * 文件删除专用线程池配置，设置线程拒绝策略，丢弃队列中最旧的任务
         */
        FILE_DELETE_EXECUTOR("delete-file-pool", new ThreadPoolExecutor.CallerRunsPolicy()),

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
     * es索引名称相关常量
     */
    interface IndexName {
        /**
         * 用户信息数据索引
         */
        String USERINFO = "userinfo";

        /**
         * 用户文件信息索引
         */
        String DISK_FILE = "disk_file";

        /**
         * 文件分享信息索引
         */
        String DISK_SHARE_FILE = "file_share";

        /**
         * 文件临时操作记录信息索引
         */
        String FILE_RECORD = "file_record";
    }

    /**
     * es搜索相关常量
     */
    interface Elasticsearch {

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
         * es搜索关键字后缀，不分词搜索
         */
        String KEYWORD = "%s.keyword";
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
            FILE_CONTENT("1");

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
             * 通常用于审核失败时的文件待删除
             */
            TO_DELETE("1");

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
        String DEFAULT_CACHE_KEY_PREFIX = "doudoudrive:shiro-cache:";

        /**
         * 默认session权限相关内容缓存的key值前缀
         */
        String DEFAULT_CACHE_REALM_PREFIX = "doudoudrive:shiro-realm:";

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
         * 文件复制时的节点缓存
         */
        String FILE_COPY_NODE_CACHE = "FILE_COPY_NODE_CACHE:";

        /**
         * redis延迟队列专用消息通道
         */
        String REDIS_DELAY_QUEUE_CHANNEL = "REDIS_DELAY_QUEUE_CHANNEL:";

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
            CHANNEL_CACHE("DOUDOU_CACHE_CHANNEL"),

            /**
             * redis分布式锁专用通道名
             */
            REDIS_LOCK_CHANNEL("REDIS_LOCK_CHANNEL");

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
     * 文件外部系统回调状态类型枚举
     */
    @Getter
    enum CallbackStatusEnum {

        /**
         * 等待
         */
        WAIT("1"),

        /**
         * 执行中
         */
        EXECUTING("2"),

        /**
         * 回调成功
         */
        SUCCESS("3"),

        /**
         * 回调失败
         */
        FAIL("4");

        /**
         * 回调状态
         */
        private final String status;

        CallbackStatusEnum(String status) {
            this.status = status;
        }
    }

    /**
     * 重试级别枚举类型，自定义的延迟级别
     */
    @Getter
    enum RetryLevelEnum {

        /**
         * 一级重试，延迟15秒执行
         */
        LEVEL_ONE(1, 15L, TimeUnit.SECONDS),

        /**
         * 二级重试，延迟5分钟执行
         */
        LEVEL_TWO(2, 5L, TimeUnit.MINUTES),

        /**
         * 三级重试，延迟30分钟执行
         */
        LEVEL_THREE(3, 30L, TimeUnit.MINUTES);

        /**
         * 重试次数
         */
        private final Integer retry;

        /**
         * 延迟时间
         */
        private final Long delay;

        /**
         * 时间单位
         */
        private final TimeUnit timeUnit;

        RetryLevelEnum(Integer retry, Long delay, TimeUnit timeUnit) {
            this.retry = retry;
            this.delay = delay;
            this.timeUnit = timeUnit;
        }

        /**
         * 根据重试次数获取对应的重试级别
         *
         * @param retry 重试次数
         * @return 重试级别枚举
         */
        public static RetryLevelEnum getLevel(Integer retry) {
            return Stream.of(values())
                    .filter(anEnum -> anEnum.retry.equals(retry)).findFirst().orElse(null);
        }
    }

    /**
     * RocketMQ消息消费状态类型美枚举
     */
    @Getter
    enum RocketmqConsumerStatusEnum {
        /**
         * 等待消费
         */
        WAIT("1"),

        /**
         * 消费中
         */
        CONSUMING("2"),

        /**
         * 完成消费
         */
        COMPLETED("3");

        /**
         * RocketMQ消费状态
         */
        private final String status;

        RocketmqConsumerStatusEnum(String status) {
            this.status = status;
        }
    }

    /**
     * 最多一次性处理的任务数量
     */
    Long MAX_BATCH_TASKS_QUANTITY = 400L;

}
