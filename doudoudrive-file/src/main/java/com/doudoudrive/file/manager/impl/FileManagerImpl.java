package com.doudoudrive.file.manager.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.FileAuthModel;
import com.doudoudrive.common.model.dto.model.FileReviewConfig;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.common.model.dto.request.DeleteFileConsumerRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.FileRecord;
import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.http.UrlQueryUtil;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.constant.TransactionManagerConstant;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.DiskFileService;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.doudoudrive.file.client.DiskFileSearchFeignClient;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.FileRecordManager;
import com.doudoudrive.file.manager.OssFileManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.convert.FileRecordConvert;
import com.doudoudrive.file.model.dto.response.FileSearchResponseDTO;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * <p>用户文件信息服务的通用业务处理层接口实现</p>
 * <p>2022-05-21 17:50</p>
 *
 * @author Dan
 **/
@Slf4j
@Scope("singleton")
@Service("fileManager")
public class FileManagerImpl implements FileManager {

    private CacheManagerConfig cacheManagerConfig;

    private DiskFileService diskFileService;

    private OssFileManager ossFileManager;

    private DiskFileConvert diskFileConvert;

    private DiskFileSearchFeignClient diskFileSearchFeignClient;

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    private DiskUserAttrService diskUserAttrService;

    private LoginManager loginManager;

    private FileRecordManager fileRecordManager;

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;

    private FileRecordConvert fileRecordConvert;

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    @Autowired
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }

    @Autowired
    public void setOssFileManager(OssFileManager ossFileManager) {
        this.ossFileManager = ossFileManager;
    }

    @Autowired(required = false)
    public void setDiskFileConvert(DiskFileConvert diskFileConvert) {
        this.diskFileConvert = diskFileConvert;
    }

    @Autowired
    public void setDiskFileSearchFeignClient(DiskFileSearchFeignClient diskFileSearchFeignClient) {
        this.diskFileSearchFeignClient = diskFileSearchFeignClient;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @Autowired
    public void setDiskUserAttrService(DiskUserAttrService diskUserAttrService) {
        this.diskUserAttrService = diskUserAttrService;
    }

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Autowired
    public void setFileRecordManager(FileRecordManager fileRecordManager) {
        this.fileRecordManager = fileRecordManager;
    }

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired(required = false)
    public void setFileRecordConvert(FileRecordConvert fileRecordConvert) {
        this.fileRecordConvert = fileRecordConvert;
    }

    /**
     * 重置文件名时需要使用到的日期格式
     */
    private static final String YUMMY_HMS = "_HHmmssSSS_";

    /**
     * 文件鉴权参数字段
     */
    private static final String FILE_AUTH_PARAM = "?sign=";

    /**
     * 文件下载时指定文件名时需要拼接的参数
     */
    private static final String FILE_NAME_PARAM = "&attname=";

    /**
     * 文件名字段长度
     */
    private static final Integer FILE_NAME_LENGTH = NumberConstant.INTEGER_EIGHT * NumberConstant.INTEGER_TEN;

    /**
     * 对称加密对象本地缓存线程
     */
    private static final FastThreadLocal<SymmetricCrypto> SYMMETRIC_CRYPTO_CACHE = new FastThreadLocal<>();

    /**
     * 创建文件夹
     *
     * @param userId   用户标识
     * @param name     文件夹名称
     * @param parentId 文件父级标识
     * @return 用户文件模块信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.FILE_TRANSACTION_MANAGER)
    public DiskFile createFolder(String userId, String name, String parentId) {
        // 构建一个文件夹实体信息
        DiskFile diskFile = diskFileConvert.createFileConvert(userId, name, parentId);
        // 保存用户文件夹
        Integer insert = diskFileService.insert(diskFile);
        if (insert <= NumberConstant.INTEGER_ZERO) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }
        // 用户文件信息先入库，然后入es
        Result<String> saveElasticsearchResult = this.saveElasticsearchDiskFile(diskFile);
        if (Result.isNotSuccess(saveElasticsearchResult)) {
            BusinessExceptionUtil.throwBusinessException(saveElasticsearchResult);
        }
        return diskFile;
    }

    /**
     * 创建文件
     *
     * @param fileInfo          创建文件时的鉴权参数模型
     * @param fileId            文件标识
     * @param totalDiskCapacity 用户当前总容量
     * @param usedDiskCapacity  用户当前已经使用的磁盘容量
     * @return 返回构建入库时的用户文件模块
     */
    @Override
    public DiskFile createFile(CreateFileAuthModel fileInfo, String fileId, BigDecimal totalDiskCapacity, BigDecimal usedDiskCapacity) {
        // 根据etag查找oss文件信息
        OssFile ossFile = ossFileManager.getOssFile(fileInfo.getFileEtag());

        // 构建用户文件模块
        DiskFile userFile = diskFileConvert.createFileAuthModelConvertDiskFile(fileInfo, fileId);
        // 如果当前上传的文件是被禁止的，则在添加时直接设置为禁止访问
        userFile.setForbidden(ConstantConfig.OssFileStatusEnum.forbidden(ossFile.getStatus()));

        // 文件名重复校验机制，针对同一个目录下的文件名称重复性校验
        if (this.verifyRepeat(userFile.getFileName(), userFile.getUserId(), userFile.getFileParentId(), Boolean.FALSE)) {
            userFile.setFileName(this.resetFileName(userFile.getFileName()));
        }

        // 原子性服务增加用户已用磁盘容量属性
        Integer increase = diskUserAttrService.increase(userFile.getUserId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY,
                ossFile.getSize(), totalDiskCapacity.stripTrailingZeros().toPlainString());
        if (increase <= NumberConstant.INTEGER_ZERO) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SPACE_INSUFFICIENT);
        }

        try {
            // 将文件存入用户文件表中，忽略抛出的异常
            Integer insert = diskFileService.insert(userFile);
            if (insert <= NumberConstant.INTEGER_ZERO) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
            }
            // 删除文件记录表中指定文件的所有的状态为被删除的记录数据
            fileRecordManager.deleteAction(null, userFile.getFileEtag(), ConstantConfig.FileRecordAction.ActionEnum.FILE, ConstantConfig.FileRecordAction.ActionTypeEnum.BE_DELETED);
            // 用户文件信息先入库，然后入es
            this.saveElasticsearchDiskFile(userFile);
            // 尝试通过token获取用户信息
            Optional.ofNullable(loginManager.getUserInfoToToken(fileInfo.getToken())).ifPresent(userInfo -> {
                // 更新已用容量
                String usedCapacity = usedDiskCapacity.add(new BigDecimal(ossFile.getSize())).stripTrailingZeros().toPlainString();
                userInfo.getUserAttr().put(ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY.param, usedCapacity);
                // 尝试更新用户缓存信息
                loginManager.attemptUpdateUserSession(fileInfo.getToken(), userInfo);
            });
            return userFile;
        } catch (Exception e) {
            // 出现异常时手动减去用户已用磁盘容量
            diskUserAttrService.deducted(userFile.getUserId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY, ossFile.getSize());
            // 手动删除用户文件
            diskFileService.delete(userFile.getBusinessId(), userFile.getUserId());
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据业务标识查找指定用户下的文件信息，优先从缓存中查找
     *
     * @param userId     指定的用户标识
     * @param businessId 需要查询的文件标识
     * @return 用户文件模块信息
     */
    @Override
    public DiskFile getDiskFile(String userId, String businessId) {
        // 构建的缓存key
        String cacheKey = ConstantConfig.Cache.DISK_FILE_CACHE + businessId;
        // 从缓存中获取用户文件信息
        DiskFile diskFile = cacheManagerConfig.getCache(cacheKey);
        if (diskFile != null) {
            return diskFile;
        }
        // 从数据库中获取用户文件信息
        diskFile = diskFileService.getDiskFile(userId, businessId);
        // 用户文件信息不为空时，将查询结果压入缓存
        Optional.ofNullable(diskFile).ifPresent(file -> cacheManagerConfig
                .putCache(cacheKey, file, ConstantConfig.Cache.DEFAULT_EXPIRE));
        return diskFile;
    }

    /**
     * 重命名文件或文件夹
     *
     * @param file 需要重命名的文件或文件夹信息
     * @param name 需要更改的文件名
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.FILE_TRANSACTION_MANAGER)
    public void renameFile(DiskFile file, String name) {
        // 先修改数据库中的文件名
        Integer result = diskFileService.update(DiskFile.builder()
                .userId(file.getUserId())
                .businessId(file.getBusinessId())
                .fileName(name)
                .build());
        if (result > NumberConstant.INTEGER_ZERO) {
            file.setFileName(name);
            // 更新es信息
            Result<String> updateElasticsearchResult = diskFileSearchFeignClient.updateElasticsearchDiskFile(diskFileConvert.diskFileConvertUpdateElasticRequest(file));
            if (Result.isNotSuccess(updateElasticsearchResult)) {
                BusinessExceptionUtil.throwBusinessException(updateElasticsearchResult);
            }

            // 构建的缓存key
            String cacheKey = ConstantConfig.Cache.DISK_FILE_CACHE + file.getBusinessId();
            // 重命名成功后删除缓存中的文件信息
            cacheManagerConfig.removeCache(cacheKey);
        }
    }

    /**
     * 根据文件id批量删除文件或文件夹，如果是文件夹则删除文件夹下所有文件
     * 删除失败时会抛出异常
     *
     * @param content 需要删除的文件或文件夹信息
     * @param userId  需要删除的文件或文件夹所属用户标识
     * @param sendMsg 是否发送MQ消息，用于删除文件(true:发送，false:不发送)
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.FILE_TRANSACTION_MANAGER)
    public void delete(List<DiskFile> content, String userId, boolean sendMsg) {
        // 所有的文件标识信息集合
        List<String> allFileIdList = new ArrayList<>();

        // 其中所有的文件夹信息集合
        List<String> fileFolderList = new ArrayList<>();

        // 构建文件操作记录信息
        List<FileRecord> fileRecordList = new ArrayList<>();

        // 当前删除的文件大小总量
        BigDecimal totalSize = BigDecimal.ZERO;

        for (DiskFile diskFile : content) {
            allFileIdList.add(diskFile.getBusinessId());
            // 判断当前文件是否为文件夹
            if (diskFile.getFileFolder()) {
                fileFolderList.add(diskFile.getBusinessId());
            } else {
                // 构建文件操作记录信息，用于记录文件的删除操作
                fileRecordList.add(fileRecordConvert.diskFileConvertFileRecord(diskFile, userId,
                        ConstantConfig.FileRecordAction.ActionEnum.FILE.status,
                        ConstantConfig.FileRecordAction.ActionTypeEnum.BE_DELETED.status));
            }
            // 计算当前删除的文件大小总量
            totalSize = totalSize.add(new BigDecimal(diskFile.getFileSize()));
        }

        // 先删除最上层的所有文件数据
        diskFileService.deleteBatch(allFileIdList, userId);

        // 批量添加文件操作记录信息
        fileRecordManager.insertBatch(fileRecordList);

        // 原子性服务减去用户已用磁盘容量属性
        if (totalSize.compareTo(BigDecimal.ZERO) > NumberConstant.INTEGER_ZERO) {
            diskUserAttrService.deducted(userId, ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY, totalSize.stripTrailingZeros().toPlainString());
        }

        // 发送MQ消息，用于删除子文件夹下的所有文件
        if (sendMsg) {
            // 使用sync模式发送消息，保证消息发送成功
            String destination = ConstantConfig.Topic.FILE_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.DELETE_FILE;
            SendResult sendResult = rocketmqTemplate.syncSend(destination, ObjectUtil.serialize(DeleteFileConsumerRequestDTO.builder()
                    .userId(userId)
                    .businessId(fileFolderList)
                    .build()));
            // 判断消息是否发送成功
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                // 消息发送失败，抛出异常
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.ROCKETMQ_SEND_MESSAGE_FAILED);
            }
        }

        // 批量删除es中的文件信息
        for (List<String> allFileId : CollectionUtil.collectionCutting(allFileIdList, NumberConstant.LONG_TEN_THOUSAND)) {
            // 使用sync模式发送消息，保证消息发送成功
            String destination = ConstantConfig.Topic.FILE_SEARCH_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.DELETE_FILE_ES;
            SendResult sendResult = rocketmqTemplate.syncSend(destination, ObjectUtil.serialize(DeleteFileConsumerRequestDTO.builder()
                    .userId(userId)
                    .businessId(allFileId)
                    .build()));
            // 判断消息是否发送成功
            if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
                // 消息发送失败，抛出异常
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.ROCKETMQ_SEND_MESSAGE_FAILED);
            }
        }
    }

    /**
     * 文件信息翻页搜索
     *
     * @param queryElasticRequest 构建ES文件查询请求数据模型
     * @param marker              加密的游标数据
     * @return 文件信息翻页搜索结果
     */
    @Override
    public FileSearchResponseDTO search(QueryElasticsearchDiskFileRequestDTO queryElasticRequest, String marker) {
        // 解密游标数据
        if (StringUtils.isNotBlank(marker)) {
            FileAuthModel content = this.decrypt(marker, FileAuthModel.class);
            if (content == null || CollectionUtil.isEmpty(content.getSortValues())) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_MARKER);
            }
            // 解析游标数据
            queryElasticRequest.setSearchAfter(content.getSortValues());
        }

        // 执行文件信息搜索请求
        Result<List<QueryElasticsearchDiskFileResponseDTO>> queryElasticResponse = diskFileSearchFeignClient.fileInfoSearch(queryElasticRequest);
        if (Result.isNotSuccess(queryElasticResponse)) {
            BusinessExceptionUtil.throwBusinessException(queryElasticResponse);
        }

        // 构建文件信息翻页搜索结果
        List<DiskFileModel> content = new ArrayList<>(queryElasticResponse.getData().size());
        FileSearchResponseDTO fileSearchResponse = FileSearchResponseDTO.builder()
                .content(content)
                .marker(StringUtils.EMPTY)
                .build();

        // 没有搜索到结果时不对内容转换
        if (CollectionUtil.isEmpty(queryElasticResponse.getData())) {
            return fileSearchResponse;
        }

        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        // 获取文件审核配置
        FileReviewConfig reviewConfig = diskDictionaryService.getDictionary(DictionaryConstant.FILE_REVIEW_CONFIG, FileReviewConfig.class);
        // 构建文件鉴权信息
        FileAuthModel fileAuthModel = FileAuthModel.builder()
                .userId(queryElasticRequest.getUserId())
                .timestamp(System.currentTimeMillis())
                .build();

        // 循环查询结果，为文件赋值访问Url地址
        for (QueryElasticsearchDiskFileResponseDTO response : queryElasticResponse.getData()) {
            content.add(this.accessUrl(fileAuthModel, response.getContent(), config, reviewConfig));
        }

        // 获取集合最后一个元素的排序值，用作下一页的游标
        int index = queryElasticResponse.getData().size() - NumberConstant.INTEGER_ONE;
        List<Object> sortValues = queryElasticResponse.getData().get(index).getSortValues();
        if (CollectionUtil.isNotEmpty(sortValues)) {
            // 对游标值进行加密
            fileSearchResponse.setMarker(this.encrypt(FileAuthModel.builder()
                    .timestamp(System.currentTimeMillis())
                    .sortValues(sortValues)
                    .build()));
        }
        return fileSearchResponse;
    }

    /**
     * 获取指定文件节点下所有的子节点信息 （递归）
     *
     * @param userId   用户系统内唯一标识
     * @param parentId 文件父级标识
     * @param consumer 回调函数中返回查找到的用户文件模块数据集合
     */
    @Override
    public void getUserFileAllNode(String userId, List<String> parentId, Consumer<List<DiskFile>> consumer) {
        diskFileService.getUserFileAllNode(userId, parentId, consumer);
    }

    /**
     * 根据文件业务标识批量查询用户文件信息
     *
     * @param userId 用户系统内唯一标识
     * @param fileId 文件业务标识
     * @return 返回查找到的用户文件模块数据集合
     */
    @Override
    public List<DiskFile> fileIdSearch(String userId, List<String> fileId) {
        return diskFileService.fileIdSearch(userId, fileId);
    }

    /**
     * 文件的parentId校验机制，针对是否存在、是否与自己有关、是否为文件夹的校验
     *
     * @param userId   与parentId一致的用户id
     * @param parentId 文件的父级标识
     */
    @Override
    public void verifyParentId(String userId, String parentId) {
        // 如果parentId等于0，则不需要进行校验
        if (NumberConstant.STRING_ZERO.equals(parentId)) {
            return;
        }
        // 根据文件标识查找文件对象
        DiskFile diskFile = this.getDiskFile(userId, parentId);
        if (diskFile == null || !diskFile.getUserId().equals(userId)) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.PARENT_ID_NOT_FOUND);
        }

        if (!diskFile.getFileFolder()) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.NOT_FOLDER);
        }
    }

    /**
     * 文件名重复校验机制，针对同一个目录下的文件、文件夹名称重复性校验
     *
     * @param fileName   文件、文件夹名称(文件和文件夹重复时互不影响)
     * @param userId     指定的用户标识
     * @param parentId   文件的父级标识
     * @param fileFolder 是否为文件夹
     * @return true:目录下存在相同的文件 false:不存在相同的文件
     */
    @Override
    public Boolean verifyRepeat(String fileName, String userId, String parentId, boolean fileFolder) {
        return NumberConstant.INTEGER_ONE.equals(diskFileService.getRepeatFileName(parentId, fileName, userId, fileFolder));
    }

    /**
     * 文件鉴权参数加密
     *
     * @param object 需要鉴权的参数对象
     * @return 加密后的签名
     */
    @Override
    public String encrypt(Object object) {
        // 获取对称加密SymmetricCrypto对象
        SymmetricCrypto symmetricCrypto = this.getSymmetricCrypto();
        return symmetricCrypto.encryptBase64(JSON.toJSONString(object));
    }

    /**
     * 文件访问签名解密
     *
     * @param sign  签名
     * @param clazz 签名解密后需要转换的对象类
     * @return 解密后的对象串
     */
    @Override
    public <T> T decrypt(String sign, Class<T> clazz) {
        try {
            // 获取解密后的内容
            return JSON.parseObject(this.decrypt(sign), clazz);
        } catch (Exception e) {
            // 出现异常响应null值
            return null;
        }
    }

    /**
     * 获取对称加密SymmetricCrypto对象
     *
     * @return SymmetricCrypto对象
     */
    @Override
    public SymmetricCrypto getSymmetricCrypto() {
        // 获取本地缓存对象
        SymmetricCrypto symmetricCrypto = SYMMETRIC_CRYPTO_CACHE.get();
        if (symmetricCrypto == null) {
            // 获取全局对称加密密钥
            String cipher = diskDictionaryService.getDictionary(DictionaryConstant.CIPHER, String.class);
            symmetricCrypto = new SymmetricCrypto(SymmetricAlgorithm.AES, cipher.getBytes(StandardCharsets.UTF_8));
            SYMMETRIC_CRYPTO_CACHE.set(symmetricCrypto);
        }
        return symmetricCrypto;
    }

    /**
     * 获取文件访问Url
     *
     * @param authModel 文件鉴权参数
     * @param fileModel 文件模型
     * @return 重新赋值后的文件模型
     */
    @Override
    public DiskFileModel accessUrl(FileAuthModel authModel, DiskFileModel fileModel) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        // 获取文件审核配置
        FileReviewConfig reviewConfig = diskDictionaryService.getDictionary(DictionaryConstant.FILE_REVIEW_CONFIG, FileReviewConfig.class);
        return this.accessUrl(authModel, fileModel, config, reviewConfig);
    }

    /**
     * 批量获取文件访问Url
     *
     * @param authModel     文件鉴权参数
     * @param fileModelList 文件模型集合
     * @return 重新赋值后的文件模型集合
     */
    @Override
    public List<DiskFileModel> accessUrl(FileAuthModel authModel, List<DiskFileModel> fileModelList) {
        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        // 获取文件审核配置
        FileReviewConfig reviewConfig = diskDictionaryService.getDictionary(DictionaryConstant.FILE_REVIEW_CONFIG, FileReviewConfig.class);

        // 对文件模型集合批量赋值访问地址
        for (DiskFileModel fileModel : fileModelList) {
            this.accessUrl(authModel, fileModel, config, reviewConfig);
        }
        return fileModelList;
    }

    // ==================================================== private ====================================================

    /**
     * 重置文件名
     * 针对文件名重复的情况下，会进行文件名称的重置
     * 重置规则:
     * 1.保证文件名长度尽量不超过50个字符
     * 2.在文件名末尾(后缀之前)插入指定字符串(_HHmmssSSS_3位随机数)
     * 形成最终的文件名 （原文件名）（_HHmmssSSS_3位随机数）.后缀
     * 3.如果最终的文件名长度超过50个字符串，则适当的减少（原文件名）的长度
     * 确保最终形成的文件名长度不超过50个字符
     *
     * @param originalFile 原来的 完整的 文件名
     * @return 构建的最终的文件名
     */
    private String resetFileName(String originalFile) {
        // 获取文件的后缀索引信息
        int suffixIndex = originalFile.lastIndexOf(ConstantConfig.SpecialSymbols.DOT);
        // 获取原文件的后缀
        String suffix = suffixIndex != NumberConstant.INTEGER_MINUS_ONE ? originalFile.substring(suffixIndex) : CharSequenceUtil.EMPTY;
        // 获取原文件的文件名
        String filename = suffixIndex != NumberConstant.INTEGER_MINUS_ONE ? originalFile.substring(NumberConstant.INTEGER_ZERO, suffixIndex) : originalFile;

        // 需要进行插入的 特定的字符串
        String specificCharacter = DateUtils.format(DateUtil.date(), YUMMY_HMS) + RandomUtil.randomString(NumberConstant.INTEGER_THREE) + suffix;

        // 在文件名末尾加入指定字符串
        String finalFilename = filename + specificCharacter;
        // 如果最终生成的文件名字符串长度 > 80
        if (finalFilename.length() > FILE_NAME_LENGTH) {
            // 字符串之间的差值(求出超过指定数值的多少)
            int differenceValue = finalFilename.length() - FILE_NAME_LENGTH;
            // 将原字符串从末尾开始 共截取 $differenceValue 位
            finalFilename = StrUtil.reverse(StrUtil.reverse(filename).substring(differenceValue)) + specificCharacter;
        }
        return finalFilename;
    }

    /**
     * 获取文件访问Url
     *
     * @param authModel    文件鉴权参数
     * @param fileModel    文件模型
     * @param config       七牛云配置信息
     * @param reviewConfig 文件审核配置
     * @return 重新赋值后的文件模型
     */
    private DiskFileModel accessUrl(FileAuthModel authModel, DiskFileModel fileModel, QiNiuUploadConfig config, FileReviewConfig reviewConfig) {
        // 不对文件夹进行访问地址赋值
        if (fileModel.getFileFolder() == null || fileModel.getFileFolder()) {
            return fileModel;
        }

        // 对文件进行鉴权，获取鉴权签名
        authModel.setFileId(fileModel.getBusinessId());
        String sign = this.encrypt(authModel);

        // 获取文件预览、下载地址
        fileModel.setPreview(this.previewUrl(config, reviewConfig, sign, fileModel.getFileMimeType(), fileModel.getFileEtag()));
        fileModel.setDownload(this.downloadUrl(config, fileModel.getFileEtag(), sign, fileModel.getFileName()));
        return fileModel;
    }

    /**
     * 签名解密
     *
     * @param sign 签名
     * @return 解密后的字符串
     */
    private String decrypt(String sign) {
        // 获取对称加密SymmetricCrypto对象
        SymmetricCrypto symmetricCrypto = this.getSymmetricCrypto();
        try {
            // 获取解密后的内容
            return symmetricCrypto.decryptStr(sign, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            // 出现异常响应null值
            return null;
        }
    }

    /**
     * 在es中保存用户文件信息
     *
     * @param diskFile 用户文件模块实体类
     * @return 保存es请求结果
     */
    private Result<String> saveElasticsearchDiskFile(DiskFile diskFile) {
        // 文件数据类型转换
        SaveElasticsearchDiskFileRequestDTO requestDTO = diskFileConvert.diskFileConvertSaveElasticsearchDiskFileRequest(diskFile);
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskFile.getUserId(), ConstantConfig.TableSuffix.DISK_FILE);
        requestDTO.setTableSuffix(tableSuffix);
        // 用户文件信息先入库，然后入es
        return diskFileSearchFeignClient.saveElasticsearchDiskFile(requestDTO);
    }

    /**
     * 根据文件类型获取文件预览域名
     *
     * @param config       配置文件
     * @param reviewConfig 文件审核配置
     * @param sign         文件签名
     * @param mimeType     文件类型
     * @param etag         文件etag
     * @return 文件预览域名
     */
    private String previewUrl(QiNiuUploadConfig config, FileReviewConfig reviewConfig, String sign, String mimeType, String etag) {
        // 文件访问地址需要拼接的后缀
        String suffix = FILE_AUTH_PARAM + UrlQueryUtil.encode(sign, StandardCharsets.UTF_8, null);
        if (reviewConfig.getImageTypes().contains(mimeType)) {
            // 使用图片类型域名
            return String.format(config.getDomain().getPicture(), etag) + suffix;
        }
        if (reviewConfig.getVideoTypes().contains(mimeType)) {
            // 使用视频类型域名
            return String.format(config.getDomain().getStream(), etag) + suffix;
        }
        // 使用默认下载域名
        return String.format(config.getDomain().getDownload(), etag) + suffix;
    }

    /**
     * 根据文件类型获取文件下载域名
     *
     * @param config   配置文件
     * @param etag     文件etag
     * @param sign     文件签名
     * @param filename 文件名
     * @return 文件下载域名
     */
    private String downloadUrl(QiNiuUploadConfig config, String etag, String sign, String filename) {
        // 文件访问地址需要拼接的后缀
        String suffix = FILE_AUTH_PARAM + UrlQueryUtil.encode(sign, StandardCharsets.UTF_8, null);
        // 使用默认下载域名
        if (StringUtils.isNotBlank(filename)) {
            return String.format(config.getDomain().getDownload(), etag) + suffix + FILE_NAME_PARAM + UrlQueryUtil.encode(filename, StandardCharsets.UTF_8, null);
        }
        return String.format(config.getDomain().getDownload(), etag) + suffix;
    }
}
