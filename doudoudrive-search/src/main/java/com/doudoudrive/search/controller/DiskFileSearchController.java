package com.doudoudrive.search.controller;

import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.request.*;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileIdResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.search.manager.DiskFileSearchManager;
import com.doudoudrive.search.model.convert.DiskFileModelConvert;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
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
 * <p>用户文件信息搜索服务控制层实现</p>
 * <p>2022-05-22 14:14</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
public class DiskFileSearchController {

    private DiskFileSearchManager diskFileSearchManager;

    private DiskFileModelConvert diskFileModelConvert;

    @Autowired
    public void setDiskFileSearchManager(DiskFileSearchManager diskFileSearchManager) {
        this.diskFileSearchManager = diskFileSearchManager;
    }

    @Autowired(required = false)
    public void setDiskFileModelConvert(DiskFileModelConvert diskFileModelConvert) {
        this.diskFileModelConvert = diskFileModelConvert;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "保存用户文件信息", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/search/file/save", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> saveElasticsearchDiskFile(@RequestBody @Valid SaveElasticsearchDiskFileRequestDTO requestDTO,
                                                    HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // es中保存用户文件信息
        diskFileSearchManager.saveDiskFile(diskFileModelConvert.saveElasticsearchDiskFileRequestConvertDiskFile(requestDTO));
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "删除用户文件信息", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/search/file/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<DeleteElasticsearchDiskFileResponseDTO> deleteElasticsearchDiskFile(@RequestBody @Valid DeleteElasticsearchDiskFileRequestDTO requestDTO,
                                                                                      HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);
        // 删除es中的数据
        ByQueryResponse deleteResponse = diskFileSearchManager.deleteDiskFile(requestDTO.getBusinessId());
        return Result.ok(DeleteElasticsearchDiskFileResponseDTO.builder()
                .deleted(deleteResponse.getDeleted())
                .took(deleteResponse.getTook())
                .build());
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "更新用户文件信息", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/search/file/update", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> updateElasticsearchDiskFile(@RequestBody @Valid UpdateElasticsearchDiskFileRequestDTO requestDTO,
                                                      HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);
        // 需要更新的用户信息数据模型
        DiskFileDTO diskFile = diskFileModelConvert.updateElasticsearchDiskFileRequestConvertDiskFile(requestDTO);
        // 构建es更新请求
        diskFileSearchManager.updateDiskFile(diskFile.getBusinessId(), diskFile);
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件信息查询", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/search/file", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<List<QueryElasticsearchDiskFileResponseDTO>> fileInfoSearch(@RequestBody @Valid QueryElasticsearchDiskFileRequestDTO requestDTO,
                                                                              HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 文件信息搜索请求，获取搜索结果
        List<SearchHit<DiskFileDTO>> searchHit = diskFileSearchManager.fileInfoSearch(requestDTO).getSearchHits();
        return Result.ok(diskFileModelConvert.diskFileDtoConvertQueryDiskFileResponse(searchHit));
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件ID查询", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/search/file/id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<QueryElasticsearchDiskFileIdResponseDTO> fileIdSearch(@RequestBody @Valid QueryElasticsearchDiskFileIdRequestDTO requestDTO,
                                                                        HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 文件ID批量搜索请求，获取搜索结果
        List<SearchHit<DiskFileDTO>> searchHit = diskFileSearchManager.fileIdSearch(requestDTO.getBusinessId()).getSearchHits();

        // 构建查询结果
        List<DiskFileModel> content = Lists.newArrayListWithExpectedSize(searchHit.size());
        for (SearchHit<DiskFileDTO> hit : searchHit) {
            content.add(diskFileModelConvert.diskFileDtoConvertDiskFileModel(hit.getContent()));
        }
        return Result.ok(QueryElasticsearchDiskFileIdResponseDTO.builder()
                .content(content)
                .build());
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件父级ID查询", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/search/file/parent-id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<List<QueryElasticsearchDiskFileResponseDTO>> fileParentIdSearch(@RequestBody @Valid QueryElasticsearchDiskFileParentIdRequestDTO requestDTO,
                                                                                  HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 文件父级业务标识批量搜索请求，获取搜索结果
        SearchHits<DiskFileDTO> searchHit = diskFileSearchManager.fileParentIdSearch(requestDTO.getUserId(),
                requestDTO.getParentId(), requestDTO.getCount(), requestDTO.getSearchAfter());
        return Result.ok(diskFileModelConvert.diskFileDtoConvertQueryDiskFileResponse(searchHit.getSearchHits()));
    }
}
