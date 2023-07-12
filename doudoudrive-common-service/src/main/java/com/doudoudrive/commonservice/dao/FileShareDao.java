package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.FileShare;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>文件分享信息数据访问层</p>
 * <p>2023-01-03 16:39</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.FILE_SHARE)
public interface FileShareDao {

    /**
     * 新增文件分享信息
     *
     * @param fileShare   需要新增的文件分享信息实体
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("fileShare") FileShare fileShare, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量删除文件分享信息
     *
     * @param shareId     需要删除的分享的短链接标识(shareId)数据集合
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer deleteBatch(@Param("shareId") List<String> shareId, @Param("tableSuffix") String tableSuffix);

    /**
     * 修改文件分享信息
     *
     * @param fileShare   需要进行修改的文件分享信息实体
     * @param tableSuffix 表后缀
     * @return 返回修改的条数
     */
    Integer update(@Param("fileShare") FileShare fileShare, @Param("tableSuffix") String tableSuffix);

    /**
     * 对指定的字段自增，如: browse_count、save_count、download_count
     *
     * @param shareId     分享标识
     * @param fieldName   字段名(browse_count、save_count、download_count)
     * @param tableSuffix 表后缀
     * @return 返回修改的条数
     */
    Integer increase(@Param("shareId") String shareId, @Param("fieldName") String fieldName, @Param("tableSuffix") String tableSuffix);

    /**
     * 更新所有过期的分享链接
     *
     * @param now         当前时间
     * @param tableSuffix 所有的分表后缀
     * @return 返回修改的条数
     */
    Integer updateExpiredShare(@Param("now") LocalDateTime now, @Param("tableSuffix") List<String> tableSuffix);

    /**
     * 查找文件分享信息
     *
     * @param businessId  根据业务id(businessId)查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的文件分享信息实体
     */
    FileShare getFileShare(@Param("businessId") String businessId, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量查找文件分享信息
     *
     * @param list        需要进行查找的分享的短链接标识(shareId)数据集合
     * @param tableSuffix 表后缀
     * @return 返回查找到的文件分享信息数据集合
     */
    List<FileShare> listFileShare(@Param("list") List<String> list, @Param("tableSuffix") String tableSuffix);
}
