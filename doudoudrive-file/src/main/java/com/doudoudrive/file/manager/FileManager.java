package com.doudoudrive.file.manager;

import com.doudoudrive.common.model.pojo.DiskFile;

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
     * 根据业务标识查找指定用户下的文件信息，优先从缓存中查找
     *
     * @param userId     指定的用户标识
     * @param businessId 需要查询的文件标识
     * @return 用户文件模块信息
     */
    DiskFile getDiskFile(String userId, String businessId);

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
}
