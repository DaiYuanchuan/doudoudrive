package com.doudoudrive.commonservice.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.SysRole;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.SysRoleDao;
import com.doudoudrive.commonservice.service.SysRoleService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>系统角色管理模块服务层实现</p>
 * <p>2022-04-06 15:46</p>
 *
 * @author Dan
 **/
@Service("sysRoleService")
@DataSource(DataSourceEnum.USERINFO)
public class SysRoleServiceImpl implements SysRoleService {

    private SysRoleDao sysRoleDao;

    @Autowired
    public void setSysRoleDao(SysRoleDao sysRoleDao) {
        this.sysRoleDao = sysRoleDao;
    }

    /**
     * 新增系统角色管理模块
     *
     * @param sysRole 需要新增的系统角色管理模块实体
     */
    @Override
    public void insert(SysRole sysRole) {
        if (ObjectUtils.isEmpty(sysRole)) {
            return;
        }
        if (StringUtils.isBlank(sysRole.getBusinessId())) {
            sysRole.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_ROLE));
        }
        sysRoleDao.insert(sysRole);
    }

    /**
     * 批量新增系统角色管理模块
     *
     * @param list 需要新增的系统角色管理模块集合
     */
    @Override
    public void insertBatch(List<SysRole> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(sysRole -> {
            List<SysRole> sysRoleList = sysRole.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (SysRole sysRoleInfo : sysRoleList) {
                if (StringUtils.isBlank(sysRoleInfo.getBusinessId())) {
                    sysRoleInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_ROLE));
                }
            }
            if (CollectionUtil.isNotEmpty(sysRoleList)) {
                sysRoleDao.insertBatch(sysRoleList);
            }
        });
    }

    /**
     * 删除系统角色管理模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return sysRoleDao.delete(businessId);
    }

    /**
     * 批量删除系统角色管理模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    @Override
    public void deleteBatch(List<String> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                sysRoleDao.deleteBatch(businessIdList);
            }
        });
    }

    /**
     * 修改系统角色管理模块
     *
     * @param sysRole 需要进行修改的系统角色管理模块实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(SysRole sysRole) {
        if (ObjectUtils.isEmpty(sysRole) || StringUtils.isBlank(sysRole.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return sysRoleDao.update(sysRole);
    }

    /**
     * 批量修改系统角色管理模块
     *
     * @param list 需要进行修改的系统角色管理模块集合
     */
    @Override
    public void updateBatch(List<SysRole> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(sysRole -> {
            List<SysRole> sysRoleList = sysRole.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(sysRoleInfo -> StringUtils.isNotBlank(sysRoleInfo.getBusinessId()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(sysRoleList)) {
                sysRoleDao.updateBatch(sysRoleList);
            }
        });
    }

    /**
     * 查找系统角色管理模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的系统角色管理模块实体
     */
    @Override
    public SysRole getSysRole(String businessId) {
        return sysRoleDao.getSysRole(businessId);
    }

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value 需要查找的值
     * @return 返回查找到的系统角色管理模块实体
     */
    @Override
    public SysRole getSysRoleToModel(String modelName, Object value) {
        return sysRoleDao.getSysRoleToModel(modelName.replaceAll("([A-Z])", "_$1" ).toLowerCase(), value);
    }

    /**
     * 批量查找系统角色管理模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的系统角色管理模块数据集合
     */
    @Override
    public List<SysRole> listSysRole(List<String> list) {
        List<SysRole> sysRoleList = new ArrayList<>();
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> sysRoleList
                .addAll(sysRoleDao.listSysRole(businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return sysRoleList;
    }

    /**
     * 指定条件查找系统角色管理模块
     *
     * @param sysRole 需要查询的系统角色管理模块实体
     * @param startTime    需要查询的开始时间(如果有)
     * @param endTime      需要查询的结束时间(如果有)
     * @param page          页码
     * @param pageSize      每页大小
     * @return 系统角色管理模块搜索响应数据模型
     */
    @Override
    public PageResponse<SysRole> listSysRoleToKey(SysRole sysRole, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<SysRole> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);
        // 对象是否为空
        boolean sysRoleIsBlank = sysRole == null || JSON.parseObject(JSONObject.toJSONString(sysRole)).isEmpty();

        // 对象不为空 ，开始时间为空
        if (!sysRoleIsBlank && timeIsBlank) {
            response.setRows(sysRoleDao.listSysRoleToKey(sysRole, null, null, pageSql));
            response.setTotal(countSearch(sysRole, null, null));
            return response;
        }

        // 对象为空 ，开始时间为空
        if (sysRoleIsBlank && timeIsBlank) {
            // 构建返回数据
            response.setRows(sysRoleDao.listSysRoleToKey(null, null, null, pageSql));
            response.setTotal(countSearch(null, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 构建返回数据
        response.setRows(sysRoleDao.listSysRoleToKey(sysRole, str[0], str[1], pageSql));
        response.setTotal(countSearch(sysRole, str[0], str[1]));
        return response;
    }

    /**
     * 指定条件查找系统角色管理模块
     * 返回系统角色管理模块集合数据
     *
     * @param sysRole 需要查询的系统角色管理模块实体
     * @param startTime    需要查询的开始时间(如果有)
     * @param endTime      需要查询的结束时间(如果有)
     * @param page          页码
     * @param pageSize      每页大小
     * @return 返回系统角色管理模块集合
     */
    @Override
    public List<SysRole> listSysRole(SysRole sysRole, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listSysRoleToKey(sysRole, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 查找所有系统角色管理模块
     *
     * @return 返回所有的系统角色管理模块集合数据
     */
    @Override
    public List<SysRole> listSysRoleFindAll() {
        return sysRoleDao.listSysRoleToKey(null, null, null, null);
    }

    /**
     * 返回搜索结果的总数
     *
     * @param sysRole 需要查询的系统角色管理模块实体
     * @param startTime    需要查询的开始时间(如果有)
     * @param endTime      需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    @Override
    public Long countSearch(SysRole sysRole, String startTime, String endTime) {
        return sysRoleDao.countSearch(sysRole, startTime, endTime);
    }

}
