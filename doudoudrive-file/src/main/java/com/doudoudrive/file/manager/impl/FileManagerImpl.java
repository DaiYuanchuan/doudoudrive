package com.doudoudrive.file.manager.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.FileReviewConfig;
import com.doudoudrive.common.model.dto.model.auth.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.auth.FileAuthModel;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.common.model.dto.request.*;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.http.UrlQueryUtil;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.constant.TransactionManagerConstant;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.DiskFileService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.file.client.DiskFileSearchFeignClient;
import com.doudoudrive.file.manager.DiskUserAttrManager;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.FileRecordManager;
import com.doudoudrive.file.manager.OssFileManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.convert.FileRecordConvert;
import com.doudoudrive.file.model.dto.request.CreateFileRollbackConsumerRequestDTO;
import com.doudoudrive.file.model.dto.response.FileSearchResponseDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private DiskDictionaryService diskDictionaryService;
    private LoginManager loginManager;
    private FileRecordManager fileRecordManager;
    private RocketMQTemplate rocketmqTemplate;
    private FileRecordConvert fileRecordConvert;
    private DiskUserAttrManager diskUserAttrManager;
    private RocketmqConsumerRecordService rocketmqConsumerRecordService;

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

    @Autowired
    public void setDiskUserAttrManager(DiskUserAttrManager diskUserAttrManager) {
        this.diskUserAttrManager = diskUserAttrManager;
    }

    @Autowired
    public void setRocketmqConsumerRecordService(RocketmqConsumerRecordService rocketmqConsumerRecordService) {
        this.rocketmqConsumerRecordService = rocketmqConsumerRecordService;
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
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.FILE_CREATE_FAILED);
        }
        // 用户文件信息先入库，然后入es
        this.saveElasticsearchDiskFile(Collections.singletonList(diskFile));
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
        Integer increase = diskUserAttrManager.increase(userFile.getUserId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY,
                ossFile.getSize(), totalDiskCapacity.stripTrailingZeros().toPlainString());
        if (increase <= NumberConstant.INTEGER_ZERO) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SPACE_INSUFFICIENT);
        }

        try {
            // 将文件存入用户文件表中，忽略抛出的异常
            Integer insert = diskFileService.insert(userFile);
            if (insert <= NumberConstant.INTEGER_ZERO) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.FILE_CREATE_FAILED);
            }
            // 删除文件记录表中指定文件的所有的状态为被删除的记录数据
            fileRecordManager.deleteAction(null, Collections.singletonList(userFile.getFileEtag()),
                    ConstantConfig.FileRecordAction.ActionEnum.FILE, ConstantConfig.FileRecordAction.ActionTypeEnum.BE_DELETED);
            // 用户文件信息先入库，然后入es
            this.saveElasticsearchDiskFile(Collections.singletonList(userFile));
            // 尝试通过token获取用户信息
            Optional.ofNullable(loginManager.getUserInfoToToken(fileInfo.getToken())).ifPresent(userInfo -> {
                // 更新已用容量
                String usedCapacity = usedDiskCapacity.add(new BigDecimal(ossFile.getSize())).stripTrailingZeros().toPlainString();
                userInfo.getUserAttr().put(ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY.getParam(), usedCapacity);
                // 尝试更新用户缓存信息
                loginManager.attemptUpdateUserSession(fileInfo.getToken(), userInfo);
            });
            return userFile;
        } catch (Exception e) {
            // 发送MQ消息，异步回滚文件和用户磁盘容量数据
            CreateFileRollbackConsumerRequestDTO copyFileConsumerRequest = CreateFileRollbackConsumerRequestDTO.builder()
                    .userId(userFile.getUserId())
                    .fileId(userFile.getBusinessId())
                    .size(ossFile.getSize())
                    .retryCount(NumberConstant.INTEGER_ZERO)
                    .build();
            // 使用RocketMQ同步模式发送消息
            MessageBuilder.syncSend(ConstantConfig.Topic.FILE_SERVICE, ConstantConfig.Tag.CREATE_FILE_ROLLBACK, copyFileConsumerRequest,
                    rocketmqTemplate, consumerRecord -> rocketmqConsumerRecordService.insert(consumerRecord));
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
            file.setUpdateTime(new Date());
            // 更新es信息
            Result<String> updateElasticsearchResult = diskFileSearchFeignClient.updateElasticsearchDiskFile(UpdateBatchElasticsearchDiskFileRequestDTO.builder()
                    .fileInfo(Collections.singletonList(diskFileConvert.diskFileConvertUpdateElasticRequest(file)))
                    .build());
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
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.FILE_TRANSACTION_MANAGER)
    public void delete(List<DiskFile> content, String userId) {
        // 所有的文件标识信息集合
        List<String> allFileIdList = new ArrayList<>();

        // 构建文件操作记录信息
        List<SaveElasticsearchFileRecordRequestDTO> fileRecordList = new ArrayList<>();

        // 当前删除的文件大小总量
        BigDecimal totalSize = BigDecimal.ZERO;

        for (DiskFile diskFile : content) {
            allFileIdList.add(diskFile.getBusinessId());
            // 判断当前文件是否为文件夹
            if (!diskFile.getFileFolder()) {
                // 构建文件操作记录信息，用于记录文件的删除操作
                fileRecordList.add(fileRecordConvert.diskFileConvertFileRecord(diskFile, userId,
                        ConstantConfig.FileRecordAction.ActionEnum.FILE.getStatus(),
                        ConstantConfig.FileRecordAction.ActionTypeEnum.BE_DELETED.getStatus()));
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
            diskUserAttrManager.deducted(userId, ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY, totalSize.stripTrailingZeros().toPlainString());
        }

        // 批量删除es中的文件信息
        for (List<String> allFileId : CollectionUtil.collectionCutting(allFileIdList, NumberConstant.LONG_ONE_THOUSAND)) {
            try {
                // 删除es中保存的用户文件信息
                Result<DeleteElasticsearchResponseDTO> deleteElasticResponse = diskFileSearchFeignClient.deleteElasticsearchDiskFile(DeleteElasticsearchDiskFileRequestDTO.builder()
                        .businessId(allFileId)
                        .build());
                if (Result.isNotSuccess(deleteElasticResponse)) {
                    BusinessExceptionUtil.throwBusinessException(deleteElasticResponse);
                }
            } catch (Exception e) {
                // 构建文件删除的消费者消息
                DeleteFileConsumerRequestDTO delFileConsumerRequest = DeleteFileConsumerRequestDTO.builder()
                        .userId(userId)
                        .businessId(allFileId)
                        .build();
                // 使用RocketMQ同步模式发送消息
                MessageBuilder.syncSend(ConstantConfig.Topic.FILE_SEARCH_SERVICE, ConstantConfig.Tag.DELETE_FILE_ES, delFileConsumerRequest,
                        rocketmqTemplate, consumerRecord -> rocketmqConsumerRecordService.insert(consumerRecord));
            }
        }
    }

    /**
     * 批量复制文件信息
     *
     * @param targetUserId      目标文件夹所属的用户标识
     * @param targetFolderId    目标文件夹的业务标识
     * @param treeStructureMap  用来保存树形结构的Map<原有的数据标识, 新数据返回的数据标识>(上一轮循环中的数据)
     * @param preCopyFileList   指定需要进行复制的文件信息
     * @param totalDiskCapacity 用户总磁盘容量
     * @return 本次循环中的用来保存树形结构的Map<原有的数据标识, 新数据返回的数据标识>
     */
    @Override
    public Map<String, String> batchCopyFile(String targetUserId, String targetFolderId, Map<String, String> treeStructureMap,
                                             List<DiskFile> preCopyFileList, String totalDiskCapacity) {
        // 本次循环中的用来保存树形结构的Map<原有的数据标识, 新数据返回的数据标识>
        Map<String, String> nodeMap = Maps.newHashMapWithExpectedSize(preCopyFileList.size());

        // 当前需要复制的文件大小总量
        BigDecimal totalSize = BigDecimal.ZERO;

        // 构建文件操作记录信息
        List<String> fileEtagList = new ArrayList<>();

        for (DiskFile fileInfo : preCopyFileList) {
            // 保存新的业务标识与原有标识之间的对应关系
            String businessId = SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE);

            // 递归复制文件时，需要将 原来的业务标识 与 新的业务标识 保存到map中
            nodeMap.put(fileInfo.getBusinessId(), businessId);

            // 重新定义文件父级标识
            fileInfo.setFileParentId(treeStructureMap.getOrDefault(fileInfo.getFileParentId(), NumberConstant.STRING_ZERO));
            // 重新设置文件的业务标识
            fileInfo.setBusinessId(businessId);
            // 重新设置文件的创建人标识
            fileInfo.setUserId(targetUserId);
            // 计算当前需要复制的的文件大小总量
            totalSize = totalSize.add(new BigDecimal(fileInfo.getFileSize()));

            // 当前etag不为空时，才需要进行文件操作记录
            if (StringUtils.isNotBlank(fileInfo.getFileEtag())) {
                fileEtagList.add(fileInfo.getFileEtag());
            }
        }

        // 原子性服务增加用户已用磁盘容量属性
        if (totalSize.compareTo(BigDecimal.ZERO) > NumberConstant.INTEGER_ZERO) {
            Integer increase = diskUserAttrManager.increase(targetUserId, ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY,
                    totalSize.stripTrailingZeros().toPlainString(), totalDiskCapacity);
            if (increase <= NumberConstant.INTEGER_ZERO) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SPACE_INSUFFICIENT);
            }
        }

        // 批量复制新增的文件数据
        diskFileService.insertBatch(preCopyFileList);

        // 删除文件记录表中指定文件的所有的状态为被删除的记录数据
        fileRecordManager.deleteAction(null, fileEtagList, ConstantConfig.FileRecordAction.ActionEnum.FILE, ConstantConfig.FileRecordAction.ActionTypeEnum.BE_DELETED);

        // 批量新增es中的文件信息
        this.saveElasticsearchDiskFile(preCopyFileList);

        // 返回本次循环中的用来保存树形结构的Map
        return nodeMap;
    }

    /**
     * 将指定的文件移动到目标文件夹
     *
     * @param businessId     需要移动的文件标识
     * @param targetFolderId 目标文件夹标识
     * @param userinfo       当前登录的用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.FILE_TRANSACTION_MANAGER)
    public void move(List<String> businessId, String targetFolderId, DiskUserModel userinfo) {
        // 根据传入的文件业务标识查找是否存在对应的文件信息
        List<DiskFile> fileIdSearchResult = this.fileIdSearch(userinfo.getBusinessId(), businessId);
        if (CollectionUtil.isEmpty(fileIdSearchResult)) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.FILE_NOT_FOUND);
        }

        // 校验目标文件夹是否存在
        this.checkTargetFolder(userinfo.getBusinessId(), targetFolderId);

        // 文件名重复校验机制，如果存在重复的文件名，则会重置原始文件名
        this.verifyRepeat(targetFolderId, userinfo.getBusinessId(), fileIdSearchResult);

        // 构建更新elastic中文件信息的参数
        List<UpdateElasticsearchDiskFileRequestDTO> updateElasticFileRequest = Lists.newArrayListWithExpectedSize(fileIdSearchResult.size());

        // 批量更新数据库中的文件父级标识
        diskFileService.updateBatch(fileIdSearchResult.stream()
                // 重新定义所有的文件父级标识
                .map(fileInfo -> diskFileConvert.diskFileConvertMoveRequest(fileInfo, targetFolderId))
                .peek(fileInfo -> updateElasticFileRequest.add(diskFileConvert.diskFileConvertUpdateElasticRequest(fileInfo)))
                .toList());

        // 更新es信息
        Result<String> updateElasticsearchResult = diskFileSearchFeignClient.updateElasticsearchDiskFile(UpdateBatchElasticsearchDiskFileRequestDTO.builder()
                .fileInfo(updateElasticFileRequest)
                .build());
        if (Result.isNotSuccess(updateElasticsearchResult)) {
            BusinessExceptionUtil.throwBusinessException(updateElasticsearchResult);
        }
    }

    /**
     * 文件信息翻页搜索
     *
     * @param queryElasticRequest 构建ES文件查询请求数据模型
     * @param authModel           文件鉴权参数模型
     * @param marker              加密的游标数据
     * @return 文件信息翻页搜索结果
     */
    @Override
    public FileSearchResponseDTO search(QueryElasticsearchDiskFileRequestDTO queryElasticRequest, FileAuthModel authModel, String marker) {
        // 解析游标数据
        queryElasticRequest.setSearchAfter(this.decryptMarker(marker));

        // 执行文件信息搜索请求
        Result<List<QueryElasticsearchDiskFileResponseDTO>> queryElasticResponse = diskFileSearchFeignClient.fileInfoSearch(queryElasticRequest);
        if (Result.isNotSuccess(queryElasticResponse)) {
            BusinessExceptionUtil.throwBusinessException(queryElasticResponse);
        }

        // 获取文件信息搜索结果，同时对文件浏览地址进行授权
        return this.accessUrl(authModel, queryElasticResponse.getData(), null);
    }

    /**
     * 获取指定文件节点下所有的子节点信息 （循环）
     *
     * @param userId   用户系统内唯一标识
     * @param parentId 文件父级标识
     * @param consumer 回调函数中返回查找到的用户文件模块数据集合
     */
    @Override
    public void getUserFileAllNode(String userId, List<String> parentId, Consumer<List<DiskFile>> consumer) {
        // 创建一个栈
        Deque<List<String>> stack = new LinkedList<>();
        stack.push(parentId);
        while (!stack.isEmpty()) {
            this.getAllFileInfo(userId, stack.pop(), queryParentIdResponse -> {
                try {
                    // 回调函数
                    consumer.accept(queryParentIdResponse);
                } catch (Exception ignored) {
                    // ignored
                }

                // 获取查询结果中的所有文件夹标识
                List<String> parentFileList = queryParentIdResponse.stream()
                        .filter(DiskFile::getFileFolder)
                        .map(DiskFile::getBusinessId).toList();
                if (CollectionUtil.isNotEmpty(parentFileList)) {
                    // 存在有文件夹时，继续循环查询
                    stack.push(parentFileList);
                }
            });
        }
    }

    /**
     * 获取指定父目录下的所有文件信息
     *
     * @param userId       用户系统内唯一标识
     * @param parentFileId 文件父级标识
     * @param consumer     回调函数中返回查找到的用户文件模块数据集合
     */
    @Override
    public void getAllFileInfo(String userId, List<String> parentFileId, Consumer<List<DiskFile>> consumer) {
        diskFileService.getAllFileInfo(userId, parentFileId, consumer);
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
     * 文件 进行 移动、复制 时，需要对其目标文件夹进行校验
     * 确保 目标文件夹 一定不为空，一定是文件夹，一定是归属于当前登录的用户
     * 目标文件夹不符合条件时会抛出业务异常
     *
     * @param userId         用户系统内唯一标识
     * @param targetFolderId 目标文件夹标识
     */
    @Override
    public void checkTargetFolder(String userId, String targetFolderId) {
        // 目标文件夹过滤掉为0的根目录
        if (StringUtils.equals(targetFolderId, NumberConstant.STRING_ZERO)) {
            return;
        }

        // 非根目录
        DiskFile targetFolder = this.getDiskFile(userId, targetFolderId);
        // 判断目标文件夹是否存在
        if (targetFolder == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.PARENT_ID_NOT_FOUND);
        }

        // 判断目标文件夹是否为文件夹
        if (!targetFolder.getFileFolder()) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.NOT_FOLDER);
        }
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
        DiskFile diskFile = diskFileService.getDiskFile(userId, parentId);
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
     * 文件名重复批量校验机制，针对同一个目录下的文件、文件夹名称重复性的批量校验
     * 如果存在重名的文件信息，则会重置原始文件名，将重置后的文件名返回，如果不存在重名的文件则原样返回数据集合
     *
     * @param parentId   文件的父级标识
     * @param userId     指定的用户标识
     * @param queryParam 指定的查询参数，包含文件名、是否为文件夹
     */
    @Override
    public void verifyRepeat(String parentId, String userId, List<DiskFile> queryParam) {
        // 批量查询指定目录下是否存在指定的文件名
        List<DiskFile> batchQueryResult = diskFileService.listRepeatFileName(parentId, userId, queryParam);

        // 将查询结果转为Map<文件名+是否为文件夹, 文件信息>
        Map<String, DiskFile> diskFileMap = batchQueryResult.stream()
                // 转为Map时规定 ，如果key值重复了 ，则使用最新的数据进行覆盖
                .collect(Collectors.toMap(entry -> entry.getFileName() + entry.getFileFolder(), Function.identity(), (key1, key2) -> key2));

        for (DiskFile sourceFile : queryParam) {
            // 需要比对的结果对象
            String comparison = sourceFile.getFileName() + sourceFile.getFileFolder();
            // 如果存在重名的文件，则重置文件名
            if (diskFileMap.containsKey(comparison)) {
                sourceFile.setFileName(this.resetFileName(sourceFile.getFileName()));
            }
        }
    }

    /**
     * 加密游标数据
     *
     * @param marker 游标数据
     * @return 加密后的游标数据
     */
    @Override
    public String encryptMarker(List<Object> marker) {
        return diskDictionaryService.encrypt(FileAuthModel.builder()
                .timestamp(System.currentTimeMillis())
                .sortValues(marker)
                .build());
    }

    /**
     * 解密游标数据，marker不存在时返回null
     *
     * @param marker 加密的游标数据
     * @return 解密后的游标数据
     */
    @Override
    public List<Object> decryptMarker(String marker) {
        if (StringUtils.isNotBlank(marker)) {
            FileAuthModel content = diskDictionaryService.decrypt(marker, FileAuthModel.class);
            if (content == null || CollectionUtil.isEmpty(content.getSortValues())) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_MARKER);
            }
            // 解析游标数据
            return content.getSortValues();
        }
        return null;
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
        return this.accessUrl(authModel, fileModel, config, reviewConfig, null);
    }

    /**
     * 获取文件访问Url
     *
     * @param authModel    文件鉴权参数
     * @param fileModel    文件模型
     * @param config       七牛云配置信息
     * @param reviewConfig 文件审核配置
     * @param consumer     生成访问Url时的回调函数，方便对文件鉴权模型进行扩展
     * @return 重新赋值后的文件模型
     */
    @Override
    public DiskFileModel accessUrl(FileAuthModel authModel, DiskFileModel fileModel,
                                   QiNiuUploadConfig config, FileReviewConfig reviewConfig,
                                   Consumer<FileAuthModel> consumer) {
        // 不对文件夹、被屏蔽的文件进行访问地址赋值
        if (fileModel.getFileFolder() == null
                || fileModel.getFileFolder()
                || fileModel.getForbidden()) {
            return fileModel;
        }

        // 对文件进行鉴权，获取鉴权签名
        authModel.setFileId(fileModel.getBusinessId());
        // 生成鉴权签名前执行回调函数
        Optional.ofNullable(consumer).ifPresent(accessUrlConsumer -> accessUrlConsumer.accept(authModel));
        String sign = diskDictionaryService.encrypt(authModel);

        // 获取文件预览、下载地址
        fileModel.setPreview(this.previewUrl(config, reviewConfig, sign, fileModel.getFileMimeType(), fileModel.getFileEtag()));
        fileModel.setDownload(this.downloadUrl(config, fileModel.getFileEtag(), sign, fileModel.getFileName()));
        return fileModel;
    }

    /**
     * 批量获取文件访问Url，同时加密下一页的游标数据
     *
     * @param authModel            文件鉴权参数
     * @param queryElasticResponse 搜索es用户文件信息时的响应数据模型
     * @param consumer             生成访问Url时的回调函数，方便对文件鉴权模型进行扩展
     * @return 重新赋值后的文件模型集合
     */
    @Override
    public FileSearchResponseDTO accessUrl(FileAuthModel authModel, List<QueryElasticsearchDiskFileResponseDTO> queryElasticResponse,
                                           Consumer<FileAuthModel> consumer) {
        // 构建文件信息翻页搜索结果
        List<DiskFileModel> content = new ArrayList<>(queryElasticResponse.size());
        FileSearchResponseDTO fileSearchResponse = FileSearchResponseDTO.builder()
                .content(content)
                .marker(StringUtils.EMPTY)
                .build();

        // 没有搜索到结果时不对内容转换
        if (CollectionUtil.isEmpty(queryElasticResponse)) {
            return fileSearchResponse;
        }

        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);
        // 获取文件审核配置
        FileReviewConfig reviewConfig = diskDictionaryService.getDictionary(DictionaryConstant.FILE_REVIEW_CONFIG, FileReviewConfig.class);

        // 循环查询结果，为文件赋值访问Url地址
        for (QueryElasticsearchDiskFileResponseDTO response : queryElasticResponse) {
            content.add(this.accessUrl(authModel, response.getContent(), config, reviewConfig, consumer));
        }

        // 获取集合最后一个元素的排序值，用作下一页的游标
        int index = queryElasticResponse.size() - NumberConstant.INTEGER_ONE;
        List<Object> sortValues = queryElasticResponse.get(index).getSortValues();
        if (CollectionUtil.isNotEmpty(sortValues)) {
            // 对游标值进行加密
            fileSearchResponse.setMarker(this.encryptMarker(sortValues));
        }
        return fileSearchResponse;
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
     * 在es中保存用户文件信息，保存失败时会写入MQ消息队列
     *
     * @param diskFile 用户文件模块实体类
     */
    private void saveElasticsearchDiskFile(List<DiskFile> diskFile) {
        CollectionUtil.collectionCutting(diskFile, NumberConstant.LONG_ONE_THOUSAND).forEach(fileList -> {
            // 文件数据类型转换
            List<SaveElasticsearchDiskFileRequestDTO> fileInfo = diskFileConvert.diskFileConvertSaveElasticsearchDiskFileRequest(fileList);
            try {
                Result<String> saveElasticsearchResult = diskFileSearchFeignClient.saveElasticsearchDiskFile(BatchSaveElasticsearchDiskFileRequestDTO.builder()
                        .fileInfo(fileInfo)
                        .build());
                if (Result.isNotSuccess(saveElasticsearchResult)) {
                    BusinessExceptionUtil.throwBusinessException(saveElasticsearchResult);
                }
            } catch (Exception e) {
                // 构建保存文件信息的消费者消息
                SaveFileConsumerRequestDTO saveFileConsumerRequest = SaveFileConsumerRequestDTO.builder()
                        .fileInfo(fileInfo)
                        .build();
                // 使用RocketMQ同步模式发送消息
                MessageBuilder.syncSend(ConstantConfig.Topic.FILE_SEARCH_SERVICE, ConstantConfig.Tag.SAVE_FILE_ES, saveFileConsumerRequest,
                        rocketmqTemplate, consumerRecord -> rocketmqConsumerRecordService.insert(consumerRecord));
            }
        });
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
