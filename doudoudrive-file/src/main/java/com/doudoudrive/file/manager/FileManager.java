package com.doudoudrive.file.manager;

import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.doudoudrive.common.model.dto.model.*;
import com.doudoudrive.common.model.dto.model.qiniu.QiNiuUploadConfig;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.dto.response.QueryElasticsearchDiskFileResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.file.model.dto.response.FileSearchResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    void delete(List<DiskFile> content, String userId);

    /**
     * 批量复制文件信息
     *
     * @param targetUserId      目标文件夹所属的用户标识
     * @param targetFolderId    目标文件夹的业务标识
     * @param treeStructureMap  用来保存树形结构的Map<原有的数据标识, 新数据返回的数据标识>(上一轮循环中的数据)
     * @param preCopyFileList   指定需要进行复制的文件信息
     * @param totalDiskCapacity 用户总磁盘容量
     * @return @return 本次循环中的用来保存树形结构的Map<原有的数据标识, 新数据返回的数据标识>
     */
    Map<String, String> batchCopyFile(String targetUserId, String targetFolderId, Map<String, String> treeStructureMap,
                                      List<DiskFile> preCopyFileList, String totalDiskCapacity);

    /**
     * 将指定的文件移动到目标文件夹
     *
     * @param businessId     需要移动的文件标识
     * @param targetFolderId 目标文件夹标识
     * @param userinfo       当前登录的用户信息
     */
    void move(List<String> businessId, String targetFolderId, DiskUserModel userinfo);

    /**
     * 文件信息翻页搜索
     *
     * @param queryElasticRequest 构建ES文件查询请求数据模型
     * @param authModel           文件鉴权参数模型
     * @param marker              加密的游标数据
     * @return 文件信息翻页搜索结果
     */
    FileSearchResponseDTO search(QueryElasticsearchDiskFileRequestDTO queryElasticRequest, FileAuthModel authModel, String marker);

    /**
     * 获取指定文件节点下所有的子节点信息 （递归）
     *
     * @param userId   用户系统内唯一标识
     * @param parentId 文件父级标识
     * @param consumer 回调函数中返回查找到的用户文件模块数据集合
     */
    void getUserFileAllNode(String userId, List<String> parentId, Consumer<List<DiskFile>> consumer);

    /**
     * 获取指定父目录下的所有文件信息
     *
     * @param autoId       自增长标识，用于分页游标
     * @param userId       用户系统内唯一标识
     * @param parentFileId 文件父级标识
     * @param consumer     回调函数中返回查找到的用户文件模块数据集合
     */
    void getAllFileInfo(Long autoId, String userId, List<String> parentFileId, Consumer<List<DiskFile>> consumer);

    /**
     * 根据文件业务标识批量查询用户文件信息
     *
     * @param userId 用户系统内唯一标识
     * @param fileId 文件业务标识
     * @return 返回查找到的用户文件模块数据集合
     */
    List<DiskFile> fileIdSearch(String userId, List<String> fileId);

    /**
     * 文件 进行 移动、复制 时，需要对其目标文件夹进行校验
     * 确保 目标文件夹 一定不为空，一定是文件夹，一定是归属于当前登录的用户
     * 目标文件夹不符合条件时会抛出业务异常
     *
     * @param userId         用户系统内唯一标识
     * @param targetFolderId 目标文件夹标识
     */
    void checkTargetFolder(String userId, String targetFolderId);

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
     * 文件名重复批量校验机制，针对同一个目录下的文件、文件夹名称重复性的批量校验
     * 如果存在重名的文件信息，则会重置原始文件名，将重置后的文件名返回，如果不存在重名的文件则原样返回数据集合
     *
     * @param parentId   文件的父级标识
     * @param userId     指定的用户标识
     * @param queryParam 指定的查询参数，包含文件名、是否为文件夹
     */
    void verifyRepeat(String parentId, String userId, List<DiskFile> queryParam);

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
     * 加密游标数据
     *
     * @param marker 游标数据
     * @return 加密后的游标数据
     */
    String encryptMarker(List<Object> marker);

    /**
     * 解密游标数据，marker不存在时返回null
     *
     * @param marker 加密的游标数据
     * @return 解密后的游标数据
     */
    List<Object> decryptMarker(String marker);

    /**
     * 获取文件访问Url
     *
     * @param authModel 文件鉴权参数
     * @param fileModel 文件模型
     * @return 重新赋值后的文件模型
     */
    DiskFileModel accessUrl(FileAuthModel authModel, DiskFileModel fileModel);

    /**
     * 获取文件访问Url
     *
     * @param authModel    文件鉴权参数
     * @param fileModel    文件模型
     * @param config       七牛云配置信息
     * @param reviewConfig 文件审核配置
     * @param consumer     生成访问Url时的回调函数，方便对文件鉴权模型进行扩展
     * @return 重新赋值后的文件模型
     */
    DiskFileModel accessUrl(FileAuthModel authModel, DiskFileModel fileModel, QiNiuUploadConfig config,
                            FileReviewConfig reviewConfig, Consumer<FileAuthModel> consumer);

    /**
     * 批量获取文件访问Url，同时加密下一页的游标数据
     *
     * @param authModel            文件鉴权参数
     * @param queryElasticResponse 搜索es用户文件信息时的响应数据模型
     * @param consumer             生成访问Url时的回调函数，方便对文件鉴权模型进行扩展
     * @return 重新赋值后的文件模型集合
     */
    FileSearchResponseDTO accessUrl(FileAuthModel authModel, List<QueryElasticsearchDiskFileResponseDTO> queryElasticResponse,
                                    Consumer<FileAuthModel> consumer);
}
