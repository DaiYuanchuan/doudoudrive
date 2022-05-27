package com.doudoudrive.file.model.convert;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.FileRecord;
import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.common.util.lang.MimeTypes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

/**
 * <p>用户文件信息等相关的实体数据类型转换器</p>
 * <p>2022-05-21 21:43</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Boolean.class, CharSequenceUtil.class, Date.class, ConstantConfig.class})
public interface DiskFileConvert {

    /**
     * 将 diskFile(用户文件模块实体类) 类型转换为 DiskFileModel(网盘文件数据模型)
     *
     * @param diskFile 用户文件模块实体类
     * @return 网盘文件数据模型
     */
    DiskFileModel diskFileConvertDiskFileModel(DiskFile diskFile);

    /**
     * 将 createFileAuthModel(创建文件时的鉴权参数模型) 类型转换为 DiskFileModel(网盘文件数据模型)
     *
     * @param createFileAuthModel 创建文件时的鉴权参数模型
     * @param businessId          创建文件时的文件业务标识
     * @return 网盘文件数据模型
     */
    @Mappings({
            @Mapping(target = "fileFolder", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "forbidden", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "collect", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    DiskFileModel createFileAuthConvertDiskFileModel(CreateFileAuthModel createFileAuthModel, String businessId);

    /**
     * 创建文件时的转换，默认为创建文件夹
     *
     * @param userId   用户标识
     * @param name     文件夹名称
     * @param parentId 文件父级标识
     * @return 用户文件模块信息
     */
    @Mappings({
            @Mapping(target = "fileName", source = "name"),
            @Mapping(target = "fileParentId", source = "parentId"),
            @Mapping(target = "fileSize", constant = NumberConstant.STRING_ZERO),
            @Mapping(target = "fileMimeType", constant = MimeTypes.DEFAULT_MIMETYPE),
            @Mapping(target = "fileEtag", expression = "java(CharSequenceUtil.EMPTY)"),
            @Mapping(target = "fileFolder", expression = "java(Boolean.TRUE)"),
            @Mapping(target = "forbidden", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "collect", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "status", constant = NumberConstant.STRING_ONE),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    DiskFile createFileConvert(String userId, String name, String parentId);

    /**
     * 将 diskFile(用户文件模块实体类) 类型转换为 SaveElasticsearchDiskFileRequestDTO(保存es用户文件信息时的请求数据模型)
     *
     * @param diskFile 用户文件模块实体类
     * @return 保存es用户文件信息时的请求数据模型
     */
    SaveElasticsearchDiskFileRequestDTO diskFileConvertSaveElasticsearchDiskFileRequest(DiskFile diskFile);

    /**
     * 将 CreateFileAuthModel(创建文件时的鉴权参数模型) 类型转换为 FileRecord(文件临时操作记录)
     *
     * @param createFileAuthModel 创建文件时的鉴权参数模型
     * @param fileId              文件标识
     * @param action              动作
     * @param actionType          动作对应的动作类型
     * @return 文件临时操作记录
     */
    FileRecord createFileAuthModelConvertFileRecord(CreateFileAuthModel createFileAuthModel, String fileId, String action, String actionType);

    /**
     * 将 CreateFileAuthModel(创建文件时的鉴权参数模型) 类型转换为 OssFile(OSS文件对象存储实体类)
     *
     * @param fileInfo 创建文件时的鉴权参数模型
     * @return OSS文件对象存储实体类
     */
    @Mappings({
            @Mapping(target = "etag", source = "fileEtag"),
            @Mapping(target = "size", source = "fileSize"),
            @Mapping(target = "mimeType", source = "fileMimeType"),
            @Mapping(target = "status", constant = NumberConstant.STRING_ZERO)
    })
    OssFile createFileAuthModelConvertOssFile(CreateFileAuthModel fileInfo);

    /**
     * 将 CreateFileAuthModel(创建文件时的鉴权参数模型) 类型转换为 DiskFile(用户文件模块实体类)
     *
     * @param fileInfo 创建文件时的鉴权参数模型
     * @return 用户文件模块实体类
     */
    @Mappings({
            @Mapping(target = "fileName", source = "fileInfo.name"),
            @Mapping(target = "fileFolder", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "forbidden", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "collect", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "status", constant = NumberConstant.STRING_ONE)
    })
    DiskFile createFileAuthModelConvertDiskFile(CreateFileAuthModel fileInfo, String businessId);

    /**
     * 将 OssFile(OSS文件对象存储实体类) 类型转换为 DiskFile(用户文件模块实体类)
     *
     * @param ossFile      OSS文件对象存储实体类
     * @param userId       用户标识
     * @param fileName     文件名称
     * @param fileParentId 文件父级标识
     * @return 用户文件模块实体类
     */
    @Mappings({
            @Mapping(target = "autoId", ignore = true),
            @Mapping(target = "businessId", ignore = true),
            @Mapping(target = "fileSize", source = "ossFile.size"),
            @Mapping(target = "fileMimeType", source = "ossFile.mimeType"),
            @Mapping(target = "fileEtag", source = "ossFile.etag"),
            @Mapping(target = "fileFolder", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "forbidden", expression = "java(ConstantConfig.OssFileStatusEnum.forbidden(ossFile.getStatus()))"),
            @Mapping(target = "collect", expression = "java(Boolean.FALSE)"),
            @Mapping(target = "status", constant = NumberConstant.STRING_ONE),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    DiskFile ossFileConvertDiskFile(OssFile ossFile, String userId, String fileName, String fileParentId);

    /**
     * 将 DiskFile(用户文件模块实体类) 类型转换为 CreateFileAuthModel(创建文件时的鉴权参数模型)
     *
     * @param diskFile 用户文件模块实体类
     * @return 创建文件时的鉴权参数模型
     */
    @Mappings({
            @Mapping(target = "name", source = "fileName")
    })
    CreateFileAuthModel diskFileConvertCreateFileAuthModel(DiskFile diskFile);

}
