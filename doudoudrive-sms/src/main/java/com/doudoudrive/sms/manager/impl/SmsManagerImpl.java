package com.doudoudrive.sms.manager.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.MailConfig;
import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.dto.model.Throughput;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import com.doudoudrive.sms.constant.SmsConstant;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.model.convert.MailInfoConvert;
import com.doudoudrive.sms.model.dto.VerificationCodeCache;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Date;
import java.util.Map;

/**
 * <p>通讯平台通用业务处理层接口实现</p>
 * <p>2022-04-12 17:15</p>
 *
 * @author Dan
 **/
@Service("smsManager")
public class SmsManagerImpl implements SmsManager {

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    /**
     * SMS发送记录服务层
     */
    private SmsSendRecordService smsSendRecordService;

    private MailInfoConvert mailInfoConvert;

    private Configuration configuration;

    private CacheManagerConfig cacheManagerConfig;

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @Autowired
    public void setSmsSendRecordService(SmsSendRecordService smsSendRecordService) {
        this.smsSendRecordService = smsSendRecordService;
    }

    @Autowired(required = false)
    public void setMailInfoConvert(MailInfoConvert mailInfoConvert) {
        this.mailInfoConvert = mailInfoConvert;
    }

    @Autowired
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    /**
     * 异常字段的最大索引值
     */
    private static final Integer MAXIMUM_INDEX = 255;

    /**
     * 邮件发送
     *
     * @param model              自定义参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     * @return 消息发送结果
     */
    @Override
    public SmsSendRecord sendMail(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel) {
        // 构建子模板名称
        model.put(SmsConstant.SUB_TEMPLATE, String.format(SmsConstant.FREEMARKER_TEMPLATE_NAME, smsSendRecordModel.getSmsDataId()));

        // 获取SMS发送记录
        SmsSendRecord smsSendRecord = new SmsSendRecord();
        smsSendRecord.setBusinessId(smsSendRecordModel.getBusinessId());

        try {
            // 加载据模型文件，同时对指定内容进行渲染
            Template mailTemplate = configuration.getTemplate(SmsConstant.MAIL_TEMPLATE);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, model);
            MailUtil.send(getMailAccount(), smsSendRecordModel.getSmsRecipient(), smsSendRecordModel.getSmsTitle(), content, true);
            smsSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.SUCCESS.status);
            smsSendRecord.setSmsSendTime(new Date());
        } catch (Exception e) {
            smsSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.FAIL.status);
            smsSendRecord.setSmsErrorReason(e.getMessage());
            // 判断消息发送失败时的异常原因字数是否达到最大值
            if (smsSendRecord.getSmsErrorReason().length() > MAXIMUM_INDEX) {
                smsSendRecord.setSmsErrorReason(e.getMessage().substring(NumberConstant.INTEGER_ZERO, MAXIMUM_INDEX));
            }
        }
        smsSendRecordService.update(smsSendRecord, smsSendRecordModel.getTableSuffix());
        return smsSendRecord;
    }

    /**
     * 邮箱验证码信息发送
     *
     * @param email    需要发送到的收件人邮箱
     * @param username 当前操作的用户名，可以为null
     */
    @Override
    public void mailVerificationCode(String email, String username) {
        // 邮箱验证码缓存key
        String cacheKey = ConstantConfig.Cache.MAIL_VERIFICATION_CODE + email;

        // 获取缓存中验证码的值
        VerificationCodeCache cacheData = cacheManagerConfig.getCache(cacheKey);
        if (cacheData != null) {
            // 获取邮件最大吞吐量配置
            Throughput throughput = diskDictionaryService.getDictionary(DictionaryConstant.THROUGHPUT, Throughput.class);

            // 缓存的时间戳 > 当前的时间戳
            if (cacheData.getTimestamp() >= System.currentTimeMillis() || cacheData.getNumber() >= throughput.getMail()) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.TOO_MANY_REQUESTS);
            }
        }

        // 生成4位数随机安全码
        String securityCode = RandomUtil.randomStringUpper(4);
        // 构建报错消息发送记录
        SmsSendRecordModel sendRecordModel = smsSendRecordService.insert(SmsSendRecord.builder()
                .smsRecipient(email)
                .smsTitle(String.format("验证码：%s", securityCode))
                .smsDataId(SmsConstant.MailVerificationCode.MAIL_VERIFICATION_CODE)
                .username(username)
                .smsType(ConstantConfig.SmsTypeEnum.MAIL.type)
                .smsStatus(ConstantConfig.SmsStatusEnum.WAIT.status)
                .build());

        // 构建数据模板参数
        Map<String, Object> model = Maps.newHashMapWithExpectedSize(2);
        model.put(SmsConstant.MailVerificationCode.CODE, securityCode);

        // 发送邮件
        SmsSendRecord smsSendRecord = this.sendMail(model, sendRecordModel);
        if (ConstantConfig.SmsStatusEnum.FAIL.status.equals(smsSendRecord.getSmsStatus())) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.ABNORMAL_MAIL_SENDING);
        }

        // 构建验证码缓存对象
        VerificationCodeCache cache = new VerificationCodeCache();
        cache.setNumber(cache.getNumber() + NumberConstant.INTEGER_ONE);
        cache.setSecurityCode(securityCode);
        cache.setCreateTime(System.currentTimeMillis());
        // 获取当前时间偏移1分钟后的时间戳
        cache.setTimestamp(cache.getCreateTime() + ConstantConfig.DateUnit.MINUTE.ms);

        // 插入缓存，设置缓存有效期为1天
        cacheManagerConfig.putCache(cacheKey, cache, ConstantConfig.DateUnit.DAY.s);
    }

    /**
     * 校验邮箱验证码是否正确，校验失败时会抛出异常
     *
     * @param email 需要校验的收件人邮箱
     * @param code  邮箱验证码
     */
    @Override
    public void verifyCode(String email, String code) {
        // 邮箱验证码缓存key
        String cacheKey = ConstantConfig.Cache.MAIL_VERIFICATION_CODE + email;

        // 获取缓存中验证码的值
        VerificationCodeCache cacheData = cacheManagerConfig.getCache(cacheKey);
        if (cacheData == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.VERIFY_CODE_NOT_EXIST);
        }

        // 获取缓存插入时间偏移5分钟后的时间戳
        long validTime = cacheData.getCreateTime() + (NumberConstant.INTEGER_FIVE * ConstantConfig.DateUnit.MINUTE.ms);
        // 有效时间 < 当前的时间戳
        if (validTime < System.currentTimeMillis()) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.VERIFY_CODE_NOT_EXIST);
        }

        // 判断缓存中的值与给定的值是否一致
        if (!code.equalsIgnoreCase(cacheData.getSecurityCode())) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.VERIFY_CODE_INVALID);
        }
    }

    // ==================================================== private ====================================================

    /**
     * 获取一个邮件发送对象
     *
     * @return 邮件账户对象
     */
    private MailAccount getMailAccount() {
        // 获取系统邮件配置信息
        MailConfig mailConfig = diskDictionaryService.getDictionary(DictionaryConstant.MAIL_CONFIG, MailConfig.class);
        return mailInfoConvert.mailConfigConvert(mailConfig);
    }
}
