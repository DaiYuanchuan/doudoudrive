package com.doudoudrive.file.client;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.*;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchFileShareIdResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchShareUserIdResponseDTO;
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
    Result<String> saveElasticsearchDiskFile(@RequestBody BatchSaveElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 删除es中保存的用户文件信息
     *
     * @param requestDTO 删除es用户文件信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<DeleteElasticsearchResponseDTO> deleteElasticsearchDiskFile(@RequestBody DeleteElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 批量更新es中保存的用户文件信息
     *
     * @param requestDTO 批量更新es用户文件信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file/update", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> updateElasticsearchDiskFile(@RequestBody UpdateBatchElasticsearchDiskFileRequestDTO requestDTO);

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
    Result<List<QueryElasticsearchDiskFileResponseDTO>> fileIdSearch(@RequestBody QueryElasticsearchDiskFileIdRequestDTO requestDTO);

    /**
     * 根据文件父级业务标识批量查询用户文件信息
     *
     * @param requestDTO 查询文件父级业务标识信息时的请求数据模型
     * @return 查询结果
     */
    @PostMapping(value = "/search/file/parent-id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<List<QueryElasticsearchDiskFileResponseDTO>> fileParentIdSearch(@RequestBody QueryElasticsearchDiskFileParentIdRequestDTO requestDTO);

    // =============================================== 以下为分享文件搜索 ===============================================

    /**
     * 保存es文件分享记录信息
     *
     * @param saveFileShareRequest 保存es文件分享记录信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file-share/save", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> saveElasticsearchFileShare(@RequestBody SaveElasticsearchFileShareRequestDTO saveFileShareRequest);

    /**
     * 删除es文件分享记录信息
     *
     * @param cancelShareRequest 删除es文件分享记录信息时的请求数据模型
     * @return 返回删除结果
     */
    @PostMapping(value = "/search/file-share/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<DeleteElasticsearchResponseDTO> cancelShare(@RequestBody DeleteElasticsearchFileShareRequestDTO cancelShareRequest);

    /**
     * 根据用户标识搜索es文件分享记录信息
     *
     * @param userIdSearchRequest 用户标识搜索es文件分享记录信息时的请求数据模型
     * @return 查询结果
     */
    @PostMapping(value = "/search/file-share/user-id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<List<QueryElasticsearchShareUserIdResponseDTO>> shareUserIdSearch(@RequestBody QueryElasticsearchShareUserIdRequestDTO userIdSearchRequest);

    /**
     * 搜索es用户文件分享标识数据
     * 调用此接口时，必须传入用户标识，同时浏览量+1
     *
     * @param shareIdRequest 搜索es用户文件分享标识数据时的请求数据模型
     * @return 查询结果
     */
    @PostMapping(value = "/search/file-share/id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<QueryElasticsearchFileShareIdResponseDTO> shareIdResponse(@RequestBody QueryElasticsearchFileShareIdRequestDTO shareIdRequest);

}
