package com.doudoudrive.sms.manager.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.SmsConfig;
import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.dto.model.aliyun.AliYunSignature;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import com.doudoudrive.sms.constant.SmsConstant;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.model.dto.response.AliYunSmsResponseDTO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

/**
 * <p>阿里云短信业务通用处理层接口实现</p>
 * <p>2022-04-28 20:54</p>
 *
 * @author Dan
 **/
@Slf4j
@Service(SmsConstant.AppType.A_LI_YUN)
public class AliYunSmsManagerImpl implements SmsManager {

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
     * “+”的URL编码
     */
    private static final String PLUS_SIGN_ENCODER = "%20";

    /**
     * “*”的URL编码
     */
    private static final String ASTERISK_ENCODER = "%2A";

    /**
     * “~”的URL编码
     */
    private static final String TILDE_ENCODER = "%7E";

    /**
     * 构造签名时使用到的算法
     */
    private static final String ALGORITHM = "HmacSHA1";

    /**
     * Base64编码器
     */
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * 需要签名的字符串
     */
    private static final String SIGN = "GET&" + specialUrlEncode("/") + ConstantConfig.SpecialSymbols.AMPERSAND;

    /**
     * 请求拼接的url地址模板
     */
    private static final String REQUEST_URL = "https://%s/?Signature=%s%s";

    /**
     * 阿里云sms短信发送时请求成功的标志
     */
    private static final String OK = "OK";

    /**
     * 阿里大鱼的短信发送
     *
     * @param model              自定义模板参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     * @return 消息发送结果
     */
    @Override
    public SmsSendRecord send(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel) {
        // 获取短信配置
        SmsConfig smsConfig = diskDictionaryService.getDictionary(DictionaryConstant.SMS_CONFIG, SmsConfig.class);
        // 获取阿里大鱼初始化短信配置Map
        Map<String, String> aliYunSmsConfig = SmsConstant.AliYunSmsConfigEnum.builderMap();
        aliYunSmsConfig.put(SmsConstant.AliYunSmsConfigEnum.ACCESS_KEY_ID.getParam(), smsConfig.getAppId());
        aliYunSmsConfig.put(SmsConstant.AliYunSmsConfigEnum.PHONE_NUMBER.getParam(), smsSendRecordModel.getSmsRecipient());
        aliYunSmsConfig.put(SmsConstant.AliYunSmsConfigEnum.SIGN_NAME.getParam(), smsConfig.getSignName());
        aliYunSmsConfig.put(SmsConstant.AliYunSmsConfigEnum.TEMPLATE_CODE.getParam(), smsSendRecordModel.getSmsDataId());
        aliYunSmsConfig.put(SmsConstant.AliYunSmsConfigEnum.TEMPLATE_PARAM.getParam(), JSON.toJSONString(model));

        // 获取阿里云API签名
        AliYunSignature signature = this.getAliYunSignature(aliYunSmsConfig, smsConfig.getAppKey(), smsConfig.getDomain());
        if (signature == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SMS_SIGNATURE_EXCEPTION);
        }

        // 构建sms发送记录
        SmsSendRecord smsSendRecord = new SmsSendRecord();
        smsSendRecord.setSmsSendTime(LocalDateTime.now());
        smsSendRecord.setBusinessId(smsSendRecordModel.getBusinessId());
        smsSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.FAIL.getStatus());

        // 请求最终构建的url,获取请求body
        try (cn.hutool.http.HttpResponse execute = HttpRequest.get(signature.getRequestUrl())
                .timeout(NumberConstant.INTEGER_MINUS_ONE)
                .execute()) {
            // 获取阿里大鱼短信发送响应数据
            AliYunSmsResponseDTO aliYunSmsResponse = JSON.parseObject(execute.body(), AliYunSmsResponseDTO.class);
            if (OK.equals(aliYunSmsResponse.getCode())) {
                smsSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.SUCCESS.getStatus());
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
     * <p>阿里大鱼的短信验证码信息发送，发送失败时会抛出业务异常</p>
     * <p>这里需要构建数据模板参数，同时调用发送接口</p>
     *
     * @param securityCode       4位数随机安全码
     * @param smsSendRecordModel SMS发送记录的BO模型
     */
    @Override
    public void verificationCode(String securityCode, SmsSendRecordModel smsSendRecordModel) {
        // 构建数据模板参数
        Map<String, Object> model = Maps.newHashMapWithExpectedSize(NumberConstant.INTEGER_ONE);
        model.put(SmsConstant.AliYunSmsTemplate.CODE, securityCode);

        // 发送短信
        SmsSendRecord smsSendRecord = this.send(model, smsSendRecordModel);
        if (ConstantConfig.SmsStatusEnum.FAIL.getStatus().equals(smsSendRecord.getSmsStatus())) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.ABNORMAL_SMS_SENDING);
        }
    }

    /**
     * 生成阿里云API请求签名信息
     *
     * @param param           参与签名构建的参数信息
     * @param accessKeySecret 加密签名字符串和服务器端验证签名字符串的密钥
     * @param domain          产品域名
     * @return 返回构建的签名、参数、请求地址
     * {
     * "signature":"构建的签名信息",
     * "parameters":"请求参数信息",
     * "requestUrl":"最终拼接的url地址"
     * }
     */
    private AliYunSignature getAliYunSignature(Map<String, String> param, String accessKeySecret, String domain) {
        try {
            // 构造请求参数的明文字符串
            StringBuilder sortQueryStringTmp = new StringBuilder();
            param.forEach((key, value) -> sortQueryStringTmp.append(ConstantConfig.SpecialSymbols.AMPERSAND)
                    .append(specialUrlEncode(key)).append(ConstantConfig.SpecialSymbols.EQUALS).append(specialUrlEncode(value)));

            // 构建签名字符串
            String stringToSign = SIGN + specialUrlEncode(sortQueryStringTmp.substring(NumberConstant.INTEGER_ONE));
            // 构建签名
            Mac mac = Mac.getInstance(ALGORITHM);
            byte[] key = (accessKeySecret + ConstantConfig.SpecialSymbols.AMPERSAND).getBytes(StandardCharsets.UTF_8);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            // 最终构建的签名数据
            String signature = specialUrlEncode(ENCODER.encodeToString(signData));
            // 构建阿里云签名对象
            return AliYunSignature.builder()
                    .param(sortQueryStringTmp.toString())
                    .signature(signature)
                    .requestUrl(String.format(REQUEST_URL, domain, signature, sortQueryStringTmp))
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 构造待签名的请求串
     * 一个特殊的URL编码这个是POP特殊的一种规则
     * 即在一般的URLEncode后再增加三种字符替换：加号 （+）替换成 %20、星号 （*）替换成 %2A、 %7E 替换回波浪号 （~）参考代码如下
     *
     * @param value 需要构造的url值
     * @return 返回构造结果
     */
    private static String specialUrlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace(ConstantConfig.SpecialSymbols.PLUS_SIGN, PLUS_SIGN_ENCODER)
                .replace(ConstantConfig.SpecialSymbols.ASTERISK, ASTERISK_ENCODER)
                .replace(TILDE_ENCODER, ConstantConfig.SpecialSymbols.TILDE);
    }
}
