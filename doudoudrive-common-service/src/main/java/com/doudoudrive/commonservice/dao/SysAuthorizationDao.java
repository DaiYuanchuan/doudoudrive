package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.SysAuthorization;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>系统权限管理模块数据访问层</p>
 * <p>2022-04-06 13:56</p>
 *
 * @author Dan
 **/
@Repository
public interface SysAuthorizationDao {

    /**
     * 新增系统权限管理模块
     *
     * @param sysAuthorization 需要新增的系统权限管理模块实体
     * @return 返回新增的条数
     */
    Integer insert(@Param("sysAuthorization") SysAuthorization sysAuthorization);

    /**
     * 批量新增系统权限管理模块
     *
     * @param list 需要新增的系统权限管理模块集合
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<SysAuthorization> list);

    /**
     * 删除系统权限管理模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(@Param("businessId") String businessId);

    /**
     * 批量删除系统权限管理模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     * @return 返回删除的条数
     */
    Integer deleteBatch(@Param("list") List<String> list);

    /**
     * 修改系统权限管理模块
     *
     * @param sysAuthorization 需要进行修改的系统权限管理模块实体
     * @return 返回修改的条数
     */
    Integer update(@Param("sysAuthorization") SysAuthorization sysAuthorization);

    /**
     * 批量修改系统权限管理模块
     *
     * @param list 需要进行修改的系统权限管理模块集合
     * @return 返回修改的条数
     */
    Integer updateBatch(@Param("list") List<SysAuthorization> list);

    /**
     * 查找系统权限管理模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的系统权限管理模块实体
     */
    SysAuthorization getSysAuthorization(@Param("businessId") String businessId);

    /**
     * 根据数据表中某个成员变量名称(非实体类中property的名称)查找(value需符合unique约束)
     *
     * @param modelName 数据表中某个成员变量名称,非实体类中property的名称[如:create_time]
     * @param value     需要查找的值
     * @return 返回查找到的系统权限管理模块实体
     */
    SysAuthorization getSysAuthorizationToModel(@Param("modelName") String modelName, @Param("value") Object value);

    /**
     * 批量查找系统权限管理模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的系统权限管理模块数据集合
     */
    List<SysAuthorization> listSysAuthorization(@Param("list") List<String> list);

    /**
     * 指定条件查找系统权限管理模块
     *
     * @param sysAuthorization 需要查询的系统权限管理模块实体
     * @param startTime        需要查询的开始时间(如果有)
     * @param endTime          需要查询的结束时间(如果有)
     * @param limit            分页的SQL语句
     * @return 返回查找到的系统权限管理模块数据集合
     */
    List<SysAuthorization> listSysAuthorizationToKey(@Param("sysAuthorization") SysAuthorization sysAuthorization, @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime, @Param("limit") String limit);

    /**
     * 返回搜索结果的总数
     *
     * @param sysAuthorization 需要查询的系统权限管理模块实体
     * @param startTime        需要查询的开始时间(如果有)
     * @param endTime          需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(@Param("sysAuthorization") SysAuthorization sysAuthorization, @Param("startTime") String startTime,
                     @Param("endTime") String endTime);

}
