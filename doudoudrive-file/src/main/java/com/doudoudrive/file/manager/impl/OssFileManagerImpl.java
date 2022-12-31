package com.doudoudrive.file.manager.impl;

import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.FileReviewConfig;
import com.doudoudrive.common.model.pojo.FileRecord;
import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.OssFileService;
import com.doudoudrive.file.manager.FileRecordManager;
import com.doudoudrive.file.manager.OssFileManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>OSS文件对象存储信息服务的通用业务处理层接口实现</p>
 * <p>2022-05-26 13:36</p>
 *
 * @author Dan
 **/
@Service("ossFileManager")
public class OssFileManagerImpl implements OssFileManager {

    private DiskFileConvert diskFileConvert;

    private FileRecordManager fileRecordManager;

    private OssFileService ossFileService;

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    private CacheManagerConfig cacheManagerConfig;

    @Autowired(required = false)
    public void setDiskFileConvert(DiskFileConvert diskFileConvert) {
        this.diskFileConvert = diskFileConvert;
    }

    @Autowired
    public void setFileRecordManager(FileRecordManager fileRecordManager) {
        this.fileRecordManager = fileRecordManager;
    }

    @Autowired
    public void setOssFileService(OssFileService ossFileService) {
        this.ossFileService = ossFileService;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    /**
     * 添加OSS文件对象存储
     *
     * @param createFile 创建文件时的鉴权参数模型
     * @param fileId     用户文件标识
     */
    @Override
    public void insert(CreateFileAuthModel createFile, String fileId) {
        // 构建OSS文件对象存储模块
        OssFile ossFileInfo = diskFileConvert.createFileAuthModelConvertOssFile(createFile);
        // 获取文件审核配置
        FileReviewConfig fileReviewConfig = diskDictionaryService.getDictionary(DictionaryConstant.FILE_REVIEW_CONFIG, FileReviewConfig.class);
        // 判断文件内容是否可以参加审核
        boolean isReview = fileReviewConfig.getImageTypes().contains(createFile.getFileMimeType())
                || fileReviewConfig.getVideoTypes().contains(createFile.getFileMimeType());
        if (isReview) {
            // 需要内容审核时将文件写入文件记录表中，状态标识为待审核
            FileRecord fileRecordModel = diskFileConvert.createFileAuthModelConvertFileRecord(createFile, fileId,
                    ConstantConfig.FileRecordAction.ActionEnum.FILE_CONTENT.getStatus(), ConstantConfig.FileRecordAction.ActionTypeEnum.REVIEWED.getStatus());
            // 获取指定状态的文件操作记录数据
            FileRecord record = fileRecordManager.getFileRecordByAction(createFile.getUserId(),
                    createFile.getFileEtag(), fileRecordModel.getAction(), fileRecordModel.getActionType());
            if (record == null) {
                // 记录不存在时，添加文件记录信息
                fileRecordManager.insert(fileRecordModel);
            }
            // 重置文件状态为待审核
            ossFileInfo.setStatus(ConstantConfig.OssFileStatusEnum.PENDING_REVIEW.getStatus());
        }

        synchronized (this) {
            // 根据etag查找oss文件信息
            if (this.getOssFile(createFile.getFileEtag()) == null) {
                try {
                    // 将文件存入OSS文件对象存储表中，忽略抛出的异常
                    ossFileService.insert(ossFileInfo);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 根据文件etag查询文件信息
     *
     * @param etag 文件etag
     * @return OSS文件对象存储信息
     */
    @Override
    public OssFile getOssFile(String etag) {
        // 构建的缓存key
        String cacheKey = ConstantConfig.Cache.OSS_FILE_CACHE + etag;
        // 从缓存中获取用户文件信息
        OssFile ossFile = cacheManagerConfig.getCache(cacheKey);
        if (ossFile != null) {
            return ossFile;
        }
        // 从数据库中获取OSS文件对象信息
        ossFile = ossFileService.getOssFile(etag);
        // OSS文件对象信息不为空时，将查询结果压入缓存
        Optional.ofNullable(ossFile).ifPresent(file -> cacheManagerConfig
                .putCache(cacheKey, file, ConstantConfig.Cache.DEFAULT_EXPIRE));
        return ossFile;
    }
}
