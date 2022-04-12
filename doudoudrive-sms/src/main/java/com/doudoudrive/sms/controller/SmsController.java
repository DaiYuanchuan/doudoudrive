package com.doudoudrive.sms.controller;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.sms.manager.SmsManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>通讯平台服务控制层实现</p>
 * <p>2022-04-12 17:06</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/sms")
public class SmsController {

    private SmsManager smsManager;

    private NacosConfigManager nacosConfigManager;

    @Autowired
    public void setSmsManager(SmsManager smsManager) {
        this.smsManager = smsManager;
    }

    @Autowired
    public void setNacosConfigManager(NacosConfigManager nacosConfigManager) {
        this.nacosConfigManager = nacosConfigManager;
    }

    @SneakyThrows
    @GetMapping(value = "/save", produces = "application/json;charset=UTF-8")
    public Result<?> saveElasticsearchUserInfo() {
        System.out.println(nacosConfigManager.getConfigService().getServerStatus());
        System.out.println(nacosConfigManager.getConfigService().getConfig("mail.html", "DEV_GROUP", 3000));
        return Result.ok();
    }


}
