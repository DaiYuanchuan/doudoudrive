package com.doudoudrive.sms.constant;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.util.date.DateUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>通讯平台通用常量配置</p>
 * <p>2022-04-15 19:00</p>
 *
 * @author Dan
 **/
public interface SmsConstant {

    /**
     * freemarker模板名称字符串构造
     */
    String FREEMARKER_TEMPLATE_NAME = "%s.ftl";

    /**
     * freemarker子模板参数名
     */
    String SUB_TEMPLATE = "subTemplate";

    /**
     * freemarker父模板文件名
     */
    String MAIL_TEMPLATE = "mail-template.ftl";

    /**
     * 邮箱验证码数据模板相关常量
     */
    interface MailVerificationCode {
        /**
         * 邮箱验证码模板数据标识
         */
        String MAIL_VERIFICATION_CODE = "mail-verification-code";

        /**
         * 参数:验证码
         */
        String CODE = "code";
    }

    /**
     * 阿里云短信模板相关常量
     */
    interface AliYunSmsTemplate {

        /**
         * 阿里云短信验证码场景使用的模板CODE
         */
        String VERIFICATION_CODE = "SMS_240396467";

        /**
         * 参数:验证码
         */
        String CODE = "code";

    }

    /**
     * 腾讯云短信模板相关常量
     */
    interface TencentCloudSmsTemplate {

        /**
         * 腾讯云短信验证码场景使用的模板id
         */
        String VERIFICATION_CODE = "1386619";
    }

    /**
     * 腾讯云短信配置信息
     */
    interface TencentCloudSmsConfig {

        /**
         * 请求参数
         */
        interface Param {

            /**
             * 需要发送的手机号码[单次请求最多支持200个手机号且要求全为境内手机号或全为境外手机号,格式为+[国家或地区码][手机号] 例: +8617895721475]
             */
            String PHONE_NUMBER_SET = "PhoneNumberSet";

            /**
             * 模板 ID，必须填写已审核通过的模板 ID，若向境外手机号发送短信，仅支持使用国际/港澳台短信模板。
             */
            String TEMPLATE_ID = "TemplateId";

            /**
             * 模板参数，若无模板参数，则设置为空。
             */
            String TEMPLATE_PARAM_SET = "TemplateParamSet";

            /**
             * 短信SdkAppId在 短信控制台 添加应用后生成的实际SdkAppId
             */
            String SMS_SDK_APP_ID = "SmsSdkAppId";

            /**
             * 短信签名内容，使用 UTF-8 编码，必须填写已审核通过的签名
             */
            String SIGN_NAME = "SignName";
        }

        /**
         * 请求头
         */
        @Getter
        enum RequestHeaderEnum {

            /**
             * 请求的内容类型
             */
            CONTENT_TYPE("Content-Type", "application/json; charset=utf-8"),

            /**
             * 请求HOST
             */
            HOST("Host", ""),

            /**
             * HTTP 标准身份认证头部字段
             */
            AUTHORIZATION("Authorization", ""),

            /**
             * HTTP 请求头，操作的接口名称
             */
            ACTION("X-TC-Action", ""),

            /**
             * 当前 UNIX 时间戳，可记录发起 API 请求的时间
             */
            TIMESTAMP("X-TC-Timestamp", ""),

            /**
             * 当前操作的 API 的版本
             */
            VERSION("X-TC-Version", "2021-01-11"),

            /**
             * 地域参数，用来标识希望操作哪个地域的数据
             * <pre>
             * 华北地区(北京)	ap-beijing
             * 华南地区(广州)	ap-guangzhou
             * 华东地区(南京)	ap-nanjing
             * </pre>
             */
            REGION("X-TC-Region", "ap-nanjing");

            /**
             * 腾讯云短信配置参数
             */
            private final String param;

            /**
             * 腾讯云短信配置参数的值
             */
            private final String value;

            RequestHeaderEnum(String param, String value) {
                this.param = param;
                this.value = value;
            }

            /**
             * @return 构建初始化map
             */
            public static Map<String, String> builderMap() {
                // 枚举值转换为TreeMap
                return Stream.of(values()).collect(Collectors.toMap(value -> value.param, value -> value.value, (key1, key2) -> key2, TreeMap::new));
            }
        }
    }

    /**
     * 阿里大鱼短信配置枚举信息
     */
    @Getter
    enum AliYunSmsConfigEnum {

        // ================================================= 系统参数 ===================================================

        /**
         * 阿里大鱼短信签名方式字段
         */
        SIGNATURE_METHOD("SignatureMethod", "HMAC-SHA1"),

        /**
         * 签名唯一随机数。用于防止网络重放攻击，建议每一次请求都使用不同的随机数
         */
        SIGNATURE_NONCE("SignatureNonce", ""),

        /**
         * 访问者身份
         */
        ACCESS_KEY_ID("AccessKeyId", ""),

        /**
         * 签名算法版本，这里取值1.0
         */
        SIGNATURE_VERSION("SignatureVersion", "1.0"),

        /**
         * 请求的时间戳，按照ISO8601标准表示，并需要使用UTC时间,格式为yyyy-MM-ddTHH:mm:ssZ
         */
        TIMESTAMP("Timestamp", ""),

        /**
         * 定义返回参数使用json格式
         */
        FORMAT("Format", "json"),

        // ================================================ 业务API参数 =================================================

        /**
         * 阿里大鱼发送短信时系统调用的API名称，这里取值:SendSms
         */
        ACTION("Action", "SendSms"),

        /**
         * API的版本号
         */
        VERSION("Version", "2017-05-25"),

        /**
         * API支持的电信区域代码，短信API的值为:cn-hangzhou
         */
        REGION_ID("RegionId", "cn-hangzhou"),

        /**
         * 需要发送的手机号
         */
        PHONE_NUMBER("PhoneNumbers", ""),

        /**
         * 短信签名
         */
        SIGN_NAME("SignName", ""),

        /**
         * 短信模板ID。请在控制台模板管理页面模板CODE一列查看。[例:SMS_153055065]
         */
        TEMPLATE_CODE("TemplateCode", ""),

        /**
         * 短信模板变量对应的实际值，JSON格式。[例:{"code":"1111"}]
         */
        TEMPLATE_PARAM("TemplateParam", ""),

        /**
         * 外部流水扩展字段，随意填写
         */
        OUT_ID("OutId", ""),

        /**
         * 上行短信扩展码，无特殊需要此字段的用户请忽略此字段
         */
        SMS_UP_EXTEND_CODE("SmsUpExtendCode", "");

        /**
         * 阿里大鱼短信配置参数
         */
        private final String param;

        /**
         * 阿里大鱼短信配置参数的值
         */
        private final String value;

        AliYunSmsConfigEnum(String param, String value) {
            this.param = param;
            this.value = value;
        }

        /**
         * @return 构建初始化map
         */
        public static Map<String, String> builderMap() {
            // 获取当前UTC时区的时间
            DateTime utcTime = DateTime.now().setTimeZone(TimeZone.getTimeZone(ConstantConfig.TimeZone.UTC));
            // 将UTC时间转为utc字符串
            String utc = DateUtils.format(utcTime, DatePattern.UTC_PATTERN);

            // 枚举值转换为TreeMap
            Map<String, String> initMap = Stream.of(values()).filter(config -> StringUtils.isNotBlank(config.value))
                    .collect(Collectors.toMap(value -> value.param, value -> value.value, (key1, key2) -> key2, TreeMap::new));
            initMap.put(TIMESTAMP.param, utc);
            initMap.put(SIGNATURE_NONCE.param, String.valueOf(Instant.now().toEpochMilli()));
            return initMap;
        }
    }

    /**
     * 发送消息时使用的应用类型常量
     */
    interface AppType {
        /**
         * 使用邮件发送
         */
        String MAIL = "mail";

        /**
         * 使用阿里大鱼发送
         */
        String A_LI_YUN = "aliYunSms";

        /**
         * 使用腾讯云发送
         */
        String TENCENT_CLOUD = "tencentSms";
    }
}
