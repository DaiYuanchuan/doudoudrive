package com.doudoudrive.file.manager;

import com.doudoudrive.common.model.dto.model.auth.CreateFileAuthModel;
import com.doudoudrive.common.model.pojo.OssFile;

/**
 * <p>OSS文件对象存储信息服务的通用业务处理层接口</p>
 * <p>2022-05-26 13:32</p>
 *
 * @author Dan
 **/
public interface OssFileManager {

    /**
     * 添加OSS文件对象存储
     *
     * @param createFile 创建文件时的鉴权参数模型
     * @param fileId     用户文件标识
     */
    void insert(CreateFileAuthModel createFile, String fileId);

    /**
     * 根据文件etag查询文件信息
     *
     * @param etag 文件etag
     * @return OSS文件对象存储信息
     */
    OssFile getOssFile(String etag);

}
