package com.doudoudrive.userinfo.client;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.VerifyCodeRequestDTO;
import com.doudoudrive.common.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>短信、邮件配置服务Feign调用</p>
 * <p>2022-05-08 00:54</p>
 *
 * @author Dan
 **/
@FeignClient(name = "smsServer")
public interface SmsFeignClient {

    /**
     * 邮箱验证码信息校验接口
     *
     * @param requestDTO 校验验证码请求数据模型
     * @return 响应成功表示校验通过，否则响应失败
     */
    @PostMapping(value = "/mail/verify-code", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> mailVerifyCode(@RequestBody VerifyCodeRequestDTO requestDTO);

    /**
     * 短信验证码信息校验接口
     *
     * @param requestDTO 校验验证码请求数据模型
     * @return 响应成功表示校验通过，否则响应失败
     */
    @PostMapping(value = "/sms/verify-code", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> smsVerifyCode(@RequestBody VerifyCodeRequestDTO requestDTO);

}
