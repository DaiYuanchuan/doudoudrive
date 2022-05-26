package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.FileRecord;
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
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回删除的条数
     */
    Integer deleteAction(@Param("action") String action, @Param("actionType") String actionType);

    /**
     * 获取指定状态的文件操作记录，只会获取1条
     *
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 指定状态的文件操作记录对象
     */
    FileRecord getFileRecordByAction(@Param("action") String action, @Param("actionType") String actionType);

}