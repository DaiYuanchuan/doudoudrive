package com.doudoudrive.search.model.convert;

import com.doudoudrive.common.model.dto.model.FileShareModel;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchFileShareRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchShareUserIdResponseDTO;
import com.doudoudrive.search.model.elasticsearch.FileShareDTO;
import org.mapstruct.*;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

/**
 * <p>用户文件分享模块信息ES数据模型通用转换器</p>
 * <p>忽略NULL值</p>
 * <p>2022-09-24 23:18</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FileShareModelConvert {

    /**
     * 将SaveElasticsearchFileShareRequestDTO(保存es文件分享信息时的请求数据模型) 类型转换为 FileShareDTO(用户文件分享模块信息ES数据模型)
     *
     * @param request 保存es文件分享信息时的请求数据模型
     * @return 用户文件分享模块信息ES数据模型
     */
    @Mapping(target = "createTime", expression = "java(com.doudoudrive.common.util.date.DateUtils.toDate(request.getCreateTime()))")
    FileShareDTO saveEsFileShareRequestConvertFileShare(SaveElasticsearchFileShareRequestDTO request);

    /**
     * 将SearchHit<FileShareDTO>(用户文件分享记录信息ES数据模型) 类型转换为 QueryElasticsearchShareUserIdResponseDTO(根据用户标识搜索es文件分享记录信息时的响应数据模型)
     *
     * @param searchHit 用户文件分享记录信息ES数据模型
     * @return 根据用户标识搜索es文件分享记录信息时的响应数据模型
     */
    List<QueryElasticsearchShareUserIdResponseDTO> fileShareConvertQueryShareUserIdResponse(List<SearchHit<FileShareDTO>> searchHit);

    /**
     * 将FileShareDTO(用户文件分享记录信息ES数据模型) 类型转换为 FileShareModel(网盘文件分享记录信息数据模型)
     *
     * @param fileShareDTO 用户文件分享记录信息ES数据模型
     * @return 网盘文件分享记录信息数据模型
     */
    FileShareModel fileShareConvertFileShareModel(FileShareDTO fileShareDTO);

}
