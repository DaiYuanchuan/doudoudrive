package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.OssFile;

/**
 * <p>OSS文件对象存储服务层接口</p>
 * <p>2022-05-21 00:05</p>
 *
 * @author Dan
 **/
public interface OssFileService {

    /**
     * 新增OSS文件对象存储
     *
     * @param ossFile 需要新增的OSS文件对象存储实体
     */
    void insert(OssFile ossFile);

    /**
     * 删除OSS文件对象存储
     *
     * @param etag 根据文件的ETag(资源的唯一标识)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String etag);

    /**
     * 修改OSS文件对象存储
     *
     * @param ossFile 需要进行修改的OSS文件对象存储实体
     * @return 返回修改的条数
     */
    Integer update(OssFile ossFile);

    /**
     * 查找OSS文件对象存储
     *
     * @param etag 根据文件的ETag(资源的唯一标识)查找
     * @return 返回查找到的OSS文件对象存储实体
     */
    OssFile getOssFile(String etag);

}
