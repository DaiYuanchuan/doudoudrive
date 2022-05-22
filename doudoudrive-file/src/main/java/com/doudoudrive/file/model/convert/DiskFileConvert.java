package com.doudoudrive.file.model.convert;

import cn.hutool.core.text.CharSequenceUtil;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
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
        imports = {Boolean.class, CharSequenceUtil.class, Date.class})
public interface DiskFileConvert {

    /**
     * 将 diskFile(用户文件模块实体类) 类型转换为 DiskFileModel(网盘文件数据模型)
     *
     * @param diskFile 用户文件模块实体类
     * @return 网盘文件数据模型
     */
    DiskFileModel diskFileConvertDiskFileModel(DiskFile diskFile);

    /**
     * 创建文件夹时的转换
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
    DiskFile createFolderConvertDiskFile(String userId, String name, String parentId);

    /**
     * 将 diskFile(用户文件模块实体类) 类型转换为 SaveElasticsearchDiskFileRequestDTO(保存es用户文件信息时的请求数据模型)
     *
     * @param diskFile 用户文件模块实体类
     * @return 保存es用户文件信息时的请求数据模型
     */
    SaveElasticsearchDiskFileRequestDTO diskFileConvertSaveElasticsearchDiskFileRequest(DiskFile diskFile);

}
