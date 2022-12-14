package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.SysUserRole;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>用户、角色关联模块数据访问层</p>
 * <p>2022-04-06 13:58</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.USERINFO)
public interface SysUserRoleDao {

    /**
     * 新增用户、角色关联模块
     *
     * @param sysUserRole 需要新增的用户、角色关联模块实体
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("sysUserRole") SysUserRole sysUserRole, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量新增用户、角色关联模块
     *
     * @param list        需要新增的用户、角色关联模块集合
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<SysUserRole> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 删除指定用户关联的所有角色
     *
     * @param userId      根据用户业务id删除数据
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer deleteSysUserRole(@Param("userId") String userId, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据用户标识查询指定用户下所有绑定的角色信息
     *
     * @param userId      根据用户业务id查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的用户、角色关联模块实体数据集合
     */
    List<SysUserRole> listSysUserRole(@Param("userId") String userId, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据用户标识与系统角色编码批量查询指定用户的角色绑定信息
     *
     * @param userId       根据用户业务id查找
     * @param roleCodeList 角色编码集合
     * @param tableSuffix  表后缀
     * @return 返回查找到的用户、角色关联模块实体数据集合
     */
    List<SysUserRole> listSysUserRoleByRoleCode(@Param("userId") String userId,
                                                @Param("roleCode") List<String> roleCodeList,
                                                @Param("tableSuffix") String tableSuffix);

}
