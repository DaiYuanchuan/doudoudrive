package com.doudoudrive.search.controller;

import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.model.dto.request.DeleteElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.request.UpdateElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.search.manager.DiskFileSearchManager;
import com.doudoudrive.search.model.convert.DiskFileModelConvert;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>用户文件信息搜索服务控制层实现</p>
 * <p>2022-05-22 14:14</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/search/file")
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
    @PostMapping(value = "/save", produces = "application/json;charset=UTF-8")
    public Result<String> saveElasticsearchDiskFile(@RequestBody @Valid SaveElasticsearchDiskFileRequestDTO requestDTO,
                                                    HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // es中保存用户文件信息
        diskFileSearchManager.saveDiskFile(diskFileModelConvert.saveElasticsearchDiskFileRequestConvertDiskFile(requestDTO));
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "删除用户文件信息", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/delete", produces = "application/json;charset=UTF-8")
    public Result<String> deleteElasticsearchDiskFile(@RequestBody @Valid DeleteElasticsearchDiskFileRequestDTO requestDTO,
                                                      HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        // 删除es中的数据
        diskFileSearchManager.deleteDiskFile(requestDTO.getBusinessId());
        return Result.ok();
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "更新用户文件信息", businessType = "ES用户文件信息查询服务")
    @PostMapping(value = "/update", produces = "application/json;charset=UTF-8")
    public Result<String> updateElasticsearchDiskFile(@RequestBody @Valid UpdateElasticsearchDiskFileRequestDTO requestDTO,
                                                      HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");
        // 需要更新的用户信息数据模型
        DiskFileDTO diskFile = diskFileModelConvert.updateElasticsearchDiskFileRequestConvertDiskFile(requestDTO);
        // 构建es更新请求
        diskFileSearchManager.updateDiskFile(diskFile.getBusinessId(), diskFile);
        return Result.ok();
    }
}
