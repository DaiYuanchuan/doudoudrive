package com.doudoudrive.auth.manager;


import com.doudoudrive.common.constant.RoleCodeEnum;
import com.doudoudrive.common.model.dto.model.SysUserRoleModel;

import java.util.List;

/**
 * <p>>用户、角色关联模型通用业务处理层接口</p>
 * <p>2022-04-06 16:14</p>
 *
 * @author Dan
 **/
public interface SysUserRoleManager {

    /**
     * 批量新增用户角色关联信息
     *
     * @param userId       用户业务id
     * @param roleCodeList 角色编码列表
     */
    void insert(String userId, List<RoleCodeEnum> roleCodeList);

    /**
     * 删除指定用户关联的所有角色
     *
     * @param userId 根据用户业务id删除数据
     */
    void deleteSysUserRole(String userId);

    /**
     * 根据用户标识查询指定用户下所绑定的所有角色、权限信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回指定用户的角色、权限数据模型
     */
    List<SysUserRoleModel> listSysUserRoleInfo(String userId);

    /**
     * 判断当前用户是否存在指定的角色
     *
     * @param userId   用户业务id
     * @param roleCode 需要查询的角色编码
     * @return true:存在指定角色 false:不存在
     */
    Boolean existenceRoleCode(String userId, String roleCode);

    /**
     * 判断当前用户是否存在指定的权限
     *
     * @param userId   用户业务id
     * @param authCode 需要查询的权限编码
     * @return true:存在指定权限 false:不存在
     */
    Boolean existenceAuthCode(String userId, String authCode);
}
