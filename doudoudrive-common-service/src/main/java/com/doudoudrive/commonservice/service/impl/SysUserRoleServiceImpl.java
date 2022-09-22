package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.SysUserRole;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.SysUserRoleDao;
import com.doudoudrive.commonservice.service.SysUserRoleService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>用户、角色关联模块服务层实现</p>
 * <p>2022-04-06 15:48</p>
 *
 * @author Dan
 **/
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    public void setSysUserRoleDao(SysUserRoleDao sysUserRoleDao) {
        this.sysUserRoleDao = sysUserRoleDao;
    }

    /**
     * 新增用户、角色关联模块
     *
     * @param sysUserRole 需要新增的用户、角色关联模块实体
     */
    @Override
    public void insert(SysUserRole sysUserRole) {
        if (ObjectUtils.isEmpty(sysUserRole)) {
            return;
        }
        if (StringUtils.isBlank(sysUserRole.getBusinessId())) {
            sysUserRole.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_USER_ROLE));
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(sysUserRole.getUserId(), ConstantConfig.TableSuffix.SYS_USER_ROLE);
        sysUserRoleDao.insert(sysUserRole, tableSuffix);
    }

    /**
     * 批量新增用户、角色关联模块
     *
     * @param list 需要新增的用户、角色关联模块集合
     */
    @Override
    public void insertBatch(List<SysUserRole> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(sysUserRole -> {
            List<SysUserRole> sysUserRoleList = sysUserRole.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (SysUserRole sysUserRoleInfo : sysUserRoleList) {
                if (StringUtils.isBlank(sysUserRoleInfo.getBusinessId())) {
                    sysUserRoleInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_USER_ROLE));
                }
            }
            // 将 用户与角色关系的集合 按照 用户分组
            Map<String, List<SysUserRole>> sysUserRoleMap = sysUserRoleList.stream().collect(Collectors.groupingBy(SysUserRole::getUserId));
            sysUserRoleMap.forEach((key, value) -> {
                // 获取表后缀
                String tableSuffix = SequenceUtil.tableSuffix(key, ConstantConfig.TableSuffix.SYS_USER_ROLE);
                if (CollectionUtil.isNotEmpty(value)) {
                    sysUserRoleDao.insertBatch(value, tableSuffix);
                }
            });
        });
    }

    /**
     * 删除指定用户关联的所有角色
     *
     * @param userId 根据用户业务id删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer deleteSysUserRole(String userId) {
        if (StringUtils.isBlank(userId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.SYS_USER_ROLE);
        return sysUserRoleDao.deleteSysUserRole(userId, tableSuffix);
    }

    /**
     * 根据用户标识查询指定用户下所有绑定的角色信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户、角色关联模块实体
     */
    @Override
    public List<SysUserRole> listSysUserRole(String userId) {
        if (StringUtils.isBlank(userId)) {
            return new ArrayList<>();
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.SYS_USER_ROLE);
        // 根据用户标识查询指定用户下所有绑定的角色信息
        return sysUserRoleDao.listSysUserRole(userId, tableSuffix);
    }

    /**
     * 根据用户标识与系统角色编码批量查询指定用户的角色绑定信息
     *
     * @param userId       根据用户业务id查找
     * @param roleCodeList 角色编码集合
     * @return 返回查找到的用户、角色关联模块实体数据集合
     */
    @Override
    public List<SysUserRole> listSysUserRoleByRoleCode(String userId, List<String> roleCodeList) {
        if (StringUtils.isBlank(userId)) {
            return new ArrayList<>();
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.SYS_USER_ROLE);

        List<SysUserRole> sysUserRoleList = new ArrayList<>();
        CollectionUtil.collectionCutting(roleCodeList, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(roleCode -> sysUserRoleList
                .addAll(sysUserRoleDao.listSysUserRoleByRoleCode(userId, roleCode.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()), tableSuffix)));
        return sysUserRoleList;
    }
}
