package com.doudoudrive.search.model.dto.response;

import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.search.model.elasticsearch.UserInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;

/**
 * <p>查询用户关键信息是否存在的响应数据模型</p>
 * <p>2022-03-22 00:13</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoKeyExistsSearchResponseDTO {

    /**
     * 标识指定用户关键信息是否存在
     */
    private Boolean exists;

    /**
     * 搜索结果
     */
    private SearchHits<UserInfoDTO> searchHits;

    /**
     * 搜索结果
     */
    private StatusCodeEnum describe;

}
