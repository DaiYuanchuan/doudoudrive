package com.doudoudrive.file.controller;

import cn.hutool.core.util.ObjectUtil;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.FileAuthModel;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.doudoudrive.commonservice.service.DiskUserService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.OssFileManager;
import com.doudoudrive.file.manager.QiNiuManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFileConsumerRequestDTO;
import com.doudoudrive.file.model.dto.request.CreateFolderRequestDTO;
import com.doudoudrive.file.model.dto.request.FileUploadTokenRequestDTO;
import com.doudoudrive.file.model.dto.response.FileUploadTokenResponseDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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

    private QiNiuManager qiNiuManager;

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    /**
     * RocketMQ消息模型
     */
    private RocketMQTemplate rocketmqTemplate;

    private RocketmqConsumerRecordService rocketmqConsumerRecordService;

    private MqConsumerRecordConvert consumerRecordConvert;

    private DiskUserService diskUserService;

    private OssFileManager ossFileManager;

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
    public void setQiNiuManager(QiNiuManager qiNiuManager) {
        this.qiNiuManager = qiNiuManager;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    @Autowired
    public void setRocketmqTemplate(RocketMQTemplate rocketmqTemplate) {
        this.rocketmqTemplate = rocketmqTemplate;
    }

    @Autowired
    public void setRocketmqConsumerRecordService(RocketmqConsumerRecordService rocketmqConsumerRecordService) {
        this.rocketmqConsumerRecordService = rocketmqConsumerRecordService;
    }

    @Autowired(required = false)
    public void setConsumerRecordConvert(MqConsumerRecordConvert consumerRecordConvert) {
        this.consumerRecordConvert = consumerRecordConvert;
    }

    @Autowired
    public void setDiskUserService(DiskUserService diskUserService) {
        this.diskUserService = diskUserService;
    }

    @Autowired
    public void setOssFileManager(OssFileManager ossFileManager) {
        this.ossFileManager = ossFileManager;
    }

    /**
     * 七牛云请求鉴权的前缀(QBox)
     */
    private static final String QBOX_AUTHORIZATION_PREFIX = "QBox ";

    /**
     * 七牛云上传回调-文件名
     */
    private static final String QI_NIU_CALLBACK_FILE_NAME = "$(fname)";

    /**
     * 七牛云上传回调-文件大小
     */
    private static final String QI_NIU_CALLBACK_FILE_SIZE = "$(fsize)";

    /**
     * 七牛云上传回调-文件mime type
     */
    private static final String QI_NIU_CALLBACK_FILE_MIME_TYPE = "$(mimeType)";

    /**
     * 七牛云上传回调-文件etag
     */
    private static final String QI_NIU_CALLBACK_FILE_ETAG = "$(etag)";

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
     * @param createFile 创建文件时请求数据模型
     * @param request    请求对象
     * @param response   响应对象
     * @return 网盘文件数据模型
     */
    @SneakyThrows
    @ResponseBody
    @OpLog(title = "文件", businessType = "新建")
    @PostMapping(value = "/create-file", produces = "application/json;charset=UTF-8")
    public Result<DiskFileModel> createFile(@RequestBody @Valid CreateFileAuthModel createFile,
                                            HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 获取原始签名字符串，以 "QBox "作为起始字符
        String originAuthorization = request.getHeader(ConstantConfig.HttpRequest.AUTHORIZATION);
        if (StringUtils.isBlank(originAuthorization)) {
            return Result.build(StatusCodeEnum.FILE_AUTHENTICATION_FAILED);
        }

        // 获取七牛请求签名字符串
        String authorization = QBOX_AUTHORIZATION_PREFIX + qiNiuManager.signRequest(null, request.getContentType());
        if (!authorization.equals(originAuthorization)) {
            return Result.build(StatusCodeEnum.FILE_AUTHENTICATION_FAILED);
        }

        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);

        // 获取回调时的浏览器UA标识
        String userAgent = request.getHeader(ConstantConfig.HttpRequest.USER_AGENT);
        if (StringUtils.isNotBlank(config.getQiNiuCallback()) && !config.getQiNiuCallback().equals(userAgent)) {
            return Result.build(StatusCodeEnum.FILE_AUTHENTICATION_FAILED);
        }

        // 构建创建文件时的消费者请求数据模型
        CreateFileConsumerRequestDTO consumerRequest = CreateFileConsumerRequestDTO.builder()
                .fileId(SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE))
                .token(createFile.getToken())
                .fileInfo(createFile)
                .build();

        // 添加OSS文件对象存储(这个操作要放在前面)
        ossFileManager.insert(createFile, consumerRequest.getFileId());

        // 校验用户id是否存在
        if (diskUserService.getDiskUser(createFile.getUserId()) == null) {
            return Result.build(StatusCodeEnum.USER_NO_EXIST);
        }

        // 文件的parentId校验机制
        fileManager.verifyParentId(createFile.getUserId(), createFile.getFileParentId());

        // 查找用户当前总容量、已经使用的磁盘容量
        BigDecimal totalDiskCapacity, usedDiskCapacity;

        // 尝试通过token获取用户信息
        DiskUserModel diskUserModel = loginManager.getUserInfoToToken(createFile.getToken());
        if (diskUserModel != null) {
            // 获取用户属性缓存Map
            Map<String, String> userAttr = diskUserModel.getUserAttr();
            // 查找用户当前总容量、已经使用的磁盘容量
            totalDiskCapacity = new BigDecimal(userAttr.get(ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY.param));
            usedDiskCapacity = new BigDecimal(userAttr.get(ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY.param));
        } else {
            // 无法从缓存中获取时，从数据库中查询
            totalDiskCapacity = diskUserAttrService.getDiskUserAttrValue(createFile.getUserId(), ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY);
            usedDiskCapacity = diskUserAttrService.getDiskUserAttrValue(createFile.getUserId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY);
        }

        // 已经使用的磁盘容量 + 文件大小 与 用户当前总容量比较
        if (usedDiskCapacity.add(new BigDecimal(createFile.getFileSize())).compareTo(totalDiskCapacity) > NumberConstant.INTEGER_ZERO) {
            // 用户存储空间不足，不能入库
            return Result.build(StatusCodeEnum.SPACE_INSUFFICIENT);
        }

        // 使用sync模式同步发送消息，在消息完全发送完成之后返回结果
        String destination = ConstantConfig.Topic.FILE_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.CREATE_FILE;
        SendResult sendResult = rocketmqTemplate.syncSend(destination, ObjectUtil.serialize(consumerRequest));

        // 构建RocketMQ消费记录
        RocketmqConsumerRecord consumerRecord = consumerRecordConvert.sendResultConvertConsumerRecord(sendResult, sendResult.getMessageQueue(), ConstantConfig.Tag.CREATE_FILE);
        rocketmqConsumerRecordService.insert(consumerRecord);

        // TODO: 2022/5/25 需要拼接资源文件的访问地址
        return Result.ok(diskFileConvert.createFileAuthConvertDiskFileModel(createFile, consumerRequest.getFileId()));
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "获取上传Token", businessType = "文件系统")
    @PostMapping(value = "/token", produces = "application/json;charset=UTF-8")
    public Result<FileUploadTokenResponseDTO> getUploadToken(@RequestBody @Valid FileUploadTokenRequestDTO tokenRequest,
                                                             HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 从缓存中获取当前登录的用户信息
        UserLoginResponseDTO userLoginResponse = loginManager.getUserInfoToSession();
        // 无法获取用户信息时
        if (userLoginResponse == null || userLoginResponse.getUserInfo() == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.INVALID_USERINFO);
        }

        // 获取用户属性缓存Map
        Map<String, String> userAttr = userLoginResponse.getUserInfo().getUserAttr();

        // 查找用户当前总容量、已经使用的磁盘容量
        BigDecimal totalDiskCapacity = new BigDecimal(userAttr.get(ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY.param));
        BigDecimal usedDiskCapacity = new BigDecimal(userAttr.get(ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY.param));

        // 已经使用的磁盘容量 + 文件大小 与 用户当前总容量比较
        if (usedDiskCapacity.add(new BigDecimal(tokenRequest.getFileSize())).compareTo(totalDiskCapacity) > NumberConstant.INTEGER_ZERO) {
            // 用户存储空间不足，不能入库
            return Result.build(StatusCodeEnum.SPACE_INSUFFICIENT);
        }

        // 文件的parentId校验机制
        fileManager.verifyParentId(userLoginResponse.getUserInfo().getBusinessId(), tokenRequest.getFileParentId());

        // 根据传入的etag查找是否存在对应的文件信息
        OssFile ossFile = ossFileManager.getOssFile(tokenRequest.getFileEtag());
        if (ossFile != null) {
            // 构建一个文件实体信息
            DiskFile diskFile = diskFileConvert.ossFileConvertDiskFile(ossFile, userLoginResponse.getUserInfo().getBusinessId(),
                    tokenRequest.getName(), tokenRequest.getFileParentId());

            // 文件名重复校验机制，针对同一个目录下的文件名称重复性校验
            if (fileManager.verifyRepeat(diskFile.getFileName(), diskFile.getUserId(), diskFile.getFileParentId(), Boolean.FALSE)) {
                return Result.build(StatusCodeEnum.FILE_NAME_REPEAT);
            }

            // 构建创建文件时的消费者请求数据模型
            CreateFileConsumerRequestDTO consumerRequest = CreateFileConsumerRequestDTO.builder()
                    .fileId(SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE))
                    .token(userLoginResponse.getToken())
                    .fileInfo(diskFileConvert.diskFileConvertCreateFileAuthModel(diskFile))
                    .build();

            // 使用sync模式同步发送消息，在消息完全发送完成之后返回结果
            String destination = ConstantConfig.Topic.FILE_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.CREATE_FILE;
            SendResult sendResult = rocketmqTemplate.syncSend(destination, ObjectUtil.serialize(consumerRequest));

            // 构建RocketMQ消费记录
            RocketmqConsumerRecord consumerRecord = consumerRecordConvert.sendResultConvertConsumerRecord(sendResult, sendResult.getMessageQueue(), ConstantConfig.Tag.CREATE_FILE);
            rocketmqConsumerRecordService.insert(consumerRecord);

            // TODO: 2022/5/26 需要拼接资源文件的访问地址
            return Result.ok(FileUploadTokenResponseDTO.builder()
                    .fileInfo(diskFileConvert.diskFileConvertDiskFileModel(diskFile))
                    .build());
        }

        // 构建创建文件时的鉴权参数模型
        CreateFileAuthModel createFileAuthModel = CreateFileAuthModel.builder()
                .userId(userLoginResponse.getUserInfo().getBusinessId())
                .name(QI_NIU_CALLBACK_FILE_NAME)
                .fileParentId(tokenRequest.getFileParentId())
                .fileSize(QI_NIU_CALLBACK_FILE_SIZE)
                .fileMimeType(QI_NIU_CALLBACK_FILE_MIME_TYPE)
                .fileEtag(QI_NIU_CALLBACK_FILE_ETAG)
                .token(userLoginResponse.getToken())
                .build();

        // 生成七牛上传token
        return Result.ok(qiNiuManager.uploadToken(createFileAuthModel, tokenRequest.getFileEtag()));
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
        FileAuthModel fileAuth = fileManager.decrypt(sign, FileAuthModel.class);
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

        // TODO: 2022/5/25 资源所属用户与当前登录用户不同时，需要校验分享短链、分享时的文件key值

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
