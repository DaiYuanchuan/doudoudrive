package com.doudoudrive.file.manager;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.doudoudrive.common.model.dto.model.CreateFileAuthModel;
import com.doudoudrive.common.model.dto.model.DiskFileModel;
import com.doudoudrive.common.model.dto.model.FileAuthModel;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.file.model.dto.response.FileSearchResponseDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>用户文件信息服务的通用业务处理层接口</p>
 * <p>2022-05-21 17:50</p>
 *
 * @author Dan
 **/
public interface FileManager {

    /**
     * 创建文件夹
     *
     * @param userId   用户标识
     * @param name     文件夹名称
     * @param parentId 文件父级标识
     * @return 用户文件模块信息
     */
    DiskFile createFolder(String userId, String name, String parentId);

    /**
     * 创建文件
     *
     * @param fileInfo          创建文件时的鉴权参数模型
     * @param fileId            文件标识
     * @param totalDiskCapacity 用户当前总容量
     * @param usedDiskCapacity  用户当前已经使用的磁盘容量
     * @return 返回构建入库时的用户文件模块
     */
    DiskFile createFile(CreateFileAuthModel fileInfo, String fileId, BigDecimal totalDiskCapacity, BigDecimal usedDiskCapacity);

    /**
     * 根据业务标识查找指定用户下的文件信息，优先从缓存中查找
     *
     * @param userId     指定的用户标识
     * @param businessId 需要查询的文件标识
     * @return 用户文件模块信息
     */
    DiskFile getDiskFile(String userId, String businessId);

    /**
     * 重命名文件或文件夹
     *
     * @param file 需要重命名的文件或文件夹信息
     * @param name 需要更改的文件名
     */
    void renameFile(DiskFile file, String name);

    /**
     * 根据文件id批量删除文件或文件夹
     *
     * @param content 需要删除的文件或文件夹信息
     * @param userId  需要删除的文件或文件夹所属用户标识
     */
    void delete(List<DiskFileModel> content, String userId);

    /**
     * 文件信息翻页搜索
     *
     * @param queryElasticRequest 构建ES文件查询请求数据模型
     * @param marker              加密的游标数据
     * @return 文件信息翻页搜索结果
     */
    FileSearchResponseDTO search(QueryElasticsearchDiskFileRequestDTO queryElasticRequest, String marker);

    /**
     * 文件的parentId校验机制，针对是否存在、是否与自己有关、是否为文件夹的校验
     *
     * @param userId   与parentId一致的用户id
     * @param parentId 文件的父级标识
     */
    void verifyParentId(String userId, String parentId);

    /**
     * 文件名重复校验机制，针对同一个目录下的文件、文件夹名称重复性校验
     *
     * @param fileName   文件、文件夹名称(文件和文件夹重复时互不影响)
     * @param userId     指定的用户标识
     * @param parentId   文件的父级标识
     * @param fileFolder 是否为文件夹
     * @return true:目录下存在相同的文件 false:不存在相同的文件
     */
    Boolean verifyRepeat(String fileName, String userId, String parentId, boolean fileFolder);

    /**
     * 文件鉴权参数加密
     *
     * @param object 需要鉴权的参数对象
     * @return 加密后的签名
     */
    String encrypt(Object object);

    /**
     * 文件鉴权签名解密
     *
     * @param sign  签名
     * @param clazz 签名解密后需要转换的对象类
     * @return 解密后的对象串
     */
    <T> T decrypt(String sign, Class<T> clazz);

    /**
     * 获取对称加密SymmetricCrypto对象
     *
     * @return SymmetricCrypto对象
     */
    SymmetricCrypto getSymmetricCrypto();

    /**
     * 获取文件访问Url
     *
     * @param authModel 文件鉴权参数
     * @param fileModel 文件模型
     * @return 重新赋值后的文件模型
     */
    DiskFileModel accessUrl(FileAuthModel authModel, DiskFileModel fileModel);

    /**
     * 批量获取文件访问Url
     *
     * @param authModel     文件鉴权参数
     * @param fileModelList 文件模型集合
     * @return 重新赋值后的文件模型集合
     */
    List<DiskFileModel> accessUrl(FileAuthModel authModel, List<DiskFileModel> fileModelList);
}
