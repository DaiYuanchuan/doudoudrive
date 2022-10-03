package com.doudoudrive.search.controller;

import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.FileShareModel;
import com.doudoudrive.common.model.dto.request.DeleteElasticsearchFileShareRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchFileShareIdRequestDTO;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchShareUserIdRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchFileShareRequestDTO;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchFileShareResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchFileShareIdResponseDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchShareUserIdResponseDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.search.manager.FileShareSearchManager;
import com.doudoudrive.search.model.convert.FileShareModelConvert;
import com.doudoudrive.search.model.elasticsearch.FileShareDTO;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>用户文件分享信息搜索服务控制层实现</p>
 * <p>2022-09-24 20:23</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
public class FileShareSearchController {

    private FileShareSearchManager fileShareSearchManager;

    private FileShareModelConvert fileShareModelConvert;

    @Autowired
    public void setFileShareSearchManager(FileShareSearchManager fileShareSearchManager) {
        this.fileShareSearchManager = fileShareSearchManager;
    }

    @Autowired(required = false)
    public void setFileShareModelConvert(FileShareModelConvert fileShareModelConvert) {
        this.fileShareModelConvert = fileShareModelConvert;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "保存用户文件分享信息", businessType = "ES文件分享查询服务")
    @PostMapping(value = "/search/file-share/save", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<String> saveElasticsearchFileShare(@RequestBody @Valid SaveElasticsearchFileShareRequestDTO requestDTO,
                                                     HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // es中保存用户文件分享信息
        fileShareSearchManager.createShare(fileShareModelConvert.saveEsFileShareRequestConvertFileShare(requestDTO));
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "删除文件分享信息", businessType = "ES文件分享查询服务")
    @PostMapping(value = "/search/file-share/delete", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<DeleteElasticsearchFileShareResponseDTO> cancelShare(@RequestBody @Valid DeleteElasticsearchFileShareRequestDTO cancelShareRequest,
                                                                       HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 删除es中的数据
        ByQueryResponse deleteResponse = fileShareSearchManager.cancelShare(cancelShareRequest.getShareId());
        return Result.ok(DeleteElasticsearchFileShareResponseDTO.builder()
                .deleted(deleteResponse.getDeleted())
                .took(deleteResponse.getTook())
                .build());
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件分享用户查询", businessType = "ES文件分享查询服务")
    @PostMapping(value = "/search/file-share/user-id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<List<QueryElasticsearchShareUserIdResponseDTO>> shareUserIdSearch(@RequestBody @Valid QueryElasticsearchShareUserIdRequestDTO userIdSearchRequest,
                                                                                    HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 文件信息搜索请求，获取搜索结果
        SearchHits<FileShareDTO> searchHit = fileShareSearchManager.shareUserIdSearch(userIdSearchRequest.getUserId(),
                userIdSearchRequest.getSearchAfter(), userIdSearchRequest.getCount());
        // 将搜索结果转换为响应结果
        return Result.ok(fileShareModelConvert.fileShareConvertQueryShareUserIdResponse(searchHit.getSearchHits()));
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "分享标识查询", businessType = "ES文件分享查询服务")
    @PostMapping(value = "/search/file-share/id", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<QueryElasticsearchFileShareIdResponseDTO> shareIdResponse(@RequestBody @Valid QueryElasticsearchFileShareIdRequestDTO shareIdRequest,
                                                                            HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 用户文件分享标识数据搜索请求，获取搜索结果
        List<SearchHit<FileShareDTO>> searchHit = fileShareSearchManager.shareIdSearch(shareIdRequest.getShareId()).getSearchHits();
        // 构建查询结果
        List<FileShareModel> content = Lists.newArrayListWithExpectedSize(searchHit.size());
        for (SearchHit<FileShareDTO> hit : searchHit) {
            // 将搜索结果转换为响应结果
            FileShareModel fileShareModel = fileShareModelConvert.fileShareConvertFileShareModel(hit.getContent());
            // 浏览次数自增
            if (shareIdRequest.getUpdateViewCount()) {
                fileShareModel.setViewCount(new BigDecimal(fileShareModel.getViewCount()).add(BigDecimal.ONE).stripTrailingZeros().toPlainString());
                // 更新es中的浏览次数
                fileShareSearchManager.updateFileShare(fileShareModel.getShareId(), FileShareDTO.builder()
                        .viewCount(fileShareModel.getViewCount())
                        .build());
            }
            content.add(fileShareModel);
        }

        // 将搜索结果转换为响应结果
        return Result.ok(QueryElasticsearchFileShareIdResponseDTO.builder()
                .content(content)
                .build());
    }
}
