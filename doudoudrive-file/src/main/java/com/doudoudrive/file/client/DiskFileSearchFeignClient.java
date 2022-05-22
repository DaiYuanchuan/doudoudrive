package com.doudoudrive.file.client;

import com.doudoudrive.common.model.dto.request.DeleteElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    @PostMapping(value = "/search/file/save", produces = "application/json;charset=UTF-8")
    Result<String> saveElasticsearchDiskFile(@RequestBody SaveElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 删除es中保存的用户文件信息
     *
     * @param requestDTO 删除es用户文件信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file/delete", produces = "application/json;charset=UTF-8")
    Result<String> deleteElasticsearchDiskFile(@RequestBody DeleteElasticsearchDiskFileRequestDTO requestDTO);

    /**
     * 更新es中保存的用户文件信息
     *
     * @param requestDTO 更新es用户文件信息时的请求数据模型
     * @return 通用状态返回类
     */
    @PostMapping(value = "/search/file/update", produces = "application/json;charset=UTF-8")
    Result<String> updateElasticsearchDiskFile(@RequestBody UpdateElasticsearchDiskFileRequestDTO requestDTO);

}
