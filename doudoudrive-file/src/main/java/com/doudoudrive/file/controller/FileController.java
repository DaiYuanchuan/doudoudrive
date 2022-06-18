package com.doudoudrive.file.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.common.annotation.OpLog;
import com.doudoudrive.common.constant.*;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.convert.MqConsumerRecordConvert;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.FileAuthModel;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.common.model.pojo.RocketmqConsumerRecord;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.http.UrlQueryUtil;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.doudoudrive.commonservice.service.DiskUserService;
import com.doudoudrive.commonservice.service.RocketmqConsumerRecordService;
import com.doudoudrive.file.manager.DiskUserAttrManager;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.manager.OssFileManager;
import com.doudoudrive.file.manager.QiNiuManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFileConsumerRequestDTO;
import com.doudoudrive.file.model.dto.request.CreateFolderRequestDTO;
import com.doudoudrive.file.model.dto.request.FileSearchRequestDTO;
import com.doudoudrive.file.model.dto.request.FileUploadTokenRequestDTO;
import com.doudoudrive.file.model.dto.response.FileSearchResponseDTO;
import com.doudoudrive.file.model.dto.response.FileUploadTokenResponseDTO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

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

    private DiskUserAttrManager diskUserAttrManager;

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

    @Autowired
    public void setDiskUserAttrManager(DiskUserAttrManager diskUserAttrManager) {
        this.diskUserAttrManager = diskUserAttrManager;
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
     * @param request  请求对象
     * @param response 响应对象
     * @return 网盘文件数据模型
     */
    @SneakyThrows
    @OpLog(title = "文件", businessType = "新建", isSaveRequestData = false)
    @PostMapping(value = "/create-file", produces = "application/x-www-form-urlencoded")
    public Result<DiskFileModel> createFile(HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 获取原始签名字符串，以 "QBox "作为起始字符
        String originAuthorization = request.getHeader(ConstantConfig.HttpRequest.AUTHORIZATION);
        // 从流中获取到body原始请求数据
        String requestParam = IoUtil.read(request.getInputStream()).toString();
        if (log.isDebugEnabled()) {
            // 打印请求参数
            log.debug("Authorization : {}", originAuthorization);
            log.debug("param : {}", requestParam);
        }

        if (StringUtils.isBlank(originAuthorization) || StringUtils.isBlank(requestParam)) {
            return Result.build(StatusCodeEnum.FILE_AUTHENTICATION_FAILED);
        }

        // 获取七牛请求签名字符串
        String sign = qiNiuManager.signRequest(requestParam.getBytes(StandardCharsets.UTF_8), request.getContentType());
        if (!originAuthorization.equals(ConstantConfig.QiNiuConstant.QBOX_AUTHORIZATION_PREFIX + sign)) {
            return Result.build(StatusCodeEnum.FILE_AUTHENTICATION_FAILED);
        }

        // 获取七牛云配置信息
        QiNiuUploadConfig config = diskDictionaryService.getDictionary(DictionaryConstant.QI_NIU_CONFIG, QiNiuUploadConfig.class);

        // 获取回调时的浏览器UA标识
        String userAgent = request.getHeader(ConstantConfig.HttpRequest.USER_AGENT);
        if (StringUtils.isNotBlank(config.getQiNiuCallback()) && !config.getQiNiuCallback().equals(userAgent)) {
            return Result.build(StatusCodeEnum.FILE_AUTHENTICATION_FAILED);
        }

        // 将从流中获取到的body数据转为实体对象
        CreateFileAuthModel createFile = UrlQueryUtil.parse(requestParam, StandardCharsets.UTF_8, CreateFileAuthModel.class);
        if (createFile == null) {
            return Result.build(StatusCodeEnum.FILE_AUTHENTICATION_FAILED);
        }

        // 对象参数校验
        this.createFileParamVerify(createFile);

        // 获取一个文件标识
        String fileId = SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE);

        // 添加OSS文件对象存储(这个操作要放在前面)
        ossFileManager.insert(createFile, fileId);

        if (!createFile.getFileEtag().equals(createFile.getOriginalEtag())) {
            // 用户计算的etag和实际的etag不一致时，将云端文件重命名为正确的etag
            qiNiuManager.rename(createFile.getOriginalEtag(), createFile.getFileEtag());
        }

        // 校验用户id是否存在
        if (diskUserService.getDiskUser(createFile.getUserId()) == null) {
            return Result.build(StatusCodeEnum.USER_NO_EXIST);
        }

        // 文件的parentId校验机制
        fileManager.verifyParentId(createFile.getUserId(), createFile.getFileParentId());

        // 查找用户当前总容量、已经使用的磁盘容量
        BigDecimal totalDiskCapacity = diskUserAttrManager.getUserAttrValue(createFile.getToken(), createFile.getUserId(), ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY);
        BigDecimal usedDiskCapacity = diskUserAttrManager.getUserAttrValue(createFile.getToken(), createFile.getUserId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY);

        // 已经使用的磁盘容量 + 文件大小 与 用户当前总容量比较
        this.verifyCapacity(totalDiskCapacity, usedDiskCapacity, createFile.getFileSize());

        // 创建文件
        DiskFileModel fileModel = diskFileConvert.diskFileConvertDiskFileModel(fileManager.createFile(createFile, fileId, totalDiskCapacity, usedDiskCapacity));
        if (fileModel == null) {
            return Result.build(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 文件创建成功后的发送MQ消息
        this.sendMessage(CreateFileConsumerRequestDTO.builder()
                .fileId(fileId)
                .requestId(request.getHeader(ConstantConfig.QiNiuConstant.QI_NIU_CALLBACK_REQUEST_ID))
                .fileInfo(createFile)
                .build());

        // 构建文件鉴权模型，获取文件访问地址
        return Result.ok(fileManager.accessUrl(FileAuthModel.builder()
                .userId(createFile.getUserId())
                .build(), fileModel));
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "获取上传Token", businessType = "文件系统")
    @PostMapping(value = "/token", produces = "application/json;charset=UTF-8")
    public Result<FileUploadTokenResponseDTO> getUploadToken(@RequestBody @Valid FileUploadTokenRequestDTO tokenRequest,
                                                             HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 回调地址不为空时，校验回调Url的正确性
        if (StringUtils.isNotBlank(tokenRequest.getCallbackUrl()) && !ReUtil.isMatch(RegexConstant.URL_HTTP, tokenRequest.getCallbackUrl())) {
            return Result.build(StatusCodeEnum.URL_FORMAT_ERROR);
        }

        // 从缓存中获取当前登录的用户信息、用户token
        DiskUserModel userInfo = loginManager.getUserInfoToSessionException();
        String userToken = loginManager.getUserToken();

        // 查找用户当前总容量、已经使用的磁盘容量
        BigDecimal totalDiskCapacity = diskUserAttrManager.getUserAttrValue(userToken, userInfo.getBusinessId(), ConstantConfig.UserAttrEnum.TOTAL_DISK_CAPACITY);
        BigDecimal usedDiskCapacity = diskUserAttrManager.getUserAttrValue(userToken, userInfo.getBusinessId(), ConstantConfig.UserAttrEnum.USED_DISK_CAPACITY);

        // 已经使用的磁盘容量 + 文件大小 与 用户当前总容量比较
        this.verifyCapacity(totalDiskCapacity, usedDiskCapacity, tokenRequest.getFileSize());

        // 文件的parentId校验机制
        fileManager.verifyParentId(userInfo.getBusinessId(), tokenRequest.getFileParentId());

        // 根据传入的etag查找是否存在对应的文件信息
        OssFile ossFile = ossFileManager.getOssFile(tokenRequest.getFileEtag());
        if (ossFile != null) {
            // 生成一个文件id
            String fileId = SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE);

            // 创建文件
            DiskFile diskFile = fileManager.createFile(diskFileConvert.ossFileConvertCreateFileAuthModel(ossFile, userInfo.getBusinessId(),
                    tokenRequest.getName(), tokenRequest.getFileParentId()), fileId, totalDiskCapacity, usedDiskCapacity);
            // 类型转换
            DiskFileModel fileModel = diskFileConvert.diskFileConvertDiskFileModel(diskFile);
            if (fileModel == null) {
                return Result.build(StatusCodeEnum.SYSTEM_ERROR);
            }

            // 文件创建成功后的发送MQ消息
            this.sendMessage(CreateFileConsumerRequestDTO.builder()
                    .fileId(fileId)
                    .requestId(request.getHeader(ConstantConfig.QiNiuConstant.QI_NIU_CALLBACK_REQUEST_ID))
                    .fileInfo(diskFileConvert.diskFileConvertCreateFileAuthModel(diskFile))
                    .build());

            // 构建文件鉴权模型，拼接文件访问地址
            return Result.ok(FileUploadTokenResponseDTO.builder()
                    .fileInfo(fileManager.accessUrl(FileAuthModel.builder()
                            .userId(userInfo.getBusinessId())
                            .build(), diskFileConvert.diskFileConvertDiskFileModel(diskFile)))
                    .build());
        }

        // 生成七牛上传token
        return Result.ok(qiNiuManager.uploadToken(diskFileConvert.uploadTokenConvert(userInfo.getBusinessId(),
                tokenRequest.getFileParentId(), userToken, tokenRequest.getCallbackUrl(), tokenRequest.getFileEtag()), tokenRequest.getFileEtag()));
    }

    @SneakyThrows
    @ResponseBody
    @OpLog(title = "搜索", businessType = "文件系统")
    @PostMapping(value = "/search", produces = "application/json;charset=UTF-8")
    public Result<FileSearchResponseDTO> search(@RequestBody @Valid FileSearchRequestDTO requestDTO,
                                                HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 从缓存中获取当前登录的用户信息
        DiskUserModel userinfo = loginManager.getUserInfoToSessionException();

        // 构建ES文件查询请求
        QueryElasticsearchDiskFileRequestDTO queryElasticRequest = diskFileConvert.fileSearchRequestConvertQueryElasticRequest(requestDTO, userinfo.getBusinessId());
        return Result.ok(fileManager.search(queryElasticRequest, requestDTO.getMarker()));
    }

    /**
     * 七牛云CDN鉴权专用
     * <p>
     * CDN请求时的文件鉴权配置，这里主要操作是扣减用户流量，流量不足时返回异常的状态码
     * <p>
     * 鉴权成功时响应 204 状态码，鉴权失败时响应 403 状态码
     *
     * @param sign  参数加密后的签名字符串
     * @param path  当前文件访问路径，由七牛云CDN回调时带入
     * @param token 当前用户登录token串，用以支持使用参数登录
     */
    @SneakyThrows
    @OpLog(title = "文件鉴权", businessType = "回源鉴权")
    @RequestMapping(value = "/cdn-auth", produces = "application/json;charset=UTF-8", method = RequestMethod.HEAD)
    public void fileCdnAuth(String sign, String path, String token, HttpServletRequest request, HttpServletResponse response) {
        request.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=UTF-8");

        // 签名字符串不存在
        if (StringUtils.isBlank(sign) || StringUtils.isBlank(path)) {
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
        UserLoginResponseDTO userinfo = loginManager.getUserInfoToTokenSession(token);
        if (userinfo == null) {
            // 用户信息不存在
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

        // 校验从签名中获取到的文件信息与实际访问的文件信息是否一致
        if (!fileInfo.getFileEtag().equals(path.substring(NumberConstant.INTEGER_ONE))) {
            // 当前签名不适用于当前访问的文件
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
                userAttr.put(ConstantConfig.UserAttrEnum.USED_TRAFFIC.param, usedTrafficValue.add(fileSize).stripTrailingZeros().toPlainString());
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

    /**
     * 用户存储空间容量校验
     *
     * @param totalDiskCapacity 用户当前总容量
     * @param usedDiskCapacity  已经使用的磁盘容量
     * @param fileSize          文件大小
     */
    private void verifyCapacity(BigDecimal totalDiskCapacity, BigDecimal usedDiskCapacity, String fileSize) {
        // 已经使用的磁盘容量 + 文件大小 与 用户当前总容量比较
        if (usedDiskCapacity.add(new BigDecimal(fileSize)).compareTo(totalDiskCapacity) > NumberConstant.INTEGER_ZERO) {
            // 用户存储空间不足，不能入库
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SPACE_INSUFFICIENT);
        }
    }

    /**
     * 文件创建成功后对应的消息发送
     *
     * @param consumerRequest 创建文件时的消费者请求数据模型
     */
    private void sendMessage(CreateFileConsumerRequestDTO consumerRequest) {
        // 回调地址为空时，不做处理
        if (StringUtils.isBlank(consumerRequest.getFileInfo().getCallbackUrl())) {
            return;
        }
        // 使用async模式异步发送消息，消息发送后立刻返回，当消息完全完成发送后，会调用回调函数sendCallback来告知发送者本次发送是成功或者失败
        String destination = ConstantConfig.Topic.FILE_SERVICE + ConstantConfig.SpecialSymbols.ENGLISH_COLON + ConstantConfig.Tag.CREATE_FILE;
        rocketmqTemplate.asyncSend(destination, ObjectUtil.serialize(consumerRequest), new SendCallback() {
            /**
             * 消息发送成功时的回调
             * @param sendResult 消息发送状态
             */
            @Override
            public void onSuccess(SendResult sendResult) {
                // 构建RocketMQ消费记录
                RocketmqConsumerRecord consumerRecord = consumerRecordConvert.sendResultConvertConsumerRecord(sendResult, sendResult.getMessageQueue(), ConstantConfig.Tag.CREATE_FILE);
                rocketmqConsumerRecordService.insert(consumerRecord);
            }

            /**
             * 消息发送失败时的回调
             * @param throwable 异常消息
             */
            @Override
            public void onException(Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
            }
        });
    }

    /**
     * 对文件创建时参数进行校验，校验失败时抛出异常
     *
     * @param createFileAuthModel 创建文件时的鉴权参数模型
     */
    private void createFileParamVerify(CreateFileAuthModel createFileAuthModel) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Set<ConstraintViolation<CreateFileAuthModel>> validateResult = validatorFactory.getValidator().validate(createFileAuthModel);
            if (CollectionUtil.isNotEmpty(validateResult)) {
                BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.PARAM_INVALID, validateResult.stream()
                        .map(ConstraintViolation::getMessage)
                        .findAny().orElse(StatusCodeEnum.PARAM_INVALID.message));
            }
        }
    }
}
