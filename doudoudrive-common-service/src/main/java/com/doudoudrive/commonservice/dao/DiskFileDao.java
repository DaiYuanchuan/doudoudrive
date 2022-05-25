package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.DiskFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>用户文件模块数据访问层</p>
 * <p>2022-05-19 23:11</p>
 *
 * @author Dan
 **/
@Repository
public interface DiskFileDao {

    /**
     * 新增用户文件模块
     *
     * @param diskFile    需要新增的用户文件模块实体
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("diskFile") DiskFile diskFile, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量新增用户文件模块
     *
     * @param list        需要新增的用户文件模块集合
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<DiskFile> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 删除用户文件模块
     *
     * @param businessId  根据业务id(businessId)删除数据
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer delete(@Param("businessId") String businessId, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量删除用户文件模块
     *
     * @param list        需要删除的业务id(businessId)数据集合
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer deleteBatch(@Param("list") List<String> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 修改用户文件模块
     *
     * @param diskFile    需要进行修改的用户文件模块实体
     * @param tableSuffix 表后缀
     * @return 返回修改的条数
     */
    Integer update(@Param("diskFile") DiskFile diskFile, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量修改用户文件模块
     *
     * @param list        需要进行修改的用户文件模块集合
     * @param tableSuffix 表后缀
     * @return 返回修改的条数
     */
    Integer updateBatch(@Param("list") List<DiskFile> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据业务标识查找指定用户下的文件信息
     *
     * @param userId      指定的用户标识
     * @param businessId  需要查询的文件标识
     * @param tableSuffix 表后缀
     * @return 用户文件模块信息
     */
    DiskFile getDiskFile(@Param("userId") String userId,
                         @Param("businessId") String businessId,
                         @Param("tableSuffix") String tableSuffix);

    /**
     * 根据parentId查询指定目录下是否存在指定的文件名
     *
     * @param parentId    文件的父级标识
     * @param fileName    文件、文件夹名称
     * @param userId      指定的用户标识
     * @param fileFolder  是否为文件夹
     * @param tableSuffix 表后缀
     * @return 在指定目录下存在相同的文件名时返回
     */
    DiskFile getRepeatFileName(@Param("parentId") String parentId,
                               @Param("fileName") String fileName,
                               @Param("userId") String userId,
                               @Param("fileFolder") Boolean fileFolder,
                               @Param("tableSuffix") String tableSuffix);

    /**
     * 指定条件查找用户文件模块
     *
     * @param diskFile    需要查询的用户文件模块实体
     * @param tableSuffix 表后缀
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @param limit       分页的SQL语句
     * @return 返回查找到的用户文件模块数据集合
     */
    List<DiskFile> listDiskFileToKey(@Param("diskFile") DiskFile diskFile, @Param("tableSuffix") String tableSuffix,
                                     @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("limit") String limit);

    /**
     * 返回搜索结果的总数
     *
     * @param diskFile    需要查询的用户文件模块实体
     * @param tableSuffix 表后缀
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(@Param("diskFile") DiskFile diskFile, @Param("tableSuffix") String tableSuffix,
                     @Param("startTime") String startTime, @Param("endTime") String endTime);
}