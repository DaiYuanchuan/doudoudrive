package com.doudoudrive.sms.model.convert;

import cn.hutool.extra.mail.MailAccount;
import com.doudoudrive.common.model.dto.model.MailConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

/**
 * <p></p>
 * <p>2022-04-15 11:37</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MailInfoConvert {

    /**
     * 将MailConfig(系统邮件配置对象) 类型转换为 MailAccount(邮件账户对象)
     *
     * @param mailConfig 系统邮件配置对象
     * @return 邮件账户对象
     */
    @Mappings({
            @Mapping(target = "sslEnable", expression = "java(Boolean.TRUE)"),
            @Mapping(target = "socketFactoryClass", constant = "javax.net.ssl.SSLSocketFactory"),
            @Mapping(target = "timeout", constant = "60000L"),
            @Mapping(target = "auth", expression = "java(Boolean.TRUE)")
    })
    MailAccount mailConfigConvert(MailConfig mailConfig);

}
