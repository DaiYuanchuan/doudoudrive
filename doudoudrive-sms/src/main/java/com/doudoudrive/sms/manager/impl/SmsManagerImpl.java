package com.doudoudrive.sms.manager.impl;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.MailConfig;
import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import com.doudoudrive.common.model.pojo.SmsSendRecord;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.SmsSendRecordService;
import com.doudoudrive.sms.constant.SmsConstant;
import com.doudoudrive.sms.manager.SmsManager;
import com.doudoudrive.sms.model.convert.MailInfoConvert;
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
     * 异常字段的最大索引值
     */
    private static final Integer MAXIMUM_INDEX = 255;

    /**
     * 邮件发送
     *
     * @param model              自定义参数
     * @param smsSendRecordModel SMS发送记录的BO模型
     */
    @Override
    public void sendMail(Map<String, Object> model, SmsSendRecordModel smsSendRecordModel) {
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
    }

    /**
     * 获取一个邮件发送对象
     *
     * @return 邮件账户对象
     */
    private MailAccount getMailAccount() {
        // 获取系统邮件配置信息
        String mailConfigStr = diskDictionaryService.getDictionary(DictionaryConstant.MAIL_CONFIG);
        MailConfig mailConfig = JSON.parseObject(mailConfigStr, MailConfig.class);
        return mailInfoConvert.mailConfigConvert(mailConfig);
    }
}
