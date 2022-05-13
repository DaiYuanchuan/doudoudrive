package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.SysRole;

import java.util.List;

/**
 * <p>系统角色管理模块服务层接口</p>
 * <p>2022-04-06 15:15</p>
 *
 * @author Dan
 **/
public interface SysRoleService {

    /**
     * 新增系统角色管理模块
     *
     * @param sysRole 需要新增的系统角色管理模块实体
     */
    void insert(SysRole sysRole);

    /**
     * 批量新增系统角色管理模块
     *
     * @param list 需要新增的系统角色管理模块集合
     */
    void insertBatch(List<SysRole> list);

    /**
     * 删除系统角色管理模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 批量删除系统角色管理模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    void deleteBatch(List<String> list);

    /**
     * 修改系统角色管理模块
     *
     * @param sysRole 需要进行修改的系统角色管理模块实体
     * @return 返回修改的条数
     */
    Integer update(SysRole sysRole);

    /**
     * 批量修改系统角色管理模块
     *
     * @param list 需要进行修改的系统角色管理模块集合
     */
    void updateBatch(List<SysRole> list);

    /**
     * 查找系统角色管理模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的系统角色管理模块实体
     */
    SysRole getSysRole(String businessId);

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的系统角色管理模块实体
     */
    SysRole getSysRoleToModel(String modelName, Object value);

    /**
     * 批量查找系统角色管理模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的系统角色管理模块数据集合
     */
    List<SysRole> listSysRole(List<String> list);

    /**
     * 指定条件查找系统角色管理模块
     *
     * @param sysRole   需要查询的系统角色管理模块实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 系统角色管理模块搜索响应数据模型
     */
    PageResponse<SysRole> listSysRoleToKey(SysRole sysRole, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 指定条件查找系统角色管理模块
     * 返回系统角色管理模块集合数据
     *
     * @param sysRole   需要查询的系统角色管理模块实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 返回系统角色管理模块集合
     */
    List<SysRole> listSysRole(SysRole sysRole, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 查找所有系统角色管理模块
     *
     * @return 返回所有的系统角色管理模块集合数据
     */
    List<SysRole> listSysRoleFindAll();

    /**
     * 返回搜索结果的总数
     *
     * @param sysRole   需要查询的系统角色管理模块实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(SysRole sysRole, String startTime, String endTime);

}
