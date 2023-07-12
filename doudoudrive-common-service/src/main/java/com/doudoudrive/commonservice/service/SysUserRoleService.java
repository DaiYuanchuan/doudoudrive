package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.SysUserRole;

import java.util.List;

/**
 * <p>用户、角色关联模块服务层接口</p>
 * <p>2022-04-06 15:16</p>
 *
 * @author Dan
 **/
public interface SysUserRoleService {

    /**
     * 新增用户、角色关联模块
     *
     * @param sysUserRole 需要新增的用户、角色关联模块实体
     */
    void insert(SysUserRole sysUserRole);

    /**
     * 批量新增用户、角色关联模块
     *
     * @param list 需要新增的用户、角色关联模块集合
     */
    void insertBatch(List<SysUserRole> list);

    /**
     * 删除指定用户关联的所有角色
     *
     * @param userId 根据用户业务id删除数据
     */
    void deleteSysUserRole(String userId);

    /**
     * 根据用户标识查询指定用户下所有绑定的角色信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户、角色关联模块实体
     */
    List<SysUserRole> listSysUserRole(String userId);

    /**
     * 根据用户标识与系统角色编码批量查询指定用户的角色绑定信息
     *
     * @param userId       根据用户业务id查找
     * @param roleCodeList 角色编码集合
     * @return 返回查找到的用户、角色关联模块实体数据集合
     */
    List<SysUserRole> listSysUserRoleByRoleCode(String userId, List<String> roleCodeList);
}
