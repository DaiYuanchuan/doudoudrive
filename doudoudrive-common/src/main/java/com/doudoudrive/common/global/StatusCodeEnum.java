package com.doudoudrive.common.global;

/**
 * <p>业务异常错误码枚举类</p>
 * <p>定义业务异常错误码相关的枚举信息</p>
 * <p>2022-03-02 14:18</p>
 *
 * @author Dan
 **/
public enum StatusCodeEnum {

    /**
     * 请求被成功响应
     */
    SUCCESS(200, "请求成功"),

    /**
     * 应用请求访问类:4xx
     */
    PARAM_INVALID(400, "入参无效"),
    NOT_AUTHORIZED(401, "抱歉！您暂无访问权限"),
    USER_UN_LOGIN(403, "登录过期！请重新登录"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "错误的请求方式"),
    PAYLOAD_TOO_LARGE(413, "有效负载过大"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的内容类型"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    VERIFY_CODE_NOT_EXIST(430, "验证码已过期，请重新获取"),
    VERIFY_CODE_INVALID(431, "验证码无效"),

    /**
     * 系统类:50x
     */
    SYSTEM_ERROR(500, "系统异常"),
    SYSTEM_CONFIG_ERROR(501, "配置异常"),
    ABNORMAL_MAIL_SENDING(503, "邮件发送失败"),
    INVALID_USERINFO(504, "无法正确获取用户信息"),
    PRINCIPAL_INSTANCE_EXCEPTION(505, "无法正确获取到缓存key"),
    CACHE_MANAGER_PRINCIPAL_ID_NOT_ASSIGNED_EXCEPTION(506, "CacheManager没有分配主体Id"),
    SMS_SIGNATURE_EXCEPTION(507, "短信签名异常"),
    ABNORMAL_SMS_SENDING(508, "短信发送失败"),

    /**
     * 用户(账号)类:65x
     */
    USER_NO_EXIST(650, "当前用户不存在"),
    USER_ALREADY_EXIST(651, "当前用户名称已存在"),
    ACCOUNT_NO_EXIST(652, "用户名与密码不匹配"),
    ACCOUNT_FORBIDDEN(653, "该账号已被限制登录"),
    TOO_MANY_FAILURES(654, "当前登录失败次数过多"),
    USER_EMAIL_ALREADY_EXIST(655, "用户邮箱已存在"),
    USER_EMAIL_NOT_EXIST(656, "邮箱不存在，请重新输入"),
    USER_TEL_ALREADY_EXIST(657, "用户手机号码已存在"),
    EXPIRED_CREDENTIALS(658, "用户凭证已过期"),
    AUTHENTICATION(659, "认证失败"),
    USER_TEL_NOT_EXIST(656, "用户手机号不存在，请重新输入"),
    /**
     * 接口类:8xx
     */
    INTERFACE_INTERNAL_EXCEPTION(800, "系统内部接口调用异常");

    public final Integer statusCode;
    public final String message;

    StatusCodeEnum(Integer statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
