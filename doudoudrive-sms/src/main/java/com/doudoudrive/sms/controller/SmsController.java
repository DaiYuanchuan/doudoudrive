package com.doudoudrive.sms.controller;

import cn.hutool.core.util.RandomUtil;
import com.doudoudrive.auth.client.UserInfoSearchFeignClient;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.SmsConfig;
import com.doudoudrive.common.model.dto.model.Throughput;
import com.doudoudrive.common.model.dto.model.ValidatedInterface;
import com.doudoudrive.common.model.dto.request.VerificationCodeRequestDTO;
import com.doudoudrive.common.model.dto.request.VerifyCodeRequestDTO;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import com.doudoudrive.sms.config.SmsFactory;
import com.doudoudrive.sms.constant.SmsConstant;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.util.SmsUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>短信发送服务控制层实现</p>
 * <p>2022-04-29 19:07</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
public class SmsController {

    /**
     * 用户信息搜索服务
     */
    private UserInfoSearchFeignClient userInfoSearchFeignClient;

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    private SmsFactory smsFactory;

    private CacheManagerConfig cacheManagerConfig;

    /**
     * SMS发送记录服务层
     */
    private SmsSendRecordService smsSendRecordService;

    @Autowired
    public void setUserInfoSearchFeignClient(UserInfoSearchFeignClient userInfoSearchFeignClient) {
        this.userInfoSearchFeignClient = userInfoSearchFeignClient;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @Autowired
    public void setSmsFactory(SmsFactory smsFactory) {
        this.smsFactory = smsFactory;
    }

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    @Autowired
    public void setSmsSendRecordService(SmsSendRecordService smsSendRecordService) {
        this.smsSendRecordService = smsSendRecordService;
    }

    @SneakyThrows
    @OpLog(title = "短信验证码", businessType = "发送")
    @PostMapping(value = "/sms/send/verification-code", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> verificationCode(@RequestBody @Validated(ValidatedInterface.Sms.class)
                                           VerificationCodeRequestDTO requestDTO, HttpServletRequest request,
                                           HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        if (requestDTO.getExist()) {
            // 根据用户邮箱查询用户信息
            Result<String> userInfoSearch = userInfoSearchFeignClient.userInfoKeyExistsSearch(null, null, requestDTO.getSmsRecipient());
            if (Result.isSuccess(userInfoSearch)) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.USER_TEL_NOT_EXIST);
            }
        }

        // 获取短信配置信息
        SmsConfig smsConfig = diskDictionaryService.getDictionary(DictionaryConstant.SMS_CONFIG, SmsConfig.class);
        // 根据配置获取通讯平台处理层接口
        SmsManager smsManager = smsFactory.getSmsManager(smsConfig.getAppType());

        // 获取sms最大吞吐量配置
        Throughput throughput = diskDictionaryService.getDictionary(DictionaryConstant.THROUGHPUT, Throughput.class);

        // 构建消息发送记录
        SmsSendRecord sendRecord = SmsSendRecord.builder()
                .smsRecipient(requestDTO.getSmsRecipient())
                .smsDataId(SmsConstant.TencentCloudSmsTemplate.VERIFICATION_CODE)
                .username(requestDTO.getUsername())
                .smsType(ConstantConfig.SmsTypeEnum.SMS.type)
                .smsStatus(ConstantConfig.SmsStatusEnum.WAIT.status)
                .build();

        // 生成6位数随机安全码
        String securityCode = RandomUtil.randomNumbers(NumberConstant.INTEGER_SIX);

        // 短信验证码信息发送
        SmsUtil.verificationCode(sendRecord, securityCode, throughput.getSms(), cacheManagerConfig, smsManager, smsSendRecordService);
        return Result.ok();
    }

    @SneakyThrows
    @OpLog(title = "短信验证码", businessType = "校验")
    @PostMapping(value = "/sms/verify-code", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> verifyCode(@RequestBody @Validated(ValidatedInterface.Sms.class)
                                     VerifyCodeRequestDTO requestDTO, HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);
        SmsUtil.verifyCode(requestDTO.getSmsRecipient(), requestDTO.getCode(), cacheManagerConfig);
        return Result.ok();
    }
}
