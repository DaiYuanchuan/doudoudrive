package com.doudoudrive.file.controller;

import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.AuthorizationCodeConstant;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchFileShareResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.FileShareManager;
import com.doudoudrive.file.model.dto.request.CancelFileShareRequestDTO;
import com.doudoudrive.file.model.dto.request.CreateFileShareRequestDTO;
import com.doudoudrive.file.model.dto.request.FileShareAnonymousRequestDTO;
import com.doudoudrive.file.model.dto.response.CreateFileShareResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareAnonymousResponseDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * <p>用户文件分享记录模块控制层接口</p>
 * <p>2022-09-24 02:14</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/file/share")
public class FileShareController {

    private LoginManager loginManager;

    private FileManager fileManager;

    private FileShareManager fileShareManager;

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setFileShareManager(FileShareManager fileShareManager) {
        this.fileShareManager = fileShareManager;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件分享", businessType = "新建")
    @RequiresPermissions(value = AuthorizationCodeConstant.FILE_SHARE)
    @PostMapping(value = "/create", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<CreateFileShareResponseDTO> createShare(@RequestBody @Valid CreateFileShareRequestDTO createFileShareRequest,
                                                          HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 过期时间不为空时判断是否在当前时间之后
        if (createFileShareRequest.getExpiration() != null && new Date().after(createFileShareRequest.getExpiration())) {
            // 过期时间在当前时间之前
            return Result.build(StatusCodeEnum.EXPIRE_TIME_INVALID);
        }

        // 提取码不为空时判断是否为6位数字
        if (StringUtils.isNotBlank(createFileShareRequest.getSharePwd())
                && createFileShareRequest.getSharePwd().length() != NumberConstant.INTEGER_SIX) {
            // 分享密码长度小于6位
            return Result.build(StatusCodeEnum.SHARE_PWD_LENGTH_INVALID);
        }

        // 从缓存中获取当前登录的用户信息
        DiskUserModel userinfo = loginManager.getUserInfoToSessionException();

        // 根据文件标识查询需要分享的文件信息
        List<DiskFile> shareFileList = fileManager.fileIdSearch(userinfo.getBusinessId(), createFileShareRequest.getFiles());
        if (CollectionUtil.isEmpty(shareFileList)) {
            // 文件不存在
            return Result.build(StatusCodeEnum.FILE_NOT_FOUND);
        }

        // 创建分享记录，返回分享记录标识
        return Result.ok(fileShareManager.createShare(userinfo.getBusinessId(), createFileShareRequest, shareFileList));
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件分享", businessType = "删除")
    @PostMapping(value = "/cancel", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<DeleteElasticsearchFileShareResponseDTO> cancelShare(@RequestBody @Valid CancelFileShareRequestDTO cancelShareRequest,
                                                                       HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 从缓存中获取当前登录的用户信息
        DiskUserModel userinfo = loginManager.getUserInfoToSessionException();
        // 取消文件分享链接
        return Result.ok(fileShareManager.cancelShare(cancelShareRequest.getShareId(), userinfo));
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "匿名访问分享链接", businessType = "查询")
    @PostMapping(value = "/anonymous", produces = ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8)
    public Result<FileShareAnonymousResponseDTO> anonymous(@RequestBody @Valid FileShareAnonymousRequestDTO anonymousRequest,
                                                           HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding(ConstantConfig.HttpRequest.UTF8);
        response.setContentType(ConstantConfig.HttpRequest.CONTENT_TYPE_JSON_UTF8);

        // 提取码不为空时判断是否为6位数字
        if (StringUtils.isNotBlank(anonymousRequest.getSharePwd())
                && anonymousRequest.getSharePwd().length() != NumberConstant.INTEGER_SIX) {
            // 分享密码长度小于6位
            return Result.build(StatusCodeEnum.SHARE_PWD_LENGTH_INVALID);
        }

        // 根据分享链接的唯一标识获取分享链接的详细信息，包括分享的文件列表
        return fileShareManager.anonymous(anonymousRequest);
    }
}
