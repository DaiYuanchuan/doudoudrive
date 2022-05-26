package com.doudoudrive.file.manager.impl;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.file.manager.QiNiuManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * <p>七牛云相关服务通用业务处理层接口实现</p>
 * <p>2022-05-25 17:14</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("qiNiuManager")
public class QiNiuManagerImpl implements QiNiuManager {

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    /**
     * 构造签名时使用到的算法
     */
    private static final String ALGORITHM = "HmacSHA1";

    /**
     * 字符 ? 的字节码
     */
    private static final Byte QUESTION_MARK = (byte) ('?');

    /**
     * 字符 \n 的字节码
     */
    private static final Byte LINE_FEED = (byte) '\n';

    /**
     * 请求的内容类型
     */
    private static final String CONTENT_TYPE = "Content-Type";

    /**
     * JSON格式类型
     */
    private static final String JSON_MIME = "application/json";

    /**
     * format类型
     */
    private static final String FORM_MIME = "application/x-www-form-urlencoded";

    /**
     * 生成HTTP七牛请求签名字符串
     *
     * @param urlString   url请求字符串
     * @param body        请求内容
     * @param contentType 请求类型
     * @return 签名字符串
     */
    @Override
    public String signRequest(String urlString, byte[] body, String contentType) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        URI uri = URI.create(urlString);
        Mac mac = this.createMac(config.getSecretKey());
        if (mac == null) {
            return CharSequenceUtil.EMPTY;
        }
        mac.update(uri.getRawPath().getBytes(StandardCharsets.UTF_8));
        if (StringUtils.isNotBlank(uri.getRawQuery())) {
            mac.update(QUESTION_MARK);
            mac.update(uri.getRawQuery().getBytes(StandardCharsets.UTF_8));
        }
        mac.update(LINE_FEED);
        if (body != null && FORM_MIME.equalsIgnoreCase(contentType)) {
            mac.update(body);
        }

        // 获取签名数据
        byte[] signData = mac.doFinal();
        // 组装请求签名
        String digest = new String(Base64.getUrlEncoder().encode(signData), StandardCharsets.US_ASCII);
        return config.getAccessKey() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + digest;
    }

    /**
     * 获取签名对象
     *
     * @param secretKey 七牛云secretKey信息
     * @return Mac签名对象
     */
    private Mac createMac(String secretKey) {
        // 构建签名
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
            mac.init(new SecretKeySpec(key, ALGORITHM));
            return mac;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
