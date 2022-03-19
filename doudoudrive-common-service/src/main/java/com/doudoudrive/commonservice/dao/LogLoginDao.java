package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.LogLogin;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>登录日志数据访问层</p>
 * <p>2022-03-06 19:58</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.LOG)
public interface LogLoginDao {

    /**
     * 新增登录日志
     *
     * @param logLogin 需要新增的登录日志实体
     * @return 返回新增的条数
     */
    Integer insert(@Param("logLogin") LogLogin logLogin);

    /**
     * 批量新增登录日志
     *
     * @param list 需要新增的登录日志集合
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<LogLogin> list);

    /**
     * 删除登录日志
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(@Param("businessId") String businessId);

    /**
     * 批量删除登录日志
     *
     * @param list 需要删除的业务id(businessId)数据集合
     * @return 返回删除的条数
     */
    Integer deleteBatch(@Param("list") List<String> list);

    /**
     * 修改登录日志
     *
     * @param logLogin 需要进行修改的登录日志实体
     * @return 返回修改的条数
     */
    Integer update(@Param("logLogin") LogLogin logLogin);

    /**
     * 批量修改登录日志
     *
     * @param list 需要进行修改的登录日志集合
     * @return 返回修改的条数
     */
    Integer updateBatch(@Param("list") List<LogLogin> list);

    /**
     * 查找登录日志
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的登录日志实体
     */
    LogLogin getLogLogin(@Param("businessId") String businessId);

    /**
     * 根据数据表中某个成员变量名称(非实体类中property的名称)查找(value需符合unique约束)
     *
     * @param modelName 数据表中某个成员变量名称,非实体类中property的名称[如:create_time]
     * @param value     需要查找的值
     * @return 返回查找到的登录日志实体
     */
    LogLogin getLogLoginToModel(@Param("modelName") String modelName, @Param("value") Object value);

    /**
     * 批量查找登录日志
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的登录日志数据集合
     */
    List<LogLogin> listLogLogin(@Param("list") List<String> list);

    /**
     * 指定条件查找登录日志
     *
     * @param logLogin  需要查询的登录日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param limit     分页的SQL语句
     * @return 返回查找到的登录日志数据集合
     */
    List<LogLogin> listLogLoginToKey(@Param("logLogin") LogLogin logLogin, @Param("startTime") String startTime,
                                     @Param("endTime") String endTime, @Param("limit") String limit);

    /**
     * 返回搜索结果的总数
     *
     * @param logLogin  需要查询的登录日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(@Param("logLogin") LogLogin logLogin, @Param("startTime") String startTime,
                        @Param("endTime") String endTime);


}
