package com.doudoudrive.file.model.convert;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.FileShareDetailModel;
import com.doudoudrive.common.model.dto.model.FileShareModel;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchFileShareRequestDTO;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.model.pojo.FileShare;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.file.model.dto.request.CreateFileShareRequestDTO;
import com.doudoudrive.file.model.dto.response.CreateFileShareResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareAnonymousResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

/**
 * <p>文件分享记录信息等相关的实体数据类型转换器</p>
 * <p>2022-09-29 01:13</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {IdUtil.class, RandomUtil.class, NumberConstant.class, Boolean.class, LocalDateTime.class, DigestUtils.class, DateUtils.class})
public interface FileShareConvert {

    /**
     * 将 CreateFileShareRequestDTO(网盘文件创建分享链接时的请求数据模型) 类型转换为 FileShare(文件分享信息实体类) 类型
     *
     * @param userId                 进行分享的用户标识
     * @param shareTitle             进行分享的文件名(取每次进行分享的第一个文件名)
     * @param folder                 分享的文件中是否包含文件夹(0:false,1:true)
     * @param fileCount              分享的文件数量
     * @param createFileShareRequest 网盘文件创建分享链接时的请求数据模型
     * @return FileShare(文件分享信息实体类)
     */
    @Mappings({
            @Mapping(target = "shareId", expression = "java(IdUtil.fastSimpleUUID())"),
            @Mapping(target = "shareSalt", expression = "java(RandomUtil.randomString(NumberConstant.INTEGER_THIRTY_TWO))"),
            @Mapping(target = "browseCount", expression = "java(NumberConstant.LONG_ZERO)"),
            @Mapping(target = "saveCount", expression = "java(NumberConstant.LONG_ZERO)"),
            @Mapping(target = "downloadCount", expression = "java(NumberConstant.LONG_ZERO)"),
            @Mapping(target = "expired", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "status", constant = NumberConstant.STRING_ZERO),
            @Mapping(target = "createTime", expression = "java(LocalDateTime.now())"),
            @Mapping(target = "updateTime", expression = "java(LocalDateTime.now())")
    })
    FileShare fileShareConvert(String userId, String shareTitle, Boolean folder,
                               Integer fileCount, CreateFileShareRequestDTO createFileShareRequest);

    /**
     * 将 FileShare(文件分享信息实体类) 类型转换为 SaveElasticsearchFileShareRequestDTO(保存es文件分享记录信息时的请求数据模型)
     *
     * @param fileShare 文件分享信息实体类
     * @return 保存es文件分享记录信息时的请求数据模型
     */
    SaveElasticsearchFileShareRequestDTO fileShareConvertSaveFileShare(FileShare fileShare);

    /**
     * 将 FileShare(文件分享信息实体类) 类型转换为 CreateFileShareResponseDTO(网盘文件创建分享链接时的响应数据模型)
     *
     * @param fileShare 文件分享信息实体类
     * @return 网盘文件创建分享链接时的响应数据模型
     */
    CreateFileShareResponseDTO fileShareConvertCreateFileShareResponse(FileShare fileShare);

    /**
     * 将 FileShare(文件分享信息实体类) 类型转换为 FileShareModel(文件分享记录信息数据模型)
     *
     * @param fileShare 文件分享信息实体类
     * @return 文件分享记录信息数据模型
     */
    FileShareModel fileShareConvertFileShareModel(FileShare fileShare);

    /**
     * 将 FileShareModel(文件分享记录信息) 类型转换为 FileShareAnonymousResponseDTO(匿名用户访问分享链接时的响应数据模型)
     *
     * @param userInfo       用户信息
     * @param fileShareModel 文件分享记录信息
     * @return 匿名用户访问分享链接时的响应数据模型
     */
    @Mappings({
            @Mapping(target = "userId", source = "userInfo.businessId"),
            @Mapping(target = "createTime", source = "fileShareModel.createTime"),
            @Mapping(target = "content", expression = "java(java.util.Collections.emptyList())"),
            @Mapping(target = "marker", expression = "java(org.apache.commons.lang3.StringUtils.EMPTY)")
    })
    FileShareAnonymousResponseDTO fileShareModelConvertAnonymousResponse(DiskUser userInfo, FileShareModel fileShareModel);

    /**
     * 将 DiskFileModel(网盘文件数据模型) 类型转换为 FileShareDetailModel(网盘文件分享记录详情信息数据模型)
     * 这里的key是将文件的业务id和salt值进行拼接后进行md5加密后的值
     *
     * @param diskFile 网盘文件数据模型
     * @param share    文件分享记录信息
     * @return 网盘文件分享记录详情信息数据模型
     */
    @Mappings({
            @Mapping(target = "businessId", source = "diskFile.businessId"),
            @Mapping(target = "createTime", expression = "java(DateUtils.toLocalDateTime(diskFile.getCreateTime()))"),
            @Mapping(target = "updateTime", expression = "java(DateUtils.toLocalDateTime(diskFile.getUpdateTime()))"),
    })
    FileShareDetailModel diskFileConvertShareDetail(DiskFileModel diskFile, FileShareModel share);
}
