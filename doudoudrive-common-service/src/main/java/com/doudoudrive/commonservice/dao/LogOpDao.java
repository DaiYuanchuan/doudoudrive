package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>API操作日志数据访问层</p>
 * <p>2022-03-04 13:05</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.LOG)
public interface LogOpDao {

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     * @return 返回新增的条数
     */
    Integer insert(LogOp logOp);

    /**
     * 批量新增API操作日志
     *
     * @param list 需要新增的API操作日志集合
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<LogOp> list);

    /**
     * 删除API操作日志
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(@Param("businessId") String businessId);

    /**
     * 批量删除API操作日志
     *
     * @param list 需要删除的业务id(businessId)数据集合
     * @return 返回删除的条数
     */
    Integer deleteBatch(@Param("list") List<String> list);

    /**
     * 修改API操作日志
     *
     * @param logOp 需要进行修改的API操作日志实体
     * @return 返回修改的条数
     */
    Integer update(@Param("logOp") LogOp logOp);

    /**
     * 批量修改API操作日志
     *
     * @param list 需要进行修改的API操作日志集合
     * @return 返回修改的条数
     */
    Integer updateBatch(@Param("list") List<LogOp> list);

    /**
     * 查找API操作日志
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的API操作日志实体
     */
    LogOp getLogOp(@Param("businessId") String businessId);

    /**
     * 根据数据表中某个成员变量名称(非实体类中property的名称)查找(value需符合unique约束)
     *
     * @param modelName 数据表中某个成员变量名称,非实体类中property的名称[如:create_time]
     * @param value     需要查找的值
     * @return 返回查找到的API操作日志实体
     */
    LogOp getLogOpToModel(@Param("modelName") String modelName, @Param("value") Object value);

    /**
     * 批量查找API操作日志
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的API操作日志数据集合
     */
    List<LogOp> listLogOp(@Param("list") List<String> list);

    /**
     * 指定条件查找API操作日志
     *
     * @param logOp     需要查询的API操作日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param limit     分页的SQL语句
     * @return 返回查找到的API操作日志数据集合
     */
    List<LogOp> listLogOpToKey(@Param("logOp") LogOp logOp, @Param("startTime") String startTime,
                               @Param("endTime") String endTime, @Param("limit") String limit);

    /**
     * 返回搜索结果的总数
     *
     * @param logOp     需要查询的API操作日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(@Param("logOp") LogOp logOp, @Param("startTime") String startTime,
                     @Param("endTime") String endTime);

    // ====================================================== 截断 =====================================================

}
