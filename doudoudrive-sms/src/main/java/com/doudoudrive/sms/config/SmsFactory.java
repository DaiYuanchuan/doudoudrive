package com.doudoudrive.sms.config;

import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.util.lang.SpringBeanFactoryUtils;
import com.doudoudrive.sms.manager.SmsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>通讯平台工厂类</p>
 * <p>2022-05-05 00:03</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class SmsFactory implements InitializingBean {

    /**
     * 当前钩子挂载的所有插件
     */
    protected Map<String, SmsManager> plugins;


    @Override
    public void afterPropertiesSet() {
        plugins = SpringBeanFactoryUtils.getBeansOfType(SmsManager.class);
    }

    /**
     * 根据应用类型获取到对应的通讯平台业务处理层接口实例
     *
     * @param appType 应用类型常量
     * @return 通讯平台业务处理层接口实例
     */
    public SmsManager getSmsManager(String appType) {
        SmsManager smsManager = plugins.get(appType);
        if (smsManager == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_CONFIG_ERROR);
        }
        return smsManager;
    }
}
