package com.doudoudrive.file.manager.impl;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.FileAuthModel;
import com.doudoudrive.common.model.dto.model.FileShareDetailModel;
import com.doudoudrive.common.model.dto.model.FileShareModel;
import com.doudoudrive.common.model.dto.request.*;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchFileShareResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchFileShareIdResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.model.pojo.FileShareDetail;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.commonservice.constant.TransactionManagerConstant;
import com.doudoudrive.commonservice.service.DiskUserService;
import com.doudoudrive.commonservice.service.FileShareDetailService;
import com.doudoudrive.file.client.DiskFileSearchFeignClient;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.FileShareManager;
import com.doudoudrive.file.model.convert.FileShareConvert;
import com.doudoudrive.file.model.dto.request.CreateFileShareRequestDTO;
import com.doudoudrive.file.model.dto.request.FileShareAnonymousRequestDTO;
import com.doudoudrive.file.model.dto.response.CreateFileShareResponseDTO;
import com.doudoudrive.file.model.dto.response.FileSearchResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareAnonymousResponseDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    private FileShareDetailService fileShareDetailService;
    private DiskUserService diskUserService;
    private FileManager fileManager;

    @Autowired
    public void setDiskFileSearchFeignClient(DiskFileSearchFeignClient diskFileSearchFeignClient) {
        this.diskFileSearchFeignClient = diskFileSearchFeignClient;
    }

    @Autowired(required = false)
    public void setFileShareConvert(FileShareConvert fileShareConvert) {
        this.fileShareConvert = fileShareConvert;
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

        // 构建保存到elastic中的文件分享记录信息
        String shareName = this.getShareName(shareFileList, containFolder);
        SaveElasticsearchFileShareRequestDTO saveFileShareRequest = fileShareConvert
                .fileShareConvertSaveFileShare(userId, shareName, containFolder, createFileShareRequest);

        // 保存分享的文件列表信息到分享详情表中
        fileShareDetailService.insertBatch(this.getFileShareDetailList(userId, saveFileShareRequest.getShareId(), shareFileList));

        // 保存文件分享记录信息到elastic中
        diskFileSearchFeignClient.saveElasticsearchFileShare(saveFileShareRequest);

        // 构建返回的文件分享记录信息
        return fileShareConvert.saveFileShareConvertCreateFileShareResponse(saveFileShareRequest);
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
    public DeleteElasticsearchFileShareResponseDTO cancelShare(List<String> shareId, DiskUserModel userinfo) {
        // 根据短链接标识批量删除文件分享记录详情数据
        fileShareDetailService.delete(shareId, userinfo.getBusinessId());
        // 删除es文件分享记录信息
        Result<DeleteElasticsearchFileShareResponseDTO> deleteElasticShareResult = diskFileSearchFeignClient.cancelShare(DeleteElasticsearchFileShareRequestDTO.builder()
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
        // 根据短链接标识查询分享记录信息
        Result<QueryElasticsearchFileShareIdResponseDTO> queryElasticShareIdResult = diskFileSearchFeignClient.shareIdResponse(QueryElasticsearchFileShareIdRequestDTO.builder()
                .shareId(Collections.singletonList(anonymousRequest.getShareId()))
                .updateViewCount(anonymousRequest.getUpdateViewCount())
                .build());
        // 判断查询结果是否为空
        if (Result.isNotSuccess(queryElasticShareIdResult)
                || CollectionUtil.isEmpty(queryElasticShareIdResult.getData().getContent())) {
            return Result.build(StatusCodeEnum.SHARE_ID_INVALID);
        }

        // 获取到分享记录信息
        FileShareModel content = queryElasticShareIdResult.getData().getContent().get(NumberConstant.INTEGER_ZERO);
        if (content == null) {
            return Result.build(StatusCodeEnum.SHARE_ID_INVALID);
        }

        // 判断分享记录信息是否已经过期
        if (content.getExpired()) {
            return Result.build(StatusCodeEnum.SHARE_FILE_EXPIRE);
        }

        // 过期时间不为空时判断是否在当前时间之后
        if (content.getExpiration() != null && new Date().after(content.getExpiration())) {
            // 过期时间在当前时间之前
            return Result.build(StatusCodeEnum.SHARE_FILE_EXPIRE);
        }

        // 获取分享的用户信息
        DiskUser userInfo = diskUserService.getDiskUser(content.getUserId());

        // 构建请求返回数据模型
        FileShareAnonymousResponseDTO response = fileShareConvert.fileShareModelConvertAnonymousResponse(userInfo, content);

        // 判断是否需要输入提取码
        if (StringUtils.isNotBlank(content.getSharePwd())) {
            // 判断是否输入了提取码
            if (StringUtils.isBlank(anonymousRequest.getSharePwd())) {
                return Result.build(StatusCodeEnum.SHARE_PWD_INVALID, response);
            }
            // 判断提取码是否正确
            if (!content.getSharePwd().equals(anonymousRequest.getSharePwd())) {
                return Result.build(StatusCodeEnum.SHARE_PWD_ERROR, response);
            }
        }

        // 构建网盘文件分享记录详情信息数据列表
        return Result.ok(this.buildAnonymousResponse(anonymousRequest, response, content));
    }

    // ==================================================== private ====================================================

    /**
     * 从分享的文件列表中获取到进行分享的文件名
     *
     * @param shareFileList   分享的文件列表
     * @param isContainFolder 分享的文件列表中是否包含文件夹，true-包含，false-不包含
     * @return 进行分享的文件名
     */
    private String getShareName(List<DiskFile> shareFileList, boolean isContainFolder) {
        // 获取文件列表中的第一个文件名
        String firstFileName = shareFileList.get(NumberConstant.INTEGER_ZERO).getFileName();

        // 判断文件列表中的文件数量是否大于1
        if (shareFileList.size() <= NumberConstant.INTEGER_ONE) {
            // 如果文件数量小于等于1，则直接返回文件名
            return firstFileName;
        }

        // 判断分享的文件列表中是否包含文件夹
        if (isContainFolder) {
            // 如果包含文件夹，则将文件名设置为“多个文件(夹)”
            return String.format(SHARE_FILE_NAME_SUFFIX_FILE_FOLDER, firstFileName, shareFileList.size());
        }

        // 如果不包含文件夹，则将文件名设置为“多个文件”
        return String.format(SHARE_FILE_NAME_SUFFIX_FILE, firstFileName, shareFileList.size());
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
            String characterSigned = DigestUtils.md5DigestAsHex((content.getShareSalt() + request.getFileParentId()).getBytes());
            if (!characterSigned.equals(request.getKey())) {
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
                // 生成分享时的文件key值
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
}
