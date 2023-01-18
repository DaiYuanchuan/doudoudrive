package com.doudoudrive.sms.manager.impl;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.MailConfig;
import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import com.doudoudrive.sms.constant.SmsConstant;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.model.convert.MailInfoConvert;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>邮件业务通用处理层接口实现</p>
 * <p>2022-04-12 17:15</p>
 *
 * @author Dan
 **/
@Service(SmsConstant.AppType.MAIL)
public class MailManagerImpl implements SmsManager {

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

    /**
     * 邮件发送
     *
     * @param model              自定义参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     * @return 消息发送结果
     */
    @Override
    public SmsSendRecord send(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel) {
        // 构建子模板名称
        model.put(SmsConstant.SUB_TEMPLATE, String.format(SmsConstant.FREEMARKER_TEMPLATE_NAME, smsSendRecordModel.getSmsDataId()));

        // 构建邮件发送记录
        SmsSendRecord mailSendRecord = new SmsSendRecord();
        mailSendRecord.setBusinessId(smsSendRecordModel.getBusinessId());
        mailSendRecord.setSmsSendTime(LocalDateTime.now());

        try {
            // 加载据模型文件，同时对指定内容进行渲染
            Template mailTemplate = configuration.getTemplate(SmsConstant.MAIL_TEMPLATE);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, model);
            MailUtil.send(getMailAccount(), smsSendRecordModel.getSmsRecipient(), smsSendRecordModel.getSmsTitle(), content, true);
            mailSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.SUCCESS.getStatus());
        } catch (Exception e) {
            mailSendRecord.setSmsStatus(ConstantConfig.SmsStatusEnum.FAIL.getStatus());
            mailSendRecord.setSmsErrorReason(e.getMessage());
            // 判断消息发送失败时的异常原因字数是否达到最大值
            if (mailSendRecord.getSmsErrorReason().length() > NumberConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE) {
                mailSendRecord.setSmsErrorReason(e.getMessage().substring(NumberConstant.INTEGER_ZERO, NumberConstant.INTEGER_TWO_HUNDRED_AND_FIFTY_FIVE));
            }
        }

        smsSendRecordService.update(mailSendRecord, smsSendRecordModel.getTableSuffix());
        return mailSendRecord;
    }

    /**
     * 邮件验证码信息发送，发送失败时会抛出业务异常
     *
     * @param securityCode       4位数随机安全码
     * @param smsSendRecordModel SMS发送记录的BO模型
     */
    @Override
    public void verificationCode(String securityCode, SmsSendRecordModel smsSendRecordModel) {
        // 构建数据模板参数
        Map<String, Object> model = Maps.newHashMapWithExpectedSize(2);
        model.put(SmsConstant.MailVerificationCode.CODE, securityCode);

        // 发送邮件
        SmsSendRecord smsSendRecord = this.send(model, smsSendRecordModel);
        if (ConstantConfig.SmsStatusEnum.FAIL.getStatus().equals(smsSendRecord.getSmsStatus())) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.ABNORMAL_MAIL_SENDING);
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
