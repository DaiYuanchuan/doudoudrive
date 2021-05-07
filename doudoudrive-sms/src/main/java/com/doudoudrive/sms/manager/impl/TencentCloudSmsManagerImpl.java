package com.doudoudrive.sms.manager.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.RegexConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.SmsConfig;
import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.dto.model.TencentCloudSignature;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import com.doudoudrive.sms.constant.SmsConstant;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.model.dto.response.TencentCloudSendSmsStatus;
import com.doudoudrive.sms.model.dto.response.TencentCloudSmsResponseDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>腾讯云短信业务通用处理层接口实现</p>
 * <p>2022-05-05 14:58</p>
 *
 * @author Dan
 **/
@Slf4j
@Service(SmsConstant.AppType.TENCENT_CLOUD)
public class TencentCloudSmsManagerImpl implements SmsManager {

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    /**
     * SMS发送记录服务层
     */
    private SmsSendRecordService smsSendRecordService;

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @Autowired
    public void setSmsSendRecordService(SmsSendRecordService smsSendRecordService) {
        this.smsSendRecordService = smsSendRecordService;
    }

    /**
     * 请求拼接的url地址模板
     */
    private static final String REQUEST_URL = "https://%s/";

    /**
     * 计算签名时使用到的加密算法
     */
    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 加密请求参数信息时需要用到的加密算法
     */
    private static final String SHA_256 = "SHA-256";

    /**
     * 固定字符串的十六进制编码
     */
    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    /**
     * 腾讯云v3签名中拼接的规范请求串，主要由以下部分组成<br/>
     * <pre>
     *     1.HTTP 请求方法GET、POST
     *     2.URI 参数，API 3.0 固定为正斜杠 /
     *     3.发起 HTTP 请求 URL 中的查询字符串，对于 POST 请求，固定为空字符串""
     *     4.参与签名的头部信息，至少包含 host 和 content-type 两个头部
     *     5.参与签名的头部信息，说明此次请求有哪些头部参与了签名，此处为content-type;host
     *     6.请求正文(对 HTTP 请求正文做 SHA256 哈希，然后十六进制编码，最后编码串转换成小写字母)
     * </pre>
     */
    private static final String CANONICAL_REQUEST = "POST\n/\n\ncontent-type:application/json; charset=utf-8\nhost:%s\n\ncontent-type;host\n%s";

    /**
     * 固定标识，表示为腾讯云v3签名请求
     */
    private static final String TC3_REQUEST = "tc3_request";

    /**
     * 固定标识，签名方法 v3 版
     */
    private static final String TC3 = "TC3";

    /**
     * 凭证范围，格式为 Date/service/tc3_request<br/>
     * <pre>
     *     1.Date 为 UTC 标准时间的日期，取值需要和公共参数 X-TC-Timestamp 换算的 UTC 标准时间日期一致
     *     2.service 为产品名，必须与调用的产品域名一致
     *     3.终止字符串（tc3_request）
     * </pre>
     */
    private static final String CREDENTIAL_SCOPE = "%s/%s/" + TC3_REQUEST;

    /**
     * 拼接待签名字符串，主要由以下部分组成<br/>
     * <pre>
     *     1.签名算法，目前固定为 TC3-HMAC-SHA256
     *     2.请求时间戳，即请求头部的公共参数 X-TC-Timestamp 取值
     *     3.凭证范围，格式为 Date/service/tc3_request
     *     4.前述步骤拼接所得规范请求串的哈希值
     * </pre>
     */
    private static final String STRING_TO_SIGN = "TC3-HMAC-SHA256\n%d\n%s\n%s";

    /**
     * 腾讯云v3请求签名鉴权串，主要由以下部分组成<br/>
     * <pre>
     *     1.签名方法，固定为 TC3-HMAC-SHA256。
     *     2.密钥对中的 SecretId
     *     3.凭证范围
     *     4.参与签名的头部信息
     *     5.签名值
     * </pre>
     */
    private static final String AUTHORIZATION = "TC3-HMAC-SHA256 Credential=%s/%s, SignedHeaders=content-type;host, Signature=%s";

    /**
     * 需要操作的接口名称，在短信发送这里为固定值SendSms
     */
    private static final String ACTION = "SendSms";

    /**
     * 腾讯云sms短信发送时请求成功的标志
     */
    private static final String OK = "Ok";

    /**
     * 腾讯云的短信发送
     *
     * @param model              自定义模板参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     * @return 消息发送结果
     */
    @Override
    public SmsSendRecord send(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel) {
        // 获取短信配置
        SmsConfig smsConfig = diskDictionaryService.getDictionary(DictionaryConstant.SMS_CONFIG, SmsConfig.class);
        // 构建腾讯云初始化短信参数配置Map
        Map<String, Object> paramMap = Maps.newLinkedHashMapWithExpectedSize(NumberConstant.INTEGER_FIVE);
        paramMap.put(SmsConstant.TencentCloudSmsConfig.Param.PHONE_NUMBER_SET, Collections.singletonList(smsSendRecordModel.getSmsRecipient()));
        paramMap.put(SmsConstant.TencentCloudSmsConfig.Param.TEMPLATE_ID, smsSendRecordModel.getSmsDataId());
        paramMap.put(SmsConstant.TencentCloudSmsConfig.Param.TEMPLATE_PARAM_SET, model.values());
        paramMap.put(SmsConstant.TencentCloudSmsConfig.Param.SMS_SDK_APP_ID, smsConfig.getSdkAppId());
        paramMap.put(SmsConstant.TencentCloudSmsConfig.Param.SIGN_NAME, smsConfig.getSignName());

        // 获取腾讯云API签名
        TencentCloudSignature signature = this.getTencentCloudSignature(paramMap, smsConfig.getAppId(), smsConfig.getAppKey(), smsConfig.getDomain());
        if (signature == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SMS_SIGNATURE_EXCEPTION);
        }

        // 构建sms发送记录
        SmsSendRecord smsSendRecord = new SmsSendRecord();
        smsSendRecord.setSmsSendTime(new Date());
        smsSendRecord.setBusinessId(smsSendRecordModel.getBusinessId());
        smsSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.FAIL.status);

        // 构建腾讯云初始化短信请求头配置Map
        Map<String, String> header = SmsConstant.TencentCloudSmsConfig.RequestHeaderEnum.builderMap();
        header.put(SmsConstant.TencentCloudSmsConfig.RequestHeaderEnum.HOST.param, smsConfig.getDomain());
        header.put(SmsConstant.TencentCloudSmsConfig.RequestHeaderEnum.AUTHORIZATION.param, signature.getSignature());
        header.put(SmsConstant.TencentCloudSmsConfig.RequestHeaderEnum.ACTION.param, ACTION);
        header.put(SmsConstant.TencentCloudSmsConfig.RequestHeaderEnum.TIMESTAMP.param, String.valueOf(signature.getTimestamp()));

        // 请求最终构建的url,获取请求body
        try (cn.hutool.http.HttpResponse execute = HttpRequest.post(String.format(REQUEST_URL, smsConfig.getDomain()))
                .headerMap(header, Boolean.TRUE)
                .body(JSON.toJSONString(paramMap).getBytes(StandardCharsets.UTF_8))
                .timeout(NumberConstant.INTEGER_MINUS_ONE)
                .execute()) {

            // 获取腾讯云短信发送响应数据
            TencentCloudSmsResponseDTO tencentCloudSmsResponse = JSON.parseObject(execute.body(), TencentCloudSmsResponseDTO.class);
            // 获取发送状态
            TencentCloudSendSmsStatus sendStatus = tencentCloudSmsResponse.getResponse().getSendStatus().get(NumberConstant.INTEGER_ZERO);
            if (OK.equals(sendStatus.getCode())) {
                smsSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.SUCCESS.status);
            } else {
                smsSendRecord.setSmsErrorReason(execute.body());
            }
        } catch (Exception e) {
            smsSendRecord.setSmsErrorReason(e.getMessage());
        }

        // 判断消息发送失败时的异常原因字数是否达到最大值
        if (StringUtils.isNotBlank(smsSendRecord.getSmsErrorReason())
                && smsSendRecord.getSmsErrorReason().length() > NumberConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE) {
            // 字符串截断
            smsSendRecord.setSmsErrorReason(smsSendRecord.getSmsErrorReason().substring(NumberConstant.INTEGER_ZERO, NumberConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE));
        }

        // 更新消息发送记录
        smsSendRecordService.update(smsSendRecord, smsSendRecordModel.getTableSuffix());
        return smsSendRecord;
    }

    /**
     * <p>腾讯云的短信验证码信息发送，发送失败时会抛出业务异常</p>
     * <p>这里需要构建数据模板参数，同时调用发送接口</p>
     *
     * @param securityCode       4位数随机安全码
     * @param smsSendRecordModel SMS发送记录的BO模型
     */
    @Override
    public void verificationCode(String securityCode, SmsSendRecordModel smsSendRecordModel) {
        // 构建数据模板参数
        Map<String, Object> model = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_TWO);
        model.put(NumberConstant.STRING_ONE, securityCode);
        model.put(NumberConstant.STRING_TWO, NumberConstant.STRING_FIVE);

        // 发送短信
        SmsSendRecord smsSendRecord = this.send(model, smsSendRecordModel);
        if (ConstantConfig.SmsStatusEnum.FAIL.status.equals(smsSendRecord.getSmsStatus())) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.ABNORMAL_SMS_SENDING);
        }
    }

    /**
     * 生成腾讯云API请求签名信息
     *
     * @param param     参与签名构建的参数信息
     * @param secretId  腾讯云账户密钥对secretId
     * @param secretKey 腾讯云账户密钥对secretKey
     * @param domain    请求产品域名信息
     * @return 返回构建的签名、参数、时间戳
     * {
     * "signature":"构建的签名信息",
     * "timestamp":"生成签名时的时间秒数"
     * }
     */
    private TencentCloudSignature getTencentCloudSignature(Map<String, Object> param, String secretId, String secretKey, String domain) {
        try {
            // 按照规范拼接请求字符串
            String canonicalRequest = String.format(CANONICAL_REQUEST, domain, sha256Hex(JSON.toJSONString(param)));
            // 请求时间戳，取当前时间 UNIX 时间戳，精确到秒
            long timestamp = System.currentTimeMillis() / NumberConstant.LONG_ONE_THOUSAND;
            // 获取当前UTC时区的时间
            DateTime utcTime = DateTime.now().setTimeZone(TimeZone.getTimeZone(ConstantConfig.TimeZone.UTC));
            // 将UTC时间转为utc字符串
            String utc = DateUtils.format(utcTime, DatePattern.NORM_DATE_PATTERN);
            // 产品名，从当前请求产品域名中截取而来
            String service = ReUtil.getGroup0(RegexConstant.BEFORE_CHARACTER_DOT, domain);
            // 凭证范围
            String credentialScope = String.format(CREDENTIAL_SCOPE, utc, service);

            // 拼接待签名字符串
            String stringToSign = String.format(STRING_TO_SIGN, timestamp, credentialScope, sha256Hex(canonicalRequest));

            // 计算派生签名密钥
            byte[] secretDate = hmac256((TC3 + secretKey).getBytes(StandardCharsets.UTF_8), utc);
            byte[] secretService = hmac256(secretDate, service);
            byte[] secretSigning = hmac256(secretService, TC3_REQUEST);

            // 计算签名值
            String signature = printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();

            // 最终构建的签名数据
            String authorization = String.format(AUTHORIZATION, secretId, credentialScope, signature);

            // 构建腾讯云签名对象
            return TencentCloudSignature.builder()
                    .signature(authorization)
                    .timestamp(timestamp)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 计算签名时需要使用的加密方式
     *
     * @param key 关键值
     * @param msg 加密数据
     * @return 签名结果的字节码
     */
    private static byte[] hmac256(byte[] key, String msg) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
            mac.init(secretKeySpec);
            return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 请求参数进行加密时使用
     *
     * @param param 参数信息
     * @return 加密结果字符串
     */
    private static String sha256Hex(String param) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(SHA_256);
            byte[] digest = messageDigest.digest(param.getBytes(StandardCharsets.UTF_8));
            return printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将二进制字节数组转为十六进制字符串
     *
     * @param data 二进制字节编码
     * @return 十六进制字符串
     */
    public static String printHexBinary(byte[] data) {
        if (data == null) {
            return CharSequenceUtil.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder(data.length * 2);
        for (byte byteData : data) {
            stringBuilder.append(HEX_CODE[(byteData >> 4) & 0xF]);
            stringBuilder.append(HEX_CODE[(byteData & 0xF)]);
        }
        return stringBuilder.toString();
    }
}
