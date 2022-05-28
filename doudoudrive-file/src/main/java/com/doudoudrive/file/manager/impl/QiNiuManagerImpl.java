package com.doudoudrive.file.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.FileUploadModel;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.common.util.http.UrlQueryUtil;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.file.manager.QiNiuManager;
import com.doudoudrive.file.model.dto.response.FileUploadTokenResponseDTO;
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
import java.util.Map;

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
     * @param body        请求内容
     * @param contentType 请求类型
     * @return 签名字符串
     */
    @Override
    public String signRequest(byte[] body, String contentType) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        URI uri = URI.create(config.getCallback());
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
     * 生成七牛上传token
     *
     * @param createFileAuthModel 创建文件时的鉴权参数模型
     * @param etag                文件etag
     * @return 返回文件上传token时响应数据模型
     */
    @Override
    public FileUploadTokenResponseDTO uploadToken(CreateFileAuthModel createFileAuthModel, String etag) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);

        // 请求时间戳，取当前时间 UNIX 时间戳，精确到秒
        long timestamp = System.currentTimeMillis() / NumberConstant.LONG_ONE_THOUSAND;

        // 文件上传时对象存储中的路径
        String key = String.format(config.getPath(), etag);

        // 所支持的范围
        String scope = config.getBucket() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + key;

        // 将文件的鉴权参数模型信息转为map
        Map<String, Object> createFileAuthMap = BeanUtil.beanToMap(createFileAuthModel, Boolean.FALSE, Boolean.TRUE);
        // 通过map构建url查询字符串
        String urlQueryParam = UrlQueryUtil.buildUrlQueryParams(createFileAuthMap, Boolean.FALSE);

        // 构建文件上传模型的json字符串
        String json = JSON.toJSONString(FileUploadModel.builder()
                .callbackUrl(config.getCallback())
                .callbackBody(urlQueryParam)
                .callbackBodyType(FORM_MIME)
                .sizeLimit(config.getSize())
                .fileType(config.getFileType())
                .scope(scope)
                .deadline(timestamp + config.getExpires())
                .build());
        // 组装请求签名
        String digest = new String(Base64.getUrlEncoder().encode(json.getBytes(StandardCharsets.UTF_8)), StandardCharsets.US_ASCII);

        // 获取签名对象
        Mac mac = this.createMac(config.getSecretKey());
        if (mac == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SIGNATURE_EXCEPTION);
        }
        // 获取签名数据
        String encodedSign = new String(Base64.getUrlEncoder().encode(mac.doFinal(digest.getBytes(StandardCharsets.UTF_8))), StandardCharsets.US_ASCII);
        String token = config.getAccessKey() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + encodedSign + ConstantConfig.SpecialSymbols.ENGLISH_COLON + digest;

        // 构建响应参数
        return FileUploadTokenResponseDTO.builder()
                .token(token)
                .key(key)
                .build();
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
