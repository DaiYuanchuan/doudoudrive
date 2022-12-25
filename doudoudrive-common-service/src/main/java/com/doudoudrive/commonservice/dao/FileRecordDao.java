package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.FileRecord;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>文件操作记录数据访问层</p>
 * <p>2022-05-26 10:52</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.FILE)
public interface FileRecordDao {

    /**
     * 新增文件操作记录
     *
     * @param fileRecord 需要新增的文件操作记录实体
     * @return 返回新增的条数
     */
    Integer insert(@Param("fileRecord") FileRecord fileRecord);

    /**
     * 批量新增文件操作记录
     *
     * @param list 需要新增的文件操作记录集合
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<FileRecord> list);

    /**
     * 删除文件操作记录
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(@Param("businessId") String businessId);

    /**
     * 批量删除文件操作记录
     *
     * @param list 需要删除的业务id(businessId)数据集合
     * @return 返回删除的条数
     */
    Integer deleteBatch(@Param("list") List<String> list);

    /**
     * 修改文件操作记录
     *
     * @param fileRecord 需要进行修改的文件操作记录实体
     * @return 返回修改的条数
     */
    Integer update(@Param("fileRecord") FileRecord fileRecord);

    /**
     * 批量修改文件操作记录
     *
     * @param list 需要进行修改的文件操作记录集合
     * @return 返回修改的条数
     */
    Integer updateBatch(@Param("list") List<FileRecord> list);

    /**
     * 查找文件操作记录
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的文件操作记录实体
     */
    FileRecord getFileRecord(@Param("businessId") String businessId);

    /**
     * 根据数据表中某个成员变量名称(非实体类中property的名称)查找(value需符合unique约束)
     *
     * @param modelName 数据表中某个成员变量名称,非实体类中property的名称[如:create_time]
     * @param value     需要查找的值
     * @return 返回查找到的文件操作记录实体
     */
    FileRecord getFileRecordToModel(@Param("modelName") String modelName, @Param("value") Object value);

    /**
     * 批量查找文件操作记录
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的文件操作记录数据集合
     */
    List<FileRecord> listFileRecord(@Param("list") List<String> list);

    /**
     * 指定条件查找文件操作记录
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @param limit      分页的SQL语句
     * @return 返回查找到的文件操作记录数据集合
     */
    List<FileRecord> listFileRecordToKey(@Param("fileRecord") FileRecord fileRecord, @Param("startTime") String startTime,
                                         @Param("endTime") String endTime, @Param("limit") String limit);

    /**
     * 返回搜索结果的总数
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(@Param("fileRecord") FileRecord fileRecord, @Param("startTime") String startTime,
                     @Param("endTime") String endTime);

    // ====================================================== 截断 =====================================================

    /**
     * 删除指定状态的文件操作记录
     *
     * @param userId     用户标识
     * @param etag       文件唯一标识
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回删除的条数
     */
    Integer deleteAction(@Param("userId") String userId,
                         @Param("etag") List<String> etag,
                         @Param("action") String action,
                         @Param("actionType") String actionType);

    /**
     * 判断指定状态的文件操作记录是否存在
     *
     * @param userId     用户标识
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 存在指定状态的文件操作记录时返回 1 ，否则返回 0 或者 null
     */
    Integer isFileRecordExist(@Param("userId") String userId,
                              @Param("action") String action,
                              @Param("actionType") String actionType);

    /**
     * 根据action和actionType查询指定状态的文件操作记录，只返回一条数据
     *
     * @param userId     用户标识
     * @param etag       文件唯一标识
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回查找到的文件操作记录实体
     */
    FileRecord getFileRecordByAction(@Param("userId") String userId,
                                     @Param("etag") String etag,
                                     @Param("action") String action,
                                     @Param("actionType") String actionType);

    /**
     * 更新 指定动作类型 的文件操作记录的 动作类型
     *
     * @param businessId     文件操作记录系统内唯一标识
     * @param fromAction     原动作
     * @param fromActionType 原动作对应的动作类型
     * @param toAction       新动作
     * @param toActionType   新动作对应的动作类型
     * @return 返回更新的条数
     */
    Integer updateFileRecordByAction(@Param("businessId") String businessId,
                                     @Param("fromAction") String fromAction,
                                     @Param("fromActionType") String fromActionType,
                                     @Param("toAction") String toAction,
                                     @Param("toActionType") String toActionType);
}
