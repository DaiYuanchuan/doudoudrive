package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.pojo.FileShare;

import java.util.List;

/**
 * <p>文件分享信息服务层接口</p>
 * <p>2023-01-03 18:31</p>
 *
 * @author Dan
 **/
public interface FileShareService {

    /**
     * 新增文件分享信息
     *
     * @param fileShare 需要新增的文件分享信息实体
     * @return 返回新增的条数
     */
    Integer insert(FileShare fileShare);

    /**
     * 批量删除文件分享信息
     *
     * @param shareIdList 需要删除的分享的短链接标识(shareId)数据集合
     * @param userId      所属的用户标识
     */
    void deleteBatch(List<String> shareIdList, String userId);

    /**
     * 修改文件分享信息
     *
     * @param fileShare 需要进行修改的文件分享信息实体
     * @param userId    所属的用户标识
     * @return 返回修改的条数
     */
    Integer update(FileShare fileShare, String userId);

    /**
     * 对指定的字段自增，如: browse_count、save_count、download_count
     *
     * @param shareId   分享标识
     * @param fieldName 字段名(browse_count、save_count、download_count)
     * @param userId    所属的用户标识
     */
    void increase(String shareId, ConstantConfig.FileShareIncreaseEnum fieldName, String userId);

    /**
     * 更新所有过期的分享链接
     */
    void updateExpiredShare();

    /**
     * 查找文件分享信息
     *
     * @param businessId 根据业务id(businessId)查找
     * @param userId     所属的用户标识
     * @return 返回查找到的文件分享信息实体
     */
    FileShare getFileShare(String businessId, String userId);

    /**
     * 批量查找文件分享信息
     *
     * @param list   需要进行查找的分享的短链接标识(shareId)数据集合
     * @param userId 所属的用户标识
     * @return 返回查找到的文件分享信息数据集合
     */
    List<FileShare> listFileShare(List<String> list, String userId);
}
