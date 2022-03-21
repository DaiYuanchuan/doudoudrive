package com.doudoudrive.search.model.convert;

import com.doudoudrive.common.model.dto.request.SaveElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchUserInfoRequestDTO;
import com.doudoudrive.common.model.dto.response.UsernameSearchResponseDTO;
import com.doudoudrive.search.model.elasticsearch.UserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * <p>用户实体信息ES数据模型通用转换器</p>
 * <p>忽略NULL值</p>
 * <p>2022-03-21 13:34</p>
 *
 * @author Dan
 **/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserInfoModelConvert {

    /**
     * 将SaveElasticsearchUserInfoRequestDTO(保存es用户信息时的请求数据模型) 类型转换为 UserInfoDTO(用户实体信息ES数据模型)
     *
     * @param requestDTO 保存es用户信息时的请求数据模型
     * @return 用户实体信息ES数据模型
     */
    UserInfoDTO saveElasticsearchUserInfoRequestConvert(SaveElasticsearchUserInfoRequestDTO requestDTO);

    /**
     * 将UpdateElasticsearchUserInfoRequestDTO(更新es用户信息时的请求数据模型) 类型转换为 UserInfoDTO(用户实体信息ES数据模型)
     *
     * @param requestDTO 更新es用户信息时的请求数据模型
     * @return 用户实体信息ES数据模型
     */
    UserInfoDTO updateElasticsearchUserInfoRequestConvert(UpdateElasticsearchUserInfoRequestDTO requestDTO);

    /**
     * 将 UserInfoDTO(用户实体信息ES数据模型) 类型转换为 UsernameSearchResponseDTO(通过用户名查询用户信息请求的响应数据模型)
     *
     * @param userInfoDTO 用户实体信息ES数据模型
     * @return 通过用户名查询用户信息请求的响应数据模型
     */
    UsernameSearchResponseDTO usernameSearchResponseConvert(UserInfoDTO userInfoDTO);

}
