package com.doudoudrive.file.controller;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.request.DeleteFileInternalRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryExecutableTaskRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateExecutableTaskRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryExecutableTaskResponseDTO;
import com.doudoudrive.common.model.pojo.FileRecord;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.FileRecordManager;
import com.doudoudrive.file.model.convert.FileRecordConvert;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>文件系统内部接口专用服务控制层接口</p>
 * <p>2022-09-08 11:04</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/internal")
public class FileInternalController {

    private FileRecordManager fileRecordManager;

    private FileRecordConvert fileRecordConvert;

    private FileManager fileManager;

    @Autowired
    public void setFileRecordManager(FileRecordManager fileRecordManager) {
        this.fileRecordManager = fileRecordManager;
    }

    @Autowired(required = false)
    public void setFileRecordConvert(FileRecordConvert fileRecordConvert) {
        this.fileRecordConvert = fileRecordConvert;
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * 获取可执行的任务信息，用于定时任务
     * 查询条件：状态为等待执行的任务
     * 返回结果：任务信息列表
     *
     * @param requestDTO 获取可执行任务时的请求数据模型
     * @param request    请求对象
     * @param response   响应对象
     * @return Result<QueryExecutableTaskResponseDTO> 获取可执行任务时的响应数据模型
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/file-record/executable-task", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<QueryExecutableTaskResponseDTO> getExecutableTasks(@RequestBody @Valid QueryExecutableTaskRequestDTO requestDTO,
                                                                     HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 获取指定任务类型中待处理的任务
        FileRecord fileRecord = fileRecordManager.getFileRecordByAction(null, requestDTO.getAction(),
                ConstantConfig.FileRecordAction.ActionTypeEnum.TASK_BE_PROCESSED.status);

        if (fileRecord == null) {
            // 没有待处理的任务，响应空数据
            return Result.ok();
        }

        // 存在待处理的任务时，将任务记录状态改为处理中
        Integer updateByAction = fileRecordManager.updateFileRecordByAction(fileRecord.getBusinessId(), requestDTO.getAction(),
                ConstantConfig.FileRecordAction.ActionTypeEnum.TASK_BE_PROCESSED.status, null,
                ConstantConfig.FileRecordAction.ActionTypeEnum.TASK_PROCESSING.status);
        if (updateByAction == null || updateByAction <= NumberConstant.INTEGER_ZERO) {
            // 任务状态更新失败，响应空数据
            return Result.ok();
        }

        // 更新成功时，将任务记录转换为响应数据
        return Result.ok(fileRecordConvert.fileRecordConvertQueryFileRecordActionResponse(fileRecord));
    }

    /**
     * 更新任务执行结果，用于定时任务
     *
     * @param requestDTO 更新任务执行结果时的请求数据模型
     * @param request    请求对象
     * @param response   响应对象
     * @return Result<UpdateExecutableTaskResponseDTO> 更新任务执行结果时的响应数据模型
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/file-record/update-task", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> updateExecutableTasks(@RequestBody @Valid UpdateExecutableTaskRequestDTO requestDTO,
                                                HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 判断任务执行结果
        if (requestDTO.getSuccess()) {
            // 任务执行成功，删除本次任务记录数据
            fileRecordManager.delete(requestDTO.getBusinessId());
            return Result.ok();
        }

        // 任务执行失败，更新任务记录状态为待处理
        fileRecordManager.updateFileRecordByAction(requestDTO.getBusinessId(), requestDTO.getAction(),
                ConstantConfig.FileRecordAction.ActionTypeEnum.TASK_PROCESSING.status, null,
                ConstantConfig.FileRecordAction.ActionTypeEnum.TASK_BE_PROCESSED.status);
        return Result.ok();
    }

    /**
     * 内部接口，用于定时任务删除文件。
     *
     * @param requestDTO 批量删除文件或文件夹信息时的请求数据模型
     * @param request    请求对象
     * @param response   响应对象
     * @return Result<Integer> 批量删除文件或文件夹信息时的响应数据模型
     */
    @SneakyThrows
    @ResponseBody
    @PostMapping(value = "/file/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> deleteFileConsumer(@RequestBody @Valid DeleteFileInternalRequestDTO requestDTO,
                                             HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 需要删除的文件列表信息为空时，直接返回成功
        if (CollectionUtil.isEmpty(requestDTO.getContent())) {
            return Result.ok();
        }

        // 批量删除文件或文件夹信息
        fileManager.delete(requestDTO.getContent(), requestDTO.getUserId());
        return Result.ok();
    }
}
