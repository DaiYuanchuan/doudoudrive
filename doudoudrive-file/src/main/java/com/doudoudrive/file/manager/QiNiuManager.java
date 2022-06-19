package com.doudoudrive.file.manager;

import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.file.model.dto.response.FileUploadTokenResponseDTO;

import java.util.List;

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
     * 生成HTTP七牛请求签名字符串(v2版本，以Qiniu为签名开头)
     *
     * @param path        请求路径
     * @param method      请求方法
     * @param contentType 内容类型
     * @param body        请求body
     * @return 签名字符串
     */
    String signRequestV2(String path, String method, String contentType, byte[] body);

    /**
     * 重命名云端文件
     *
     * @param from 旧的的文件名称(由于业务因素，这里只需要传旧文件的etag)
     * @param to   新的文件名称(由于业务因素，这里只需要传新文件的etag)
     */
    void rename(String from, String to);

    /**
     * 删除云端文件
     *
     * @param keys 需要删除的文件名称(由于业务因素，这里只需要传文件的etag)
     * @return 返回所有操作成功的数据
     */
    List<String> delete(List<String> keys);

    /**
     * 生成七牛上传token
     *
     * @param createFileAuthModel 创建文件时的鉴权参数模型
     * @param etag                文件etag
     * @return 返回文件上传token时响应数据模型
     */
    FileUploadTokenResponseDTO uploadToken(CreateFileAuthModel createFileAuthModel, String etag);

}
