package com.doudoudrive.file.controller;

import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.FileAuthModel;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFileRequestDTO;
import com.doudoudrive.file.model.dto.request.CreateFolderRequestDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

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

    private DiskUserAttrService diskUserAttrService;

    private CacheManagerConfig cacheManagerConfig;

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

    @Autowired
    public void setDiskUserAttrService(DiskUserAttrService diskUserAttrService) {
        this.diskUserAttrService = diskUserAttrService;
    }

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
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
     * 创建文件接口作为对七牛云的回调接口，不对外开放
     *
     * @param requestDTO 创建文件时请求数据模型
     * @param request    请求对象
     * @param response   响应对象
     * @return 网盘文件数据模型
     */
    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件", businessType = "新建")
    @PostMapping(value = "/create-file", produces = "application/json;charset=UTF-8")
    public Result<DiskFileModel> createFile(@RequestBody @Valid CreateFileRequestDTO requestDTO,
                                            HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 待验证签名字符串，以 "QBox "作为起始字符
        String authorization = request.getHeader(ConstantConfig.HttpRequest.AUTHORIZATION);

        return null;
    }

    /**
     * 七牛云CDN鉴权专用
     * <p>
     * CDN请求时的文件鉴权配置，这里主要操作是扣减用户流量，流量不足时返回异常的状态码
     * <p>
     * 鉴权成功时响应 204 状态码，鉴权失败时响应 403 状态码
     *
     * @param sign 参数加密后的签名字符串
     */
    @SneakyThrows
    @OpLog(title = "文件鉴权", businessType = "回源鉴权")
    @RequestMapping(value = "/cdn-auth", produces = "application/json;charset=UTF-8", method = RequestMethod.HEAD)
    public void fileCdnAuth(String sign, HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 签名字符串不存在
        if (StringUtils.isBlank(sign)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 解密签名字符串
        FileAuthModel fileAuth = fileManager.decrypt(sign);
        if (fileAuth == null || StringUtils.isBlank(fileAuth.getUserId()) || StringUtils.isBlank(fileAuth.getFileId())) {
            // 签名解密失败
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 从缓存中获取当前登录的用户信息
        UserLoginResponseDTO userinfo = loginManager.getUserInfoToSession();
        if (userinfo == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 获取访问的文件对象
        DiskFile fileInfo = fileManager.getDiskFile(fileAuth.getUserId(), fileAuth.getFileId());
        if (fileInfo == null || fileInfo.getFileFolder() || fileInfo.getForbidden() || ConstantConfig.BooleanType.FALSE.equals(fileInfo.getStatus())) {
            // 文件不可访问
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 获取缓存中总流量、已用流量
        Map<String, String> userAttr = userinfo.getUserInfo().getUserAttr();
        String total = userAttr.get(ConstantConfig.UserAttrEnum.TOTAL_TRAFFIC.param);
        String usedTraffic = userAttr.get(ConstantConfig.UserAttrEnum.USED_TRAFFIC.param);
        if (StringUtils.isBlank(usedTraffic) || StringUtils.isBlank(total)) {
            // 配置异常、用户属性中没有对应配置
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 用户剩余流量为 总流量 - 已用流量
        BigDecimal usedTrafficBigDecimal = new BigDecimal(usedTraffic);
        BigDecimal remainingTraffic = new BigDecimal(total).subtract(usedTrafficBigDecimal);

        // 用户当前剩余流量 - 当前文件大小 与 0 比较
        BigDecimal fileSize = new BigDecimal(fileInfo.getFileSize());
        int surplus = remainingTraffic.subtract(fileSize).compareTo(BigDecimal.ZERO);
        if (surplus < NumberConstant.INTEGER_ZERO) {
            // 用户可用流量不足
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 原子性服务增加用户已用流量属性，判断更新结果
        Integer increase = diskUserAttrService.increase(userinfo.getUserInfo().getBusinessId(), ConstantConfig.UserAttrEnum.USED_TRAFFIC, fileInfo.getFileSize(), total);
        if (increase <= NumberConstant.INTEGER_ZERO) {
            if (NumberConstant.INTEGER_ZERO.equals(increase)) {
                // 不等于 -1 时需要更新用户缓存信息，获取最新的已用数据
                BigDecimal usedTrafficValue = diskUserAttrService.getDiskUserAttrValue(userinfo.getUserInfo().getBusinessId(), ConstantConfig.UserAttrEnum.USED_TRAFFIC);
                userAttr.put(ConstantConfig.UserAttrEnum.USED_TRAFFIC.param, usedTrafficValue.stripTrailingZeros().toPlainString());
                loginManager.attemptUpdateUserSession(userinfo.getToken(), userinfo.getUserInfo());
            }
            // 更新失败
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        // 服务更新成功时需要更新用户缓存信息
        userAttr.put(ConstantConfig.UserAttrEnum.USED_TRAFFIC.param, usedTrafficBigDecimal.add(fileSize).stripTrailingZeros().toPlainString());
        loginManager.attemptUpdateUserSession(userinfo.getToken(), userinfo.getUserInfo());

        // 响应成功的状态码
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
}