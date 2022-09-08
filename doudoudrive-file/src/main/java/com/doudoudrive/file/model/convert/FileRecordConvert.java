package com.doudoudrive.file.model.convert;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.response.QueryExecutableTaskResponseDTO;
import com.doudoudrive.common.model.pojo.FileRecord;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

/**
 * <p>文件操作记录信息等相关的实体数据类型转换器</p>
 * <p>2022-09-08 14:54</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Boolean.class, Date.class, ConstantConfig.class})
public interface FileRecordConvert {

    /**
     * 将 FileRecord(文件临时操作记录模块实体) 类型转换为 QueryFileRecordActionResponseDTO(获取指定动作的文件操作记录数据时的响应数据模型)
     *
     * @param content 文件临时操作记录模块实体
     * @return 获取指定动作的文件操作记录数据时的响应数据模型
     */
    QueryExecutableTaskResponseDTO fileRecordConvertQueryFileRecordActionResponse(FileRecord content);

}
