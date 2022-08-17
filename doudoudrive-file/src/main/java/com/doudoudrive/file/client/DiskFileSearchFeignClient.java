package com.doudoudrive.file.client;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.*;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileIdResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>用户文件信息搜索服务Feign调用</p>
 * <p>2022-05-22 16:31</p>
 *
 * @author Dan
 **/
@FeignClient(name = "searchServer", contextId = "diskFileSearchFeignClient")
public interface DiskFileSearchFeignClient {

    /**
     * 在es中保存用户文件信息
     *
     * @param requestDTO 保存es用户文件信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file/save", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> saveElasticsearchDiskFile(@RequestBody SaveElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 删除es中保存的用户文件信息
     *
     * @param requestDTO 删除es用户文件信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> deleteElasticsearchDiskFile(@RequestBody DeleteElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 更新es中保存的用户文件信息
     *
     * @param requestDTO 更新es用户文件信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file/update", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> updateElasticsearchDiskFile(@RequestBody UpdateElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 文件信息搜索，支持翻页
     *
     * @param requestDTO 搜索es用户文件信息时的请求数据模型
     * @return 搜索结果
     */
    @PostMapping(value = "/search/file", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<List<QueryElasticsearchDiskFileResponseDTO>> fileInfoSearch(@RequestBody QueryElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 根据文件id批量查询文件信息
     *
     * @param requestDTO 查询文件Id信息时的请求数据模型
     * @return 查询结果
     */
    @PostMapping(value = "/search/file/id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<QueryElasticsearchDiskFileIdResponseDTO> fileIdSearch(@RequestBody QueryElasticsearchDiskFileIdRequestDTO requestDTO);

}
