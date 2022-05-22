package com.doudoudrive.search.model.convert;

import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchDiskFileRequestDTO;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * <p>用户文件信息ES数据模型通用转换器</p>
 * <p>忽略NULL值</p>
 * <p>2022-05-22 14:43</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DiskFileModelConvert {

    /**
     * 将SaveElasticsearchDiskFileRequestDTO(保存es用户文件信息时的请求数据模型) 类型转换为 DiskFileDTO(用户文件实体信息ES数据模型)
     *
     * @param requestDTO 保存es用户文件信息时的请求数据模型
     * @return 用户文件实体信息ES数据模型
     */
    DiskFileDTO saveElasticsearchDiskFileRequestConvertDiskFile(SaveElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 将UpdateElasticsearchDiskFileRequestDTO(修改es用户文件信息时的请求数据模型) 类型转换为 DiskFileDTO(用户文件实体信息ES数据模型)
     *
     * @param requestDTO 修改es用户文件信息时的请求数据模型
     * @return 用户文件实体信息ES数据模型
     */
    DiskFileDTO updateElasticsearchDiskFileRequestConvertDiskFile(UpdateElasticsearchDiskFileRequestDTO requestDTO);

}
