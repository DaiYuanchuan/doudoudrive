package com.doudoudrive.file.manager.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSON;
import com.doudoudrive.common.cache.CacheManagerConfig;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.DictionaryConstant;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.FileAuthModel;
import com.doudoudrive.common.model.dto.request.SaveElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.service.DiskDictionaryService;
import com.doudoudrive.commonservice.service.DiskFileService;
import com.doudoudrive.file.client.DiskFileSearchFeignClient;
import com.doudoudrive.file.manager.FileManager;
import com.doudoudrive.file.model.convert.DiskFileConvert;
import com.doudoudrive.file.model.dto.request.CreateFileRequestDTO;
import io.netty.util.concurrent.FastThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * <p>用户文件信息服务的通用业务处理层接口实现</p>
 * <p>2022-05-21 17:50</p>
 *
 * @author Dan
 **/
@Scope("singleton")
@Service("fileManager")
public class FileManagerImpl implements FileManager {

    private CacheManagerConfig cacheManagerConfig;

    private DiskFileService diskFileService;

    private DiskFileConvert diskFileConvert;

    private DiskFileSearchFeignClient diskFileSearchFeignClient;

    /**
     * 数据字典模块服务
     */
    private DiskDictionaryService diskDictionaryService;

    @Autowired
    public void setCacheManagerConfig(CacheManagerConfig cacheManagerConfig) {
        this.cacheManagerConfig = cacheManagerConfig;
    }

    @Autowired
    public void setDiskFileService(DiskFileService diskFileService) {
        this.diskFileService = diskFileService;
    }

    @Autowired(required = false)
    public void setDiskFileConvert(DiskFileConvert diskFileConvert) {
        this.diskFileConvert = diskFileConvert;
    }

    @Autowired
    public void setDiskFileSearchFeignClient(DiskFileSearchFeignClient diskFileSearchFeignClient) {
        this.diskFileSearchFeignClient = diskFileSearchFeignClient;
    }

    @Autowired
    public void setDiskDictionaryService(DiskDictionaryService diskDictionaryService) {
        this.diskDictionaryService = diskDictionaryService;
    }

    /**
     * 重置文件名时需要使用到的日期格式
     */
    private static final String YUMMY_HMS = "_HHmmssSSS_";

    /**
     * 文件名字段长度
     */
    private static final Integer FILE_NAME_LENGTH = NumberConstant.INTEGER_EIGHT * NumberConstant.INTEGER_TEN;

    /**
     * 当前
     */
    private static final byte[] CIPHER = "VP+EcBOmZHGkTT0vZfeSHg==".getBytes(StandardCharsets.UTF_8);

    /**
     * 对称加密对象本地缓存线程
     */
    private static final FastThreadLocal<SymmetricCrypto> SYMMETRIC_CRYPTO_CACHE = new FastThreadLocal<>();

    /**
     * 创建文件夹
     *
     * @param userId   用户标识
     * @param name     文件夹名称
     * @param parentId 文件父级标识
     * @return 用户文件模块信息
     */
    @Override
    public DiskFile createFolder(String userId, String name, String parentId) {
        // 构建一个文件夹实体信息
        DiskFile diskFile = diskFileConvert.createFolderConvertDiskFile(userId, name, parentId);
        // 保存用户文件夹
        diskFileService.insert(diskFile);
        // 文件数据类型转换
        SaveElasticsearchDiskFileRequestDTO requestDTO = diskFileConvert.diskFileConvertSaveElasticsearchDiskFileRequest(diskFile);
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskFile.getUserId(), ConstantConfig.TableSuffix.DISK_FILE);
        requestDTO.setTableSuffix(tableSuffix);
        // 用户文件信息先入库，然后入es
        Result<String> saveElasticsearchResult = diskFileSearchFeignClient.saveElasticsearchDiskFile(requestDTO);
        if (Result.isNotSuccess(saveElasticsearchResult)) {
            BusinessExceptionUtil.throwBusinessException(saveElasticsearchResult);
        }
        return diskFile;
    }

    /**
     * 创建文件
     *
     * @param createFileRequest 创建文件时请求数据模型
     * @return 用户文件模块信息
     */
    @Override
    public DiskFile createFile(CreateFileRequestDTO createFileRequest) {
        return null;
    }

    /**
     * 根据业务标识查找指定用户下的文件信息，优先从缓存中查找
     *
     * @param userId     指定的用户标识
     * @param businessId 需要查询的文件标识
     * @return 用户文件模块信息
     */
    @Override
    public DiskFile getDiskFile(String userId, String businessId) {
        if (true){
            return DiskFile.builder()
                    .fileSize("1024")
                    .fileFolder(false)
                    .forbidden(false)
                    .status(ConstantConfig.BooleanType.TRUE)
                    .build();
        }
        // 构建的缓存key
        String cacheKey = ConstantConfig.Cache.DISK_FILE_CACHE + businessId;
        // 从缓存中获取用户文件信息
        DiskFile diskFile = cacheManagerConfig.getCache(cacheKey);
        if (diskFile != null) {
            return diskFile;
        }
        // 从数据库中获取用户文件信息
        diskFile = diskFileService.getDiskFile(userId, businessId);
        // 用户文件信息不为空时，将查询结果压入缓存
        Optional.ofNullable(diskFile).ifPresent(file -> cacheManagerConfig
                .putCache(cacheKey, file, ConstantConfig.Cache.DEFAULT_EXPIRE));
        return diskFile;
    }

    /**
     * 文件的parentId校验机制，针对是否存在、是否与自己有关、是否为文件夹的校验
     *
     * @param userId   与parentId一致的用户id
     * @param parentId 文件的父级标识
     */
    @Override
    public void verifyParentId(String userId, String parentId) {
        // 如果parentId等于0，则不需要进行校验
        if (NumberConstant.STRING_ZERO.equals(parentId)) {
            return;
        }
        // 根据文件标识查找文件对象
        DiskFile diskFile = this.getDiskFile(userId, parentId);
        if (diskFile == null || !diskFile.getUserId().equals(userId)) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.PARENT_ID_NOT_FOUND);
        }

        if (!diskFile.getFileFolder()) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.NOT_FOLDER);
        }
    }

    /**
     * 文件名重复校验机制，针对同一个目录下的文件、文件夹名称重复性校验
     *
     * @param fileName   文件、文件夹名称(文件和文件夹重复时互不影响)
     * @param userId     指定的用户标识
     * @param parentId   文件的父级标识
     * @param fileFolder 是否为文件夹
     * @return true:目录下存在相同的文件 false:不存在相同的文件
     */
    @Override
    public Boolean verifyRepeat(String fileName, String userId, String parentId, boolean fileFolder) {
        return diskFileService.getRepeatFileName(parentId, fileName, userId, fileFolder) != null;
    }

    /**
     * 文件鉴权参数加密
     *
     * @param fileAuthModel 文件鉴权参数对象
     * @return 加密后的签名
     */
    @Override
    public String encrypt(FileAuthModel fileAuthModel) {
        // 获取对称加密SymmetricCrypto对象
        SymmetricCrypto symmetricCrypto = this.getSymmetricCrypto();
        return symmetricCrypto.encryptBase64(JSON.toJSONString(fileAuthModel));
    }

    /**
     * 文件访问签名解密
     *
     * @param sign 签名
     * @return 解密后的对象串
     */
    @Override
    public FileAuthModel decrypt(String sign) {
        // 获取对称加密SymmetricCrypto对象
        SymmetricCrypto symmetricCrypto = this.getSymmetricCrypto();
        try {
            // 获取解密后的内容
            String content = symmetricCrypto.decryptStr(sign, CharsetUtil.CHARSET_UTF_8);
            return JSON.parseObject(content, FileAuthModel.class);
        } catch (Exception e) {
            // 出现异常响应null值
            return null;
        }
    }

    /**
     * 获取对称加密SymmetricCrypto对象
     *
     * @return SymmetricCrypto对象
     */
    @Override
    public SymmetricCrypto getSymmetricCrypto() {
        // 获取本地缓存对象
        SymmetricCrypto symmetricCrypto = SYMMETRIC_CRYPTO_CACHE.get();
        if (symmetricCrypto == null) {
            // 获取全局对称加密密钥
            String cipher = diskDictionaryService.getDictionary(DictionaryConstant.CIPHER, String.class);
            symmetricCrypto = new SymmetricCrypto(SymmetricAlgorithm.AES, cipher.getBytes(StandardCharsets.UTF_8));
            SYMMETRIC_CRYPTO_CACHE.set(symmetricCrypto);
        }
        return symmetricCrypto;
    }

    // ==================================================== private ====================================================

    /**
     * 重置文件名
     * 针对文件名重复的情况下，会进行文件名称的重置
     * 重置规则:
     * 1.保证文件名长度尽量不超过50个字符
     * 2.在文件名末尾(后缀之前)插入指定字符串(_HHmmssSSS_3位随机数)
     * 形成最终的文件名 （原文件名）（_HHmmssSSS_3位随机数）.后缀
     * 3.如果最终的文件名长度超过50个字符串，则适当的减少（原文件名）的长度
     * 确保最终形成的文件名长度不超过50个字符
     *
     * @param originalFile 原来的 完整的 文件名
     * @return 构建的最终的文件名
     */
    private String resetFileName(String originalFile) {
        // 获取文件的后缀索引信息
        int suffixIndex = originalFile.lastIndexOf(ConstantConfig.SpecialSymbols.DOT);
        // 获取原文件的后缀
        String suffix = suffixIndex != NumberConstant.INTEGER_MINUS_ONE ? originalFile.substring(suffixIndex) : CharSequenceUtil.EMPTY;
        // 获取原文件的文件名
        String filename = suffixIndex != NumberConstant.INTEGER_MINUS_ONE ? originalFile.substring(NumberConstant.INTEGER_ZERO, suffixIndex) : originalFile;

        // 需要进行插入的 特定的字符串
        String specificCharacter = DateUtils.format(DateUtil.date(), YUMMY_HMS) + RandomUtil.randomString(NumberConstant.INTEGER_THREE) + suffix;

        // 在文件名末尾加入指定字符串
        String finalFilename = filename + specificCharacter;
        // 如果最终生成的文件名字符串长度 > 80
        if (finalFilename.length() > FILE_NAME_LENGTH) {
            // 字符串之间的差值(求出超过指定数值的多少)
            int differenceValue = finalFilename.length() - FILE_NAME_LENGTH;
            // 将原字符串从末尾开始 共截取 $differenceValue 位
            finalFilename = StrUtil.reverse(StrUtil.reverse(filename).substring(differenceValue)) + specificCharacter;
        }
        return finalFilename;
    }
}
