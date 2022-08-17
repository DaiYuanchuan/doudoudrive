package com.doudoudrive.search.model.convert;

import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

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

    /**
     * 将List<SearchHit<DiskFileDTO>>(用户文件实体信息ES数据模型) 类型转换为 List<QueryElasticsearchDiskFileResponseDTO>(搜索es用户文件信息时的响应数据模型)
     *
     * @param searchHit 用户文件实体信息ES数据模型
     * @return 搜索es用户文件信息时的响应数据模型
     */
    List<QueryElasticsearchDiskFileResponseDTO> diskFileDTOConvertQueryDiskFileResponse(List<SearchHit<DiskFileDTO>> searchHit);

    /**
     * 将DiskFileDTO(用户文件实体信息ES数据模型) 类型转换为 DiskFileModel(用户文件信息模型)
     *
     * @param diskFile 用户文件实体信息ES数据模型
     * @return 用户文件信息模型
     */
    DiskFileModel diskFileDTOConvertDiskFileModel(DiskFileDTO diskFile);
}
