package com.doudoudrive.file.model.convert;

import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.FileRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

/**
 * <p>文件操作记录信息等相关的实体数据类型转换器</p>
 * <p>2022-09-08 14:54</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Date.class})
public interface FileRecordConvert {

    /**
     * 将 DiskFile(用户文件模块实体类) 类型转换为 FileRecord(文件临时操作记录模块实体)
     *
     * @param content    用户文件模块实体类
     * @param userId     用户id
     * @param action     动作枚举
     * @param actionType 动作类型枚举
     * @return 文件临时操作记录模块实体
     */
    @Mappings({
            @Mapping(target = "fileId", source = "content.businessId"),
            @Mapping(target = "userId", source = "userId"),
            @Mapping(target = "autoId", expression = "java(null)"),
            @Mapping(target = "businessId", expression = "java(null)"),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    FileRecord diskFileConvertFileRecord(DiskFile content, String userId, String action, String actionType);

}
