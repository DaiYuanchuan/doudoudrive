package com.doudoudrive.file.controller;

import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFolderRequestDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>用户文件模块控制层接口</p>
 * <p>2022-05-21 17:12</p>
 *
 * @author Dan
 **/
@Slf4j
@Validated
@RestController
@RequestMapping(value = "/file")
public class FileController {

    private LoginManager loginManager;

    private FileManager fileManager;

    private DiskFileConvert diskFileConvert;

    @Autowired
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired(required = false)
    public void setDiskFileConvert(DiskFileConvert diskFileConvert) {
        this.diskFileConvert = diskFileConvert;
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件夹", businessType = "新建")
    @PostMapping(value = "/create-folder", produces = "application/json;charset=UTF-8")
    public Result<DiskFileModel> createFolder(@RequestBody @Valid CreateFolderRequestDTO requestDTO,
                                              HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 从缓存中获取当前登录的用户信息
        DiskUserModel userinfo = loginManager.getUserInfoToSessionException();

        // 文件的parentId校验机制
        fileManager.verifyParentId(userinfo.getBusinessId(), requestDTO.getParentId());

        // 文件名重复校验机制
        if (fileManager.verifyRepeat(requestDTO.getName(), userinfo.getBusinessId(), requestDTO.getParentId(), Boolean.TRUE)) {
            return Result.build(StatusCodeEnum.FILE_NAME_REPEAT);
        }

        // 创建文件夹
        DiskFile diskFile = fileManager.createFolder(userinfo.getBusinessId(), requestDTO.getName(), requestDTO.getParentId());

        return Result.ok(diskFileConvert.diskFileConvertDiskFileModel(diskFile));
    }

    /**
     * 七牛云CDN鉴权专用
     * <p>
     * CDN请求时的文件鉴权配置，这里主要操作是扣减用户流量，流量不足时返回异常的状态码
     * <p>
     * 鉴权成功时响应 200 状态码，鉴权失败时响应 403 状态码
     */
    @SneakyThrows
    @OpLog(title = "文件鉴权", businessType = "回源鉴权")
    @RequestMapping(value = "/cdn-auth", produces = "application/json;charset=UTF-8", method = RequestMethod.HEAD)
    public void fileCdnAuth(HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 从缓存中获取当前登录的用户信息
        UserLoginResponseDTO userinfo = loginManager.getUserInfoToSession();
        if (userinfo != null) {
            // 获取访问的文件对象
            //fileManager.getDiskFile();
            // 原子性服务更新userAttr，判断更新结果
            // UPDATE `cloud-user`.`disk_user_attr_28` SET `attribute_value` = `attribute_value` - '100' WHERE `user_id` = '22040620322416492483446660399361827' AND `attribute_name` = 'usedDiskCapacity' AND `attribute_value` >= 0;

            return;
        }

        response.setStatus(HttpStatus.FORBIDDEN.value());
    }
}
