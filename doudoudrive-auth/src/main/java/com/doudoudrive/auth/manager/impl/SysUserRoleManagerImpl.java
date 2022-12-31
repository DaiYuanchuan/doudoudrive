package com.doudoudrive.auth.manager.impl;

import com.doudoudrive.auth.manager.SysUserRoleManager;
import com.doudoudrive.common.constant.RoleCodeEnum;
import com.doudoudrive.common.model.convert.SysUserRoleAuthConvert;
import com.doudoudrive.common.model.dto.model.SysUserRoleModel;
import com.doudoudrive.common.model.pojo.SysRole;
import com.doudoudrive.common.model.pojo.SysRoleAuth;
import com.doudoudrive.common.model.pojo.SysUserRole;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.commonservice.service.SysRoleAuthService;
import com.doudoudrive.commonservice.service.SysRoleService;
import com.doudoudrive.commonservice.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>用户、角色关联模型通用业务处理层接口实现</p>
 * <p>2022-04-06 16:28</p>
 *
 * @author Dan
 **/
@Service("sysUserRoleManager")
public class SysUserRoleManagerImpl implements SysUserRoleManager {

    private SysRoleService sysRoleService;

    private SysUserRoleService sysUserRoleService;

    private SysRoleAuthService sysRoleAuthService;

    private SysUserRoleAuthConvert sysUserRoleAuthConvert;

    @Autowired
    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @Autowired
    public void setSysUserRoleService(SysUserRoleService sysUserRoleService) {
        this.sysUserRoleService = sysUserRoleService;
    }

    @Autowired
    public void setSysRoleAuthService(SysRoleAuthService sysRoleAuthService) {
        this.sysRoleAuthService = sysRoleAuthService;
    }

    @Autowired(required = false)
    public void setSysUserRoleAuthConvert(SysUserRoleAuthConvert sysUserRoleAuthConvert) {
        this.sysUserRoleAuthConvert = sysUserRoleAuthConvert;
    }

    /**
     * 批量新增用户角色关联信息
     *
     * @param userId       用户业务id
     * @param roleCodeList 角色编码列表
     */
    @Override
    public void insert(String userId, List<String> roleCodeList) {
        List<SysUserRole> sysUserRoleList = new ArrayList<>();
        for (String roleCode : roleCodeList) {
            sysUserRoleList.add(SysUserRole.builder()
                    .userId(userId)
                    .roleCode(roleCode)
                    .build());
        }
        sysUserRoleService.insertBatch(sysUserRoleList);
    }

    /**
     * 删除指定用户关联的所有角色
     *
     * @param userId 根据用户业务id删除数据
     */
    @Override
    public void deleteSysUserRole(String userId) {
        sysUserRoleService.deleteSysUserRole(userId);
    }

    /**
     * 根据用户标识查询指定用户下所绑定的所有角色、权限信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回指定用户的角色、权限数据模型
     */
    @Override
    public List<SysUserRoleModel> listSysUserRoleInfo(String userId) {
        // 构建系统用户角色模型
        List<SysUserRoleModel> sysUserRoleModelList = new ArrayList<>();
        // 获取指定用户下所有的角色信息
        List<SysUserRole> sysUserRoleList = sysUserRoleService.listSysUserRole(userId);
        // 获取所有的角色编码
        List<String> roleCodeList = sysUserRoleList.stream().map(SysUserRole::getRoleCode).toList();

        // 如果当前用户具有管理员权限，则自动为用户注入系统所有角色、权限
        if (roleCodeList.contains(RoleCodeEnum.ADMINISTRATOR.getRoleCode())) {
            // 系统内所有的角色列表
            List<SysRole> sysRoleList = sysRoleService.listSysRoleFindAll();
            roleCodeList = sysRoleList.stream().map(SysRole::getRoleCode).toList();
        }

        // 根据角色编码批量查询当前角色下绑定的所有权限编码
        List<SysRoleAuth> sysRoleAuthList = sysRoleAuthService.listSysRoleAuthToRoleCode(roleCodeList);

        // 将 角色与权限关系的集合 按照 角色编码分组
        Map<String, List<SysRoleAuth>> sysRoleAuthMap = sysRoleAuthList.stream().collect(Collectors.groupingBy(SysRoleAuth::getRoleCode));
        sysRoleAuthMap.forEach((key, value) -> {
            if (CollectionUtil.isNotEmpty(value)) {
                sysUserRoleModelList.add(SysUserRoleModel.builder()
                        .roleCode(key)
                        .authInfo(sysUserRoleAuthConvert.sysRoleAuthListConvert(value))
                        .build());
            }
        });
        return sysUserRoleModelList;
    }

    /**
     * 判断当前用户是否存在指定的角色
     *
     * @param userId   用户业务id
     * @param roleCode 需要查询的角色编码
     * @return true:存在指定角色 false:不存在
     */
    @Override
    public Boolean existenceRoleCode(String userId, String roleCode) {
        return CollectionUtil.isNotEmpty(sysUserRoleService.listSysUserRoleByRoleCode(userId, Collections.singletonList(roleCode)));
    }

    /**
     * 判断当前用户是否存在指定的权限
     *
     * @param userId   用户业务id
     * @param authCode 需要查询的权限编码
     * @return true:存在指定权限 false:不存在
     */
    @Override
    public Boolean existenceAuthCode(String userId, String authCode) {
        // 根据权限编码查询所有拥有此权限的角色数据
        List<SysRoleAuth> sysRoleCodeList = sysRoleAuthService.listSysRoleAuthToAuthCode(authCode);
        // 获取其中所有的角色编码数据
        List<String> roleCodeList = sysRoleCodeList.stream().map(SysRoleAuth::getRoleCode).toList();

        // 根据用户标识与角色编码批量查询角色绑定信息
        return CollectionUtil.isNotEmpty(sysUserRoleService.listSysUserRoleByRoleCode(userId, roleCodeList));
    }
}
