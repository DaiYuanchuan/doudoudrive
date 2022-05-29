package com.doudoudrive.file.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.FileUploadModel;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.common.util.http.UrlQueryUtil;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.MimeTypes;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
     * format类型
     */
    private static final String FORM_MIME = "application/x-www-form-urlencoded";

    /**
     * 请求拼接的url地址模板
     */
    private static final String REQUEST_URL = "https://%s";

    /**
     * 七牛云v2规范化签名(适用于大多数没有body的场景)
     */
    private static final String CANONICAL_REQUEST = "%s %s\nHost: %s\nContent-Type: %s\n\n";

    /**
     * 重命名时的URL请求路径
     */
    private static final String RENAME = "/move/%s/%s/force/%s";

    /**
     * 删除文件时的URL请求路径
     */
    private static final String DELETE = "delete/%s";

    /**
     * 批处理操作时的URL请求路径
     */
    private static final String BATCH = "/batch";

    /**
     * post请求字符串
     */
    private static final String POST = "POST";

    /**
     * 七牛云请求响应状态码
     */
    private static final String CODE = "code";

    /**
     * 批处理操作时需要的元素连接之间的分隔符
     */
    private static final String SEP = "&op=";

    /**
     * 批处理操作时需要的元素前缀字符串
     */
    private static final String PREFIX = "op=";

    /**
     * 请求异常时的最小状态码 300
     */
    private static final Integer ERROR_CODE = NumberConstant.INTEGER_THREE * NumberConstant.INTEGER_HUNDRED;

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
        return config.getAccessKey() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + encodeToString(signData);
    }

    /**
     * 生成HTTP七牛请求签名字符串(v2版本，以Qiniu为签名开头)
     *
     * @param path        请求路径
     * @param method      请求方法
     * @param contentType 内容类型
     * @param body        请求body
     * @return 签名字符串
     */
    @Override
    public String signRequestV2(String path, String method, String contentType, byte[] body) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        // 构建签名内容
        String request = String.format(CANONICAL_REQUEST, method, path, config.getRegion().getRsHost(), contentType);

        // 获取签名对象
        Mac mac = this.createMac(config.getSecretKey());
        if (mac == null) {
            return CharSequenceUtil.EMPTY;
        }

        // body 不为空时，在请求签名中加入body
        if (!CollectionUtil.isEmpty(body) && !MimeTypes.DEFAULT_MIMETYPE.equals(contentType)) {
            request += new String(body, StandardCharsets.UTF_8);
        }

        mac.update(request.getBytes(StandardCharsets.UTF_8));
        // 组装请求签名
        return config.getAccessKey() + ConstantConfig.SpecialSymbols.ENGLISH_COLON + encodeToString(mac.doFinal());
    }

    /**
     * 重命名云端文件
     *
     * @param from 旧的的文件名称(由于业务因素，这里只需要传旧文件的etag)
     * @param to   新的文件名称(由于业务因素，这里只需要传新文件的etag)
     */
    @Override
    public void rename(String from, String to) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        // 构建path的前缀(bucket:)
        final String prefix = config.getBucket() + ConstantConfig.SpecialSymbols.ENGLISH_COLON;
        // 构建请求path
        String path = String.format(RENAME, encodeToString(prefix + from), encodeToString(prefix + to), Boolean.TRUE);
        // 拼接url地址
        String url = String.format(REQUEST_URL, config.getRegion().getRsHost()) + path;
        // 获取请求签名
        String signRequest = this.signRequestV2(path, POST, FORM_MIME, null);
        // 执行请求
        try (cn.hutool.http.HttpResponse execute = HttpRequest.post(url)
                .header(ConstantConfig.HttpRequest.AUTHORIZATION, ConstantConfig.QiNiuConstant.QI_NIU_AUTHORIZATION_PREFIX + signRequest)
                .contentType(FORM_MIME)
                .timeout(NumberConstant.INTEGER_MINUS_ONE)
                .execute()) {
            // 获取请求id
            String reqId = execute.header(ConstantConfig.QiNiuConstant.QI_NIU_CALLBACK_REQUEST_ID);
            // 重命名成功时不会响应任何内容，只有失败时才会响应
            if (execute.getStatus() >= ERROR_CODE && StringUtils.isNotBlank(reqId) && StringUtils.isNotBlank(execute.body())) {
                log.error(execute.toString());
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INTERFACE_EXTERNAL_EXCEPTION);
            }
        }
    }

    /**
     * 删除云端文件
     *
     * @param keys 需要删除的文件名称(由于业务因素，这里只需要传文件的etag)
     * @return 返回所有操作成功的数据
     */
    public List<String> delete(List<String> keys) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        // 构建path的前缀(bucket:)
        final String prefix = config.getBucket() + ConstantConfig.SpecialSymbols.ENGLISH_COLON;
        // 拼接url地址
        final String url = String.format(REQUEST_URL, config.getRegion().getRsHost()) + BATCH;
        // 记录所有操作成功的记录
        List<String> success = new ArrayList<>();
        // 批处理限制单次处理量最多1000
        CollectionUtil.collectionCutting(keys, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(list -> {
            // 构建请求path
            List<String> etag = list.stream().map(key -> String.format(DELETE, encodeToString(prefix + key))).toList();
            // 构建请求body
            byte[] body = (PREFIX + String.join(SEP, etag)).getBytes(StandardCharsets.UTF_8);
            // 获取请求签名
            String signRequest = this.signRequestV2(BATCH, POST, FORM_MIME, body);
            // 执行请求
            try (cn.hutool.http.HttpResponse execute = HttpRequest.post(url)
                    .header(ConstantConfig.HttpRequest.AUTHORIZATION, ConstantConfig.QiNiuConstant.QI_NIU_AUTHORIZATION_PREFIX + signRequest)
                    .contentType(FORM_MIME)
                    .timeout(NumberConstant.INTEGER_MINUS_ONE)
                    .body(body)
                    .execute()) {
                // 这里所有请求成功时会有200、298状态码
                if (execute.getStatus() < ERROR_CODE && StringUtils.isNotBlank(execute.body())) {
                    JSONArray resultArray = JSON.parseArray(execute.body());
                    for (int i = NumberConstant.INTEGER_ZERO; i < resultArray.size(); i++) {
                        JSONObject object = resultArray.getJSONObject(i);
                        // 过滤出所有200状态码的数据
                        if (ConstantConfig.HttpStatusCode.HTTP_STATUS_CODE_200.equals(object.getInteger(CODE))) {
                            success.add(list.get(i));
                        }
                    }
                } else {
                    log.error(execute.toString());
                }
            }
        });
        return success;
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
        String digest = encodeToString(json);

        // 获取签名对象
        Mac mac = this.createMac(config.getSecretKey());
        if (mac == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SIGNATURE_EXCEPTION);
        }
        // 获取签名数据
        String encodedSign = encodeToString(mac.doFinal(digest.getBytes(StandardCharsets.UTF_8)));
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

    /**
     * 参照七牛云加密方式，URL安全的Base64编码
     *
     * @param data 编码数据
     * @return 结果字符串
     */
    private static String encodeToString(String data) {
        return encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 参照七牛云加密方式，URL安全的Base64编码
     *
     * @param data 编码数据
     * @return 结果字符串
     */
    private static String encodeToString(byte[] data) {
        return new String(Base64.getUrlEncoder().encode(data), StandardCharsets.US_ASCII);
    }
}
