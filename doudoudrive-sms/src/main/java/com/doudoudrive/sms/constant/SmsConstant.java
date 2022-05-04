package com.doudoudrive.sms.constant;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.util.date.DateUtils;
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
     * sms短信发送时请求成功的标志
     */
    String OK = "OK";

    /**
     * 阿里大鱼短信配置枚举信息
     */
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
        public final String param;

        /**
         * 阿里大鱼短信配置参数的值
         */
        public final String value;

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
