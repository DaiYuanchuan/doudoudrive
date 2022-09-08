package com.doudoudrive.task.client;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.DeleteFileInternalRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryExecutableTaskRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateExecutableTaskRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryExecutableTaskResponseDTO;
import com.doudoudrive.common.util.http.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>用户文件信息服务Feign调用</p>
 * <p>2022-09-08 15:18</p>
 *
 * @author Dan
 **/
@FeignClient(name = "fileServer", contextId = "fileServerFeignClient")
public interface FileServerFeignClient {

    /**
     * 根据文件操作记录动作查询文件操作记录信息
     *
     * @param requestDTO 查询文件操作记录动作信息时的请求数据模型
     * @return 查询结果
     */
    @PostMapping(value = "/internal/file-record/executable-task", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<QueryExecutableTaskResponseDTO> getExecutableTasks(@RequestBody QueryExecutableTaskRequestDTO requestDTO);

    /**
     * 更新任务执行结果
     *
     * @param requestDTO 更新任务执行结果时的请求数据模型
     * @return 更新结果
     */
    @PostMapping(value = "/internal/file-record/update-task", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> updateExecutableTasks(@RequestBody UpdateExecutableTaskRequestDTO requestDTO);

    /**
     * 内部接口，用于定时任务删除文件。
     *
     * @param requestDTO 批量删除文件或文件夹信息时的请求数据模型
     * @return 删除结果
     */
    @PostMapping(value = "/internal/file/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    Result<String> deleteFileConsumer(@RequestBody DeleteFileInternalRequestDTO requestDTO);

}
