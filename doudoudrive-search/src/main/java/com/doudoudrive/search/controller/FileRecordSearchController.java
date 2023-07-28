package com.doudoudrive.search.controller;

import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.request.BatchSaveElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.request.DeleteElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchFileRecordRequestDTO;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchFileRecordResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.search.manager.FileRecordSearchManager;
import com.doudoudrive.search.model.convert.FileRecordModelConvert;
import com.doudoudrive.search.model.elasticsearch.FileRecordDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>文件临时操作记录信息搜索服务控制层实现</p>
 * <p>2023-07-28 11:00</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
public class FileRecordSearchController {

    private FileRecordSearchManager fileRecordSearchManager;
    private FileRecordModelConvert fileRecordModelConvert;

    @Autowired
    public void setFileRecordSearchManager(FileRecordSearchManager fileRecordSearchManager) {
        this.fileRecordSearchManager = fileRecordSearchManager;
    }

    @Autowired(required = false)
    public void setFileRecordModelConvert(FileRecordModelConvert fileRecordModelConvert) {
        this.fileRecordModelConvert = fileRecordModelConvert;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "保存文件操作记录", businessType = "ES文件操作记录查询服务")
    @PostMapping(value = "/search/file-record/save", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> saveFileRecord(@RequestBody @Valid BatchSaveElasticsearchFileRecordRequestDTO requestDTO,
                                         HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // es中保存文件操作记录信息
        fileRecordSearchManager.saveFileRecord(fileRecordModelConvert.saveFileRecordConvertFileRecord(requestDTO.getFileRecordInfo()));
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "删除文件操作记录", businessType = "ES文件操作记录查询服务")
    @PostMapping(value = "/search/file-record/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<DeleteElasticsearchResponseDTO> deleteFileRecord(@RequestBody @Valid DeleteElasticsearchFileRecordRequestDTO requestDTO,
                                                                   HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 删除es中文件操作记录数据
        ByQueryResponse deleteResponse = fileRecordSearchManager.deleteAction(requestDTO.getUserId(),
                requestDTO.getAction(), requestDTO.getActionType(), requestDTO.getEtag());
        return Result.ok(DeleteElasticsearchResponseDTO.builder()
                .deleted(deleteResponse.getDeleted())
                .took(deleteResponse.getTook())
                .build());
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件操作记录查询", businessType = "ES文件操作记录查询服务")
    @PostMapping(value = "/search/file-record", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<List<QueryElasticsearchFileRecordResponseDTO>> fileRecordSearch(@RequestBody @Valid QueryElasticsearchFileRecordRequestDTO requestDTO,
                                                                                  HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 文件ID批量搜索请求，获取搜索结果
        SearchHits<FileRecordDTO> searchHit = fileRecordSearchManager.fileRecordSearch(requestDTO.getUserId(),
                requestDTO.getAction(), requestDTO.getActionType(), requestDTO.getEtag());

        // 构建查询结果
        return Result.ok(fileRecordModelConvert.fileRecordConvertQueryFileRecordResponse(searchHit.getSearchHits()));
    }
}
