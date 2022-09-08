package com.doudoudrive.task.job;

import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.model.dto.model.FileRecordModel;
import com.doudoudrive.common.model.dto.request.DeleteFileInternalRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileParentIdRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryExecutableTaskRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateExecutableTaskRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryExecutableTaskResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.task.client.FileServerFeignClient;
import com.doudoudrive.task.client.SearchServerFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>文件操作相关定时任务处理程序（Bean模式）</p>
 * <p>2022-08-30 19:30</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class FileJobHandler {

    private FileServerFeignClient fileServerFeignClient;

    private SearchServerFeignClient searchServerFeignClient;

    @Autowired
    public void setFileServerFeignClient(FileServerFeignClient fileServerFeignClient) {
        this.fileServerFeignClient = fileServerFeignClient;
    }

    @Autowired
    public void setSearchServerFeignClient(SearchServerFeignClient searchServerFeignClient) {
        this.searchServerFeignClient = searchServerFeignClient;
    }

    /**
     * 每秒查询文件删除任务待处理的记录，将任务记录状态改为处理中，然后执行删除操作
     * 配置定时任务 ，每秒执行(cron = * * * * * ?)
     */
    @XxlJob(value = "deleteFileJobHandler")
    public ReturnT<String> deleteFileJobHandler() {
        // 查询文件删除任务待处理的记录
        Result<QueryExecutableTaskResponseDTO> specificAction = fileServerFeignClient.getExecutableTasks(QueryExecutableTaskRequestDTO.builder()
                .action(ConstantConfig.FileRecordAction.ActionEnum.DELETE.status)
                .build());
        // 打印请求结果
        XxlJobHelper.log(JSONObject.toJSONString(specificAction));
        if (Result.isNotSuccess(specificAction)) {
            BusinessExceptionUtil.throwBusinessException(specificAction);
            return ReturnT.FAIL;
        }

        if (specificAction.getData() == null || specificAction.getData().getContent() == null) {
            // 没有文件删除任务待处理的记录
            return ReturnT.SUCCESS;
        }

        // 文件删除任务待处理的任务记录
        FileRecordModel fileRecord = specificAction.getData().getContent();

        // 文件删除请求处理结果
        List<Result<String>> deleteFileResultList = new ArrayList<>();

        // 通过递归获取指定父目录下的所有文件信息
        this.getAllFileInfo(fileRecord.getUserId(), fileRecord.getFileId(), null, queryParentIdResponse -> {
            // 批量删除文件信息
            deleteFileResultList.add(fileServerFeignClient.deleteFileConsumer(DeleteFileInternalRequestDTO.builder()
                    .userId(fileRecord.getUserId())
                    .content(queryParentIdResponse.stream()
                            .map(QueryElasticsearchDiskFileResponseDTO::getContent)
                            .toList())
                    .build()));
        });

        // 获取本次任务处理结果，true表示全部成功，false表示有失败的
        boolean result = deleteFileResultList.stream().noneMatch(Result::isNotSuccess);
        // 更新任务执行结果
        fileServerFeignClient.updateExecutableTasks(UpdateExecutableTaskRequestDTO.builder()
                .businessId(fileRecord.getBusinessId())
                .action(ConstantConfig.FileRecordAction.ActionEnum.DELETE.status)
                .success(result)
                .build());

        return result ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    /**
     * 获取指定父目录下的所有文件信息
     *
     * @param userId      用户系统内唯一标识
     * @param fileId      文件系统内唯一标识
     * @param searchAfter 上次查询的最后一个节点的id
     * @param consumer    回调函数
     */
    private void getAllFileInfo(String userId, String fileId,
                                List<Object> searchAfter, Consumer<List<QueryElasticsearchDiskFileResponseDTO>> consumer) {
        // 根据文件父级业务标识批量查询用户文件信息
        QueryElasticsearchDiskFileParentIdRequestDTO queryParentIdRequest = QueryElasticsearchDiskFileParentIdRequestDTO.builder()
                .userId(userId)
                .parentId(Collections.singletonList(fileId))
                .count(NumberConstant.INTEGER_ONE_THOUSAND)
                .searchAfter(searchAfter)
                .build();
        Result<List<QueryElasticsearchDiskFileResponseDTO>> queryParentIdResp = searchServerFeignClient.fileParentIdSearch(queryParentIdRequest);
        if (Result.isNotSuccess(queryParentIdResp) || CollectionUtil.isEmpty(queryParentIdResp.getData())) {
            // 查询失败 或者 查询结果为空时，结束当前递归
            return;
        }

        // 进行任务的回调，执行回调函数
        consumer.accept(queryParentIdResp.getData());

        // 获取最后一个节点的id
        int index = queryParentIdResp.getData().size() - NumberConstant.INTEGER_ONE;
        List<Object> lastSearchAfter = queryParentIdResp.getData().get(index).getSortValues();
        if (CollectionUtil.isNotEmpty(lastSearchAfter)) {
            // 递归继续翻页查询
            this.getAllFileInfo(userId, fileId, lastSearchAfter, consumer);
        }
    }
}
