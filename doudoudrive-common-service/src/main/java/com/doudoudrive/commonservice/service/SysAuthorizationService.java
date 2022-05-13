package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.SysAuthorization;

import java.util.List;

/**
 * <p>系统权限管理模块服务层接口</p>
 * <p>2022-04-06 15:13</p>
 *
 * @author Dan
 **/
public interface SysAuthorizationService {

    /**
     * 新增系统权限管理模块
     *
     * @param sysAuthorization 需要新增的系统权限管理模块实体
     */
    void insert(SysAuthorization sysAuthorization);

    /**
     * 批量新增系统权限管理模块
     *
     * @param list 需要新增的系统权限管理模块集合
     */
    void insertBatch(List<SysAuthorization> list);

    /**
     * 删除系统权限管理模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 批量删除系统权限管理模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    void deleteBatch(List<String> list);

    /**
     * 修改系统权限管理模块
     *
     * @param sysAuthorization 需要进行修改的系统权限管理模块实体
     * @return 返回修改的条数
     */
    Integer update(SysAuthorization sysAuthorization);

    /**
     * 批量修改系统权限管理模块
     *
     * @param list 需要进行修改的系统权限管理模块集合
     */
    void updateBatch(List<SysAuthorization> list);

    /**
     * 查找系统权限管理模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的系统权限管理模块实体
     */
    SysAuthorization getSysAuthorization(String businessId);

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的系统权限管理模块实体
     */
    SysAuthorization getSysAuthorizationToModel(String modelName, Object value);

    /**
     * 批量查找系统权限管理模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的系统权限管理模块数据集合
     */
    List<SysAuthorization> listSysAuthorization(List<String> list);

    /**
     * 指定条件查找系统权限管理模块
     *
     * @param sysAuthorization 需要查询的系统权限管理模块实体
     * @param startTime        需要查询的开始时间(如果有)
     * @param endTime          需要查询的结束时间(如果有)
     * @param page             页码
     * @param pageSize         每页大小
     * @return 系统权限管理模块搜索响应数据模型
     */
    PageResponse<SysAuthorization> listSysAuthorizationToKey(SysAuthorization sysAuthorization, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 指定条件查找系统权限管理模块
     * 返回系统权限管理模块集合数据
     *
     * @param sysAuthorization 需要查询的系统权限管理模块实体
     * @param startTime        需要查询的开始时间(如果有)
     * @param endTime          需要查询的结束时间(如果有)
     * @param page             页码
     * @param pageSize         每页大小
     * @return 返回系统权限管理模块集合
     */
    List<SysAuthorization> listSysAuthorization(SysAuthorization sysAuthorization, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 查找所有系统权限管理模块
     *
     * @return 返回所有的系统权限管理模块集合数据
     */
    List<SysAuthorization> listSysAuthorizationFindAll();

    /**
     * 返回搜索结果的总数
     *
     * @param sysAuthorization 需要查询的系统权限管理模块实体
     * @param startTime        需要查询的开始时间(如果有)
     * @param endTime          需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(SysAuthorization sysAuthorization, String startTime, String endTime);

}
