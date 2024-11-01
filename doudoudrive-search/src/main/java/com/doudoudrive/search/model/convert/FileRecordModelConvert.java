package com.doudoudrive.search.model.convert;

import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchFileRecordResponseDTO;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.search.model.elasticsearch.FileRecordDTO;
import org.mapstruct.*;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.Date;
import java.util.List;

/**
 * <p>用文件临时操作记录信息ES数据模型通用转换器</p>
 * <p>忽略NULL值</p>
 * <p>2023-07-27 14:40</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {SequenceUtil.class, SequenceModuleEnum.class, Date.class})
public interface FileRecordModelConvert {

    /**
     * 将SaveElasticsearchFileRecordRequestDTO(保存es文件临时操作记录信息时的请求数据模型) 类型转换为 FileRecordDTO(文件临时操作记录信息ES数据模型)
     *
     * @param fileRecordInfo 保存es文件临时操作记录信息时的请求数据模型
     * @return 文件临时操作记录信息ES数据模型
     */
    List<FileRecordDTO> saveFileRecordConvertFileRecord(List<SaveElasticsearchFileRecordRequestDTO> fileRecordInfo);

    /**
     * 将SaveElasticsearchFileRecordRequestDTO(保存es文件临时操作记录信息时的请求数据模型) 类型转换为 FileRecordDTO(文件临时操作记录信息ES数据模型)
     *
     * @param saveElasticsearchFileRecordRequestDTO 保存es文件临时操作记录信息时的请求数据模型
     * @return 文件临时操作记录信息ES数据模型
     */
    @Mappings({
            @Mapping(target = "businessId", expression = "java(SequenceUtil.nextId(SequenceModuleEnum.FILE_RECORD))"),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())")
    })
    FileRecordDTO saveFileRecordConvertFileRecord(SaveElasticsearchFileRecordRequestDTO saveElasticsearchFileRecordRequestDTO);

    /**
     * 将List<SearchHit<DiskFileDTO>>(用户文件实体信息ES数据模型) 类型转换为 List<QueryElasticsearchDiskFileResponseDTO>(搜索es用户文件信息时的响应数据模型)
     *
     * @param searchHit 用户文件实体信息ES数据模型
     * @return 搜索es用户文件信息时的响应数据模型
     */
    List<QueryElasticsearchFileRecordResponseDTO> fileRecordConvertQueryFileRecordResponse(List<SearchHit<FileRecordDTO>> searchHit);

}
