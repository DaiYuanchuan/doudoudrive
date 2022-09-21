package com.doudoudrive.task.client;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileParentIdRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>信息搜索服务Feign调用</p>
 * <p>2022-09-08 09:52</p>
 *
 * @author Dan
 **/
@FeignClient(name = "searchServer", contextId = "searchServerFeignClient")
public interface SearchServerFeignClient {

    /**
     * 根据文件父级业务标识批量查询用户文件信息
     *
     * @param requestDTO 查询文件父级业务标识信息时的请求数据模型
     * @return 查询结果
     */
    @PostMapping(value = "/search/file/parent-id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<List<QueryElasticsearchDiskFileResponseDTO>> fileParentIdSearch(@RequestBody QueryElasticsearchDiskFileParentIdRequestDTO requestDTO);

}
