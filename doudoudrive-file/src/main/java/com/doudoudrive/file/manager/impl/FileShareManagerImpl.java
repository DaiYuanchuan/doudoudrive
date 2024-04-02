package com.doudoudrive.file.manager.impl;

import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.FileNestedModel;
import com.doudoudrive.common.model.dto.model.FileShareDetailModel;
import com.doudoudrive.common.model.dto.model.FileShareModel;
import com.doudoudrive.common.model.dto.model.auth.FileAuthModel;
import com.doudoudrive.common.model.dto.request.*;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchFileShareIdResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchShareUserIdResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.model.pojo.FileShare;
import com.doudoudrive.common.model.pojo.FileShareDetail;
import com.doudoudrive.common.rocketmq.MessageBuilder;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.commonservice.constant.TransactionManagerConstant;
import com.doudoudrive.commonservice.service.DiskUserService;
import com.doudoudrive.commonservice.service.FileShareDetailService;
import com.doudoudrive.commonservice.service.FileShareService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.file.client.DiskFileSearchFeignClient;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.FileShareManager;
import com.doudoudrive.file.model.convert.FileShareConvert;
import com.doudoudrive.file.model.dto.request.CreateFileShareRequestDTO;
import com.doudoudrive.file.model.dto.request.FileCopyRequestDTO;
import com.doudoudrive.file.model.dto.request.FileShareAnonymousRequestDTO;
import com.doudoudrive.file.model.dto.request.FileShareSearchRequestDTO;
import com.doudoudrive.file.model.dto.response.CreateFileShareResponseDTO;
import com.doudoudrive.file.model.dto.response.FileSearchResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareAnonymousResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareSearchResponseDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>用户文件分享记录信息服务的通用业务处理层接口实现</p>
 * <p>2022-09-28 23:51</p>
 *
 * @author Dan
 **/
@Slf4j
@Service("fileShareManager")
public class FileShareManagerImpl implements FileShareManager {

    /**
     * 进行分享的文件名后缀-不带文件夹
     */
    private static final String SHARE_FILE_NAME_SUFFIX_FILE = "%s等%d个文件";
    /**
     * 进行分享的文件名后缀-带文件夹
     */
    private static final String SHARE_FILE_NAME_SUFFIX_FILE_FOLDER = SHARE_FILE_NAME_SUFFIX_FILE + "(夹)";
    private DiskFileSearchFeignClient diskFileSearchFeignClient;
    private FileShareConvert fileShareConvert;
    private FileShareService fileShareService;
    private FileShareDetailService fileShareDetailService;
    private DiskUserService diskUserService;
    private FileManager fileManager;
    private RocketMQTemplate rocketmqTemplate;
    private CacheManagerConfig cacheManagerConfig;
    private RocketmqConsumerRecordService rocketmqConsumerRecordService;

    @Autowired
    public void setDiskFileSearchFeignClient(DiskFileSearchFeignClient diskFileSearchFeignClient) {
        this.diskFileSearchFeignClient = diskFileSearchFeignClient;
    }

    @Autowired(required = false)
    public void setFileShareConvert(FileShareConvert fileShareConvert) {
        this.fileShareConvert = fileShareConvert;
    }

    @Autowired
    public void setFileShareService(FileShareService fileShareService) {
        this.fileShareService = fileShareService;
    }

    @Autowired
    public void setFileShareDetailService(FileShareDetailService fileShareDetailService) {
        this.fileShareDetailService = fileShareDetailService;
    }

    @Autowired
    public void setDiskUserService(DiskUserService diskUserService) {
        this.diskUserService = diskUserService;
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    @Autowired
    public void setRocketmqConsumerRecordService(RocketmqConsumerRecordService rocketmqConsumerRecordService) {
        this.rocketmqConsumerRecordService = rocketmqConsumerRecordService;
    }

    /**
     * 新增用户文件分享记录信息
     *
     * @param userId                 用户标识
     * @param createFileShareRequest 网盘文件创建分享链接时的请求数据模型
     * @param shareFileList          需要分享的文件列表
     * @return 网盘文件创建分享链接时的响应数据模型
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.FILE_SHARE_TRANSACTION_MANAGER)
    public CreateFileShareResponseDTO createShare(String userId, CreateFileShareRequestDTO createFileShareRequest, List<DiskFile> shareFileList) {

        // 判断分享的文件列表中是否包含文件夹
        boolean containFolder = shareFileList.stream().anyMatch(DiskFile::getFileFolder);

        // 获取文件列表中的第一个文件名
        String firstFileName = shareFileList.get(NumberConstant.INTEGER_ZERO).getFileName();

        // 如果文件名大于32个字符，则截取前32个字符，最后拼接上省略号
        if (firstFileName.length() > NumberConstant.INTEGER_THIRTY_TWO) {
            firstFileName = firstFileName.substring(NumberConstant.INTEGER_ZERO, NumberConstant.INTEGER_THIRTY_TWO) + ConstantConfig.SpecialSymbols.ELLIPSIS;
        }

        // 获取分享文件的文件名
        String shareTitle = this.getShareTitle(firstFileName, shareFileList.size(), containFolder);

        // 构建文件分享信息实体
        FileShare fileShare = fileShareConvert.fileShareConvert(userId, shareTitle, containFolder, shareFileList.size(), createFileShareRequest);
        Integer insert = fileShareService.insert(fileShare);
        if (insert <= NumberConstant.INTEGER_ZERO) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.FILE_SHARE_FAILED);
        }

        // 保存分享的文件列表信息到分享详情表中
        fileShareDetailService.insertBatch(this.getFileShareDetailList(userId, fileShare.getShareId(), shareFileList));

        // 保存文件分享记录信息到elastic中
        diskFileSearchFeignClient.saveElasticsearchFileShare(fileShareConvert.fileShareConvertSaveFileShare(fileShare));

        // 构建返回的文件分享记录信息
        return fileShareConvert.fileShareConvertCreateFileShareResponse(fileShare);
    }

    /**
     * 取消文件分享链接
     *
     * @param shareId  分享链接标识
     * @param userinfo 当前分享的用户信息
     * @return 删除es文件分享记录信息时的响应数据模型
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = TransactionManagerConstant.FILE_SHARE_TRANSACTION_MANAGER)
    public DeleteElasticsearchResponseDTO cancelShare(List<String> shareId, DiskUserModel userinfo) {
        // 根据分享短链接标识批量删除分享记录信息
        fileShareService.deleteBatch(shareId, userinfo.getBusinessId());
        // 根据分享短链接标识批量删除分享记录详情数据
        fileShareDetailService.delete(shareId, userinfo.getBusinessId());

        // 删除es文件分享记录信息
        Result<DeleteElasticsearchResponseDTO> deleteElasticShareResult = diskFileSearchFeignClient.cancelShare(DeleteElasticsearchFileShareRequestDTO.builder()
                .userId(userinfo.getBusinessId())
                .shareId(shareId)
                .build());
        if (Result.isNotSuccess(deleteElasticShareResult)) {
            BusinessExceptionUtil.throwBusinessException(deleteElasticShareResult);
        }

        // 返回删除es文件分享记录信息时的响应数据模型
        return deleteElasticShareResult.getData();
    }

    /**
     * 根据分享链接的唯一标识获取分享链接的详细信息，包括分享的文件列表
     *
     * @param anonymousRequest 网盘文件分享链接匿名访问时的请求数据模型
     * @return 网盘文件分享链接匿名访问时的响应数据模型
     */
    @Override
    public Result<FileShareAnonymousResponseDTO> anonymous(FileShareAnonymousRequestDTO anonymousRequest) {
        // 根据短链接标识查询分享记录信息，获取到分享记录信息
        FileShareModel content = this.getShareContent(anonymousRequest.getShareId());

        // 分享链接基础信息校验
        FileShareAnonymousResponseDTO response = this.basicInfoCheck(content, anonymousRequest.getSharePwd());

        // 是否需要更新当前链接的浏览次数
        if (anonymousRequest.getUpdateBrowseCount()) {
            this.increase(content.getShareId(), ConstantConfig.FileShareIncreaseEnum.BROWSE_COUNT, content.getUserId());
        }

        // 构建网盘文件分享记录详情信息数据列表
        return Result.ok(this.buildAnonymousResponse(anonymousRequest, response, content));
    }

    /**
     * 文件分享信息搜索
     *
     * @param fileShareSearchRequest 文件分享数据搜索请求数据模型
     * @param userinfo               当前登录的用户信息
     * @return 文件分享数据搜索响应数据模型
     */
    @Override
    public FileShareSearchResponseDTO fileShareSearch(FileShareSearchRequestDTO fileShareSearchRequest, DiskUserModel userinfo) {
        // 构建文件分享信息翻页搜索的结果
        List<FileShareModel> content = Lists.newArrayList();
        FileShareSearchResponseDTO fileShareSearchResponse = FileShareSearchResponseDTO.builder()
                .content(content)
                .marker(StringUtils.EMPTY)
                .build();

        // 根据搜索条件查询文件分享记录信息
        Result<List<QueryElasticsearchShareUserIdResponseDTO>> queryElasticShareResult = diskFileSearchFeignClient.shareUserIdSearch(QueryElasticsearchShareUserIdRequestDTO.builder()
                .userId(userinfo.getBusinessId())
                .searchAfter(fileManager.decryptMarker(fileShareSearchRequest.getMarker()))
                .count(fileShareSearchRequest.getCount())
                .sort(Collections.singletonList(fileShareSearchRequest.getSort()))
                .build());
        // 查询请求是否成功
        if (Result.isNotSuccess(queryElasticShareResult)) {
            BusinessExceptionUtil.throwBusinessException(queryElasticShareResult);
        }
        // 查询结果是否为空
        if (CollectionUtil.isEmpty(queryElasticShareResult.getData())) {
            return fileShareSearchResponse;
        }

        // 获取查询结果中文件分享的短链接标识(shareId)信息
        List<String> shareIdList = queryElasticShareResult.getData().stream()
                .map(QueryElasticsearchShareUserIdResponseDTO::getContent)
                .map(FileShareModel::getShareId)
                .filter(StringUtils::isNotBlank)
                .toList();
        // 根据短链标识从数据库中查询文件分享信息，补全分享记录信息数据模型
        List<FileShare> fileShareList = fileShareService.listFileShare(shareIdList, userinfo.getBusinessId());

        for (FileShare fileShare : fileShareList) {
            // 根据文件分享的短链接标识(shareId)查询分享的文件根目录列表
            List<FileShareDetail> fileShareDetailList = fileShareDetailService.listFileShareDetail(fileShare.getShareId());
            List<String> fileIdList = fileShareDetailList.stream().map(FileShareDetail::getFileId).toList();

            // 根据分享的文件id列表查询文件信息
            List<DiskFile> fileIdSearchResult = fileManager.fileIdSearch(userinfo.getBusinessId(), fileIdList);
            if (CollectionUtil.isNotEmpty(fileIdSearchResult)) {
                // 判断分享的文件列表中是否包含文件夹
                boolean containFolder = fileIdSearchResult.stream().anyMatch(DiskFile::getFileFolder);

                // 获取文件列表中的第一个文件名
                String firstFileName = fileIdSearchResult.get(NumberConstant.INTEGER_ZERO).getFileName();
                // 获取分享文件的文件名
                fileShare.setShareTitle(this.getShareTitle(firstFileName, fileShare.getFileCount(), containFolder));
            }

            // 过期时间不为空时判断是否在当前时间之后
            if (!fileShare.getExpired()
                    && fileShare.getExpiration() != null
                    && LocalDateTime.now().isAfter(fileShare.getExpiration())) {
                // 过期时间在当前时间之前
                fileShare.setExpired(Boolean.TRUE);
            }

            // 构建文件分享信息数据模型
            content.add(fileShareConvert.fileShareConvertFileShareModel(fileShare, null));
        }

        // 获取集合最后一个元素的排序值，用作下一页的游标
        int index = queryElasticShareResult.getData().size() - NumberConstant.INTEGER_ONE;
        List<Object> sortValues = queryElasticShareResult.getData().get(index).getSortValues();
        if (CollectionUtil.isNotEmpty(sortValues)) {
            // 对游标值进行加密
            fileShareSearchResponse.setMarker(fileManager.encryptMarker(sortValues));
        }
        return fileShareSearchResponse;
    }

    /**
     * 分享文件保存到我的
     * <pre>
     *     1.校验分享链接是否存在
     *     2.校验分享链接是否过期
     *     3.校验分享提取码是否正确
     *     4.校验分享中文件key值是否正确
     *     5.实现异步复制文件到指定文件目录下
     * </pre>
     *
     * @param fileCopyRequest 文件复制(保存到我的)时的请求数据模型
     * @param userinfo        当前登录的用户信息
     */
    @Override
    public void copy(FileCopyRequestDTO fileCopyRequest, DiskUserModel userinfo) {
        // 根据短链接标识查询分享记录信息，获取到分享记录信息
        FileShareModel content = this.getShareContent(fileCopyRequest.getShareId());

        // 分享链接基础信息校验
        this.basicInfoCheck(content, fileCopyRequest.getSharePwd());

        // 校验分享链接的key值是否正确
        if (!this.shareKeyCheck(content, fileCopyRequest.getFileInfo())) {
            // 分享链接的key值不正确
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_KEY);
        }

        // 校验目标文件夹是否存在
        fileManager.checkTargetFolder(userinfo.getBusinessId(), fileCopyRequest.getTargetFolderId());

        // 获取所有需要进行复制的源文件标识
        List<String> sourceFileId = fileCopyRequest.getFileInfo().stream().map(FileNestedModel::getFileId).distinct().toList();

        // 根据传入的文件业务标识查找是否存在对应的文件信息
        List<DiskFile> fileIdSearchResult = fileManager.fileIdSearch(content.getUserId(), sourceFileId);

        // 判断文件信息搜索请求响应结果是否为空
        if (CollectionUtil.isEmpty(fileIdSearchResult)) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.FILE_NOT_FOUND);
        }

        // 文件名重复校验机制，如果存在重复的文件名，则会重置原始文件名
        fileManager.verifyRepeat(fileCopyRequest.getTargetFolderId(), userinfo.getBusinessId(), fileIdSearchResult);

        // 用来保存树形结构的Map<原有的数据标识, 新数据返回的数据标识>
        Map<String, String> treeStructureMap = Maps.newHashMapWithExpectedSize(fileIdSearchResult.size());

        // 筛选出其中所有的文件夹数据
        List<String> fileFolderList = Lists.newArrayListWithExpectedSize(fileIdSearchResult.size());

        for (DiskFile fileInfo : fileIdSearchResult) {
            // 首次复制文件时，需要将 文件父级标识 与 目标文件夹标识 保存到map中
            treeStructureMap.put(fileInfo.getFileParentId(), fileCopyRequest.getTargetFolderId());
            if (fileInfo.getFileFolder()) {
                fileFolderList.add(fileInfo.getBusinessId());
            }
        }

        // 保存、转存次数自增
        this.increase(content.getShareId(), ConstantConfig.FileShareIncreaseEnum.SAVE_COUNT, content.getUserId());

        // 用户总磁盘容量
        String totalDiskCapacity = userinfo.getUserAttr().getOrDefault(ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY.getParam(), ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY.getDefaultValue());

        // 批量复制文件信息
        Map<String, String> nodeMap = fileManager.batchCopyFile(userinfo.getBusinessId(), fileCopyRequest.getTargetFolderId(), treeStructureMap, fileIdSearchResult, totalDiskCapacity);

        // 如果存在文件夹，则异步复制子文件夹下的文件信息
        if (CollectionUtil.isNotEmpty(fileFolderList)) {
            CopyFileConsumerRequestDTO copyFileConsumerRequest = CopyFileConsumerRequestDTO.builder()
                    .targetUserId(userinfo.getBusinessId())
                    .fromUserId(content.getUserId())
                    .targetFolderId(fileCopyRequest.getTargetFolderId())
                    .treeStructureMap(nodeMap)
                    .preCopyFileList(fileFolderList)
                    .build();
            // 使用RocketMQ同步模式发送消息
            MessageBuilder.syncSend(ConstantConfig.Topic.FILE_SERVICE, ConstantConfig.Tag.COPY_FILE, copyFileConsumerRequest,
                    rocketmqTemplate, consumerRecord -> rocketmqConsumerRecordService.insert(consumerRecord));
        }
    }

    /**
     * 对指定的字段自增，如: browse_count、save_count、download_count
     *
     * @param shareId   分享标识
     * @param fieldName 字段名(browse_count、save_count、download_count)
     * @param userId    所属的用户标识
     */
    @Override
    public void increase(String shareId, ConstantConfig.FileShareIncreaseEnum fieldName, String userId) {
        // 保存、转存次数自增
        fileShareService.increase(shareId, fieldName, userId);
    }

    /**
     * 校验分享链接的key值是否正确
     *
     * @param shareId  分享短链
     * @param fileInfo 需要校验的文件信息
     * @return true:校验通过，false:校验失败
     */
    @Override
    public Boolean shareKeyCheck(String shareId, List<FileNestedModel> fileInfo) {
        // 根据短链接标识查询分享记录信息，获取到分享记录信息
        FileShareModel content = this.getShareContent(shareId);
        return this.shareKeyCheck(content, fileInfo);
    }

    // ==================================================== private ====================================================

    /**
     * 从分享的文件列表中获取到进行分享的文件名
     *
     * @param firstFileName   文件列表中的第一个文件名
     * @param fileCount       文件数量
     * @param isContainFolder 分享的文件列表中是否包含文件夹，true-包含，false-不包含
     * @return 进行分享的文件名
     */
    private String getShareTitle(String firstFileName, long fileCount, boolean isContainFolder) {
        // 判断文件列表中的文件数量是否大于1
        if (fileCount <= NumberConstant.INTEGER_ONE) {
            // 如果文件数量小于等于1，则直接返回文件名
            return firstFileName;
        }

        // 判断分享的文件列表中是否包含文件夹
        if (isContainFolder) {
            // 如果包含文件夹，则将文件名设置为“多个文件(夹)”
            return String.format(SHARE_FILE_NAME_SUFFIX_FILE_FOLDER, firstFileName, fileCount);
        }

        // 如果不包含文件夹，则将文件名设置为“多个文件”
        return String.format(SHARE_FILE_NAME_SUFFIX_FILE, firstFileName, fileCount);
    }

    /**
     * 根据分享的文件列表获取文件分享记录详情实体列表
     *
     * @param userId        用户标识
     * @param shareId       分享记录标识
     * @param shareFileList 分享的文件列表
     * @return 文件分享记录详情实体列表
     */
    private List<FileShareDetail> getFileShareDetailList(String userId, String shareId, List<DiskFile> shareFileList) {
        List<FileShareDetail> fileShareDetailList = Lists.newArrayListWithExpectedSize(shareFileList.size());
        for (DiskFile shareFile : shareFileList) {
            fileShareDetailList.add(FileShareDetail.builder()
                    .userId(userId)
                    .shareId(shareId)
                    .fileId(shareFile.getBusinessId())
                    .build());
        }
        return fileShareDetailList;
    }

    /**
     * 构建网盘文件分享记录详情信息数据列表
     *
     * @param request  匿名分享的文件请求数据模型
     * @param response 匿名分享的文件响应数据模型
     * @param content  网盘文件分享记录信息数据模型
     * @return 匿名分享的文件响应数据模型
     */
    private FileShareAnonymousResponseDTO buildAnonymousResponse(FileShareAnonymousRequestDTO request,
                                                                 FileShareAnonymousResponseDTO response, FileShareModel content) {
        // 构建分享文件鉴权信息
        FileAuthModel shareAuthModel = FileAuthModel.builder()
                .userId(response.getUserId())
                .shareShort(content.getShareId())
                .code(content.getSharePwd())
                .timestamp(System.currentTimeMillis())
                .build();

        // 执行文件信息搜索请求响应结果
        Result<List<QueryElasticsearchDiskFileResponseDTO>> queryElasticResponse;

        // 文件父级标识和文件夹的key值都不为空时
        if (StringUtils.isNotBlank(request.getFileParentId())
                && StringUtils.isNotBlank(request.getKey())) {
            // 校验key值是否正确，这里的key是将文件的业务id和salt值进行拼接后进行md5加密后的值
            if (!this.verifyShareKey(content, request.getFileParentId(), request.getKey())) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_KEY);
            }

            // 构建ES文件查询请求数据模型
            QueryElasticsearchDiskFileRequestDTO queryFileRequest = QueryElasticsearchDiskFileRequestDTO.builder()
                    .userId(response.getUserId())
                    .fileParentId(request.getFileParentId())
                    .count(request.getCount())
                    .sort(Collections.singletonList(request.getSort()))
                    .build();

            // 解密游标数据
            queryFileRequest.setSearchAfter(fileManager.decryptMarker(request.getMarker()));

            // 执行文件信息搜索请求
            queryElasticResponse = diskFileSearchFeignClient.fileInfoSearch(queryFileRequest);
        } else {
            // 根据分享的文件标识查询文件列表(根目录)
            List<FileShareDetail> fileShareDetailList = fileShareDetailService.listFileShareDetail(content.getShareId());
            List<String> fileIdList = fileShareDetailList.stream().map(FileShareDetail::getFileId).toList();
            if (CollectionUtil.isEmpty(fileIdList)) {
                // 分享的文件列表为空，默认返回空列表
                return response;
            }

            // 构建ES搜索文件Id数据时的请求数据模型
            QueryElasticsearchDiskFileIdRequestDTO queryElasticFileIdRequest = QueryElasticsearchDiskFileIdRequestDTO.builder()
                    .businessId(fileIdList)
                    .count(request.getCount())
                    .sort(Collections.singletonList(request.getSort()))
                    .build();

            // 解密游标数据
            queryElasticFileIdRequest.setSearchAfter(fileManager.decryptMarker(request.getMarker()));

            // 根据分享的文件id列表查询文件信息
            queryElasticResponse = diskFileSearchFeignClient.fileIdSearch(queryElasticFileIdRequest);
        }

        // 判断文件信息搜索请求响应结果
        if (Result.isNotSuccess(queryElasticResponse) || CollectionUtil.isEmpty(queryElasticResponse.getData())) {
            return response;
        }

        // 构建文件分享详情信息数据模型
        FileSearchResponseDTO fileSearchResponse = fileManager.accessUrl(shareAuthModel, queryElasticResponse.getData(), consumer ->
                // 生成分享时的文件key值，这里是用来构建文件访问时的鉴权签名的
                consumer.setShareKey(DigestUtils.md5DigestAsHex((content.getShareSalt() + consumer.getFileId()).getBytes())));
        List<FileShareDetailModel> fileShareDetailModelList = new ArrayList<>(queryElasticResponse.getData().size());
        for (QueryElasticsearchDiskFileResponseDTO diskFile : queryElasticResponse.getData()) {
            // 数据类型转换，同时生成分享时的文件key值
            fileShareDetailModelList.add(fileShareConvert.diskFileConvertShareDetail(diskFile.getContent(), content));
        }
        response.setContent(fileShareDetailModelList);
        response.setMarker(fileSearchResponse.getMarker());
        return response;
    }

    /**
     * 校验分享链接的key值是否正确
     *
     * @param content  网盘文件分享记录信息数据模型
     * @param fileInfo 网盘文件信息数据模型
     * @return true:校验通过，false:校验失败
     */
    private Boolean shareKeyCheck(FileShareModel content, List<FileNestedModel> fileInfo) {
        if (CollectionUtil.isEmpty(fileInfo)) {
            return Boolean.FALSE;
        }

        // 根据短链接标识查询分享记录信息，获取到分享记录信息
        if (content == null) {
            return Boolean.FALSE;
        }

        // 校验其中所有的文件信息是否都在分享的文件列表中，如果有一个不在则返回false
        for (FileNestedModel nestedModel : fileInfo) {
            if (!this.verifyShareKey(content, nestedModel.getFileId(), nestedModel.getKey())) {
                return Boolean.FALSE;
            }
        }

        // 返回校验通过
        return Boolean.TRUE;
    }

    /**
     * 分享链接基础信息校验，校验失败会抛出业务异常
     * <pre>
     *     1.校验分享链接是否存在
     *     2.校验分享链接是否被禁用
     *     3.校验分享链接是否过期
     *     4.校验分享提取码是否正确
     *     5.校验分享中文件key值是否正确
     * </pre>
     *
     * @param content  网盘文件分享记录信息数据模型
     * @param sharePwd 分享提取码
     * @return 返回匿名分享的文件响应数据模型
     */
    private FileShareAnonymousResponseDTO basicInfoCheck(FileShareModel content, String sharePwd) {
        if (content == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SHARE_ID_INVALID);
        }

        // 文件分享链接被屏蔽时抛出异常
        if (ConstantConfig.FileShareStatusEnum.CLOSE.getStatus().equals(content.getStatus())) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.FILE_SHARE_BLOCKED);
        }

        // 判断分享记录信息是否已经过期
        if (content.getExpired()) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SHARE_FILE_EXPIRE);
        }

        // 过期时间不为空时判断是否在当前时间之后
        if (content.getExpiration() != null && LocalDateTime.now().isAfter(content.getExpiration())) {
            // 过期时间在当前时间之前
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SHARE_FILE_EXPIRE);
        }

        // 获取分享的用户信息
        DiskUser userInfo = diskUserService.getDiskUser(content.getUserId());

        // 构建请求返回数据模型
        FileShareAnonymousResponseDTO response = fileShareConvert.fileShareModelConvertAnonymousResponse(userInfo, content);

        // 判断是否需要输入提取码
        if (StringUtils.isNotBlank(content.getSharePwd())) {
            // 判断是否输入了提取码
            if (StringUtils.isBlank(sharePwd)) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SHARE_PWD_INVALID, response);
            }
            // 判断提取码是否正确
            if (!content.getSharePwd().equals(sharePwd)) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SHARE_PWD_ERROR, response);
            }
        }

        return response;
    }

    /**
     * 校验分享链接的key值是否正确
     *
     * @param content  分享的文件信息
     * @param fileId   分享的文件标识
     * @param shareKey 分享的key值
     * @return true:校验通过，false:校验失败
     */
    private Boolean verifyShareKey(FileShareModel content, String fileId, String shareKey) {
        // 校验key值是否正确，这里的key是将文件的业务id和salt值进行拼接后进行md5加密后的值
        return DigestUtils.md5DigestAsHex((content.getShareSalt() + fileId).getBytes()).equals(shareKey);
    }

    /**
     * 根据分享链接标识查询分享的文件信息，如果查询失败或者分享链接不存在则返回null
     *
     * @param shareId 分享链接标识
     * @return 分享的文件信息，不存在时返回null
     */
    private FileShareModel getShareContent(String shareId) {
        // 构建缓存key
        String cacheKey = ConstantConfig.Cache.FILE_SHARE_CACHE + shareId;
        // 优先从缓存中获取分享链接信息
        return Optional.ofNullable((FileShareModel) cacheManagerConfig.getCache(cacheKey)).orElseGet(() -> {
            // 缓存中不存在分享链接信息时从elastic库中查询
            QueryElasticsearchFileShareIdRequestDTO queryFileShareIdRequest = QueryElasticsearchFileShareIdRequestDTO.builder()
                    .shareId(Collections.singletonList(shareId))
                    .build();
            Result<QueryElasticsearchFileShareIdResponseDTO> queryElasticShareIdResult = diskFileSearchFeignClient.shareIdResponse(queryFileShareIdRequest);
            // 判断查询结果是否为空
            if (Result.isNotSuccess(queryElasticShareIdResult)
                    || CollectionUtil.isEmpty(queryElasticShareIdResult.getData().getContent())) {
                return null;
            }

            // 获取分享链接信息
            FileShareModel content = queryElasticShareIdResult.getData().getContent().get(NumberConstant.INTEGER_ZERO);

            // 从数据库中查询文件分享信息，补全分享记录信息数据模型
            FileShare fileShare = fileShareService.getFileShare(content.getBusinessId(), content.getUserId());
            if (fileShare == null) {
                return null;
            }

            // 构建文件分享数据模型实体
            FileShareModel fileShareModel = fileShareConvert.fileShareConvertFileShareModel(fileShare, fileShare.getShareSalt());
            // 数据不为空时，将查询结果压入缓存
            Optional.ofNullable(fileShareModel).ifPresent(shareModel -> cacheManagerConfig.putCache(cacheKey, shareModel, ConstantConfig.Cache.DEFAULT_EXPIRE));
            return fileShareModel;
        });
    }
}
