package com.doudoudrive.file.manager;

import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.file.model.dto.response.FileUploadTokenResponseDTO;

/**
 * <p>七牛云相关服务通用业务处理层接口</p>
 * <p>2022-05-25 17:00</p>
 *
 * @author Dan
 **/
public interface QiNiuManager {

    /**
     * 生成HTTP七牛请求签名字符串
     *
     * @param body        请求内容
     * @param contentType 请求类型
     * @return 签名字符串
     */
    String signRequest(byte[] body, String contentType);

    /**
     * 生成七牛上传token
     *
     * @param createFileAuthModel 创建文件时的鉴权参数模型
     * @param etag                文件etag
     * @return 返回文件上传token时响应数据模型
     */
    FileUploadTokenResponseDTO uploadToken(CreateFileAuthModel createFileAuthModel, String etag);

}
