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
import com.doudoudrive.common.model.pojo.SysAuthorization;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.SysAuthorizationDao;
import com.doudoudrive.commonservice.service.SysAuthorizationService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>系统权限管理模块服务层实现</p>
 * <p>2022-04-06 15:21</p>
 *
 * @author Dan
 **/
@Service("sysAuthorizationService")
@DataSource(DataSourceEnum.USERINFO)
public class SysAuthorizationServiceImpl implements SysAuthorizationService {

    private SysAuthorizationDao sysAuthorizationDao;

    @Autowired
    public void setSysAuthorizationDao(SysAuthorizationDao sysAuthorizationDao) {
        this.sysAuthorizationDao = sysAuthorizationDao;
    }

    /**
     * 新增系统权限管理模块
     *
     * @param sysAuthorization 需要新增的系统权限管理模块实体
     */
    @Override
    public void insert(SysAuthorization sysAuthorization) {
        if (ObjectUtils.isEmpty(sysAuthorization)) {
            return;
        }
        if (StringUtils.isBlank(sysAuthorization.getBusinessId())) {
            sysAuthorization.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_AUTH));
        }
        sysAuthorizationDao.insert(sysAuthorization);
    }

    /**
     * 批量新增系统权限管理模块
     *
     * @param list 需要新增的系统权限管理模块集合
     */
    @Override
    public void insertBatch(List<SysAuthorization> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(sysAuthorization -> {
            List<SysAuthorization> sysAuthorizationList = sysAuthorization.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (SysAuthorization sysAuthorizationInfo : sysAuthorizationList) {
                if (StringUtils.isBlank(sysAuthorizationInfo.getBusinessId())) {
                    sysAuthorizationInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_AUTH));
                }
            }
            if (CollectionUtil.isNotEmpty(sysAuthorizationList)) {
                sysAuthorizationDao.insertBatch(sysAuthorizationList);
            }
        });
    }

    /**
     * 删除系统权限管理模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return sysAuthorizationDao.delete(businessId);
    }

    /**
     * 批量删除系统权限管理模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    @Override
    public void deleteBatch(List<String> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).toList();
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                sysAuthorizationDao.deleteBatch(businessIdList);
            }
        });
    }

    /**
     * 修改系统权限管理模块
     *
     * @param sysAuthorization 需要进行修改的系统权限管理模块实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(SysAuthorization sysAuthorization) {
        if (ObjectUtils.isEmpty(sysAuthorization) || StringUtils.isBlank(sysAuthorization.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return sysAuthorizationDao.update(sysAuthorization);
    }

    /**
     * 批量修改系统权限管理模块
     *
     * @param list 需要进行修改的系统权限管理模块集合
     */
    @Override
    public void updateBatch(List<SysAuthorization> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(sysAuthorization -> {
            List<SysAuthorization> sysAuthorizationList = sysAuthorization.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(sysAuthorizationInfo -> StringUtils.isNotBlank(sysAuthorizationInfo.getBusinessId()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(sysAuthorizationList)) {
                sysAuthorizationDao.updateBatch(sysAuthorizationList);
            }
        });
    }

    /**
     * 查找系统权限管理模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的系统权限管理模块实体
     */
    @Override
    public SysAuthorization getSysAuthorization(String businessId) {
        return sysAuthorizationDao.getSysAuthorization(businessId);
    }

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的系统权限管理模块实体
     */
    @Override
    public SysAuthorization getSysAuthorizationToModel(String modelName, Object value) {
        return sysAuthorizationDao.getSysAuthorizationToModel(modelName.replaceAll("([A-Z])", "_$1").toLowerCase(), value);
    }

    /**
     * 批量查找系统权限管理模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的系统权限管理模块数据集合
     */
    @Override
    public List<SysAuthorization> listSysAuthorization(List<String> list) {
        List<SysAuthorization> sysAuthorizationList = new ArrayList<>();
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> sysAuthorizationList
                .addAll(sysAuthorizationDao.listSysAuthorization(businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return sysAuthorizationList;
    }

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
    @Override
    public PageResponse<SysAuthorization> listSysAuthorizationToKey(SysAuthorization sysAuthorization, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<SysAuthorization> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);
        // 对象是否为空
        boolean sysAuthorizationIsBlank = sysAuthorization == null || JSON.parseObject(JSONObject.toJSONString(sysAuthorization)).isEmpty();

        // 对象不为空 ，开始时间为空
        if (!sysAuthorizationIsBlank && timeIsBlank) {
            response.setRows(sysAuthorizationDao.listSysAuthorizationToKey(sysAuthorization, null, null, pageSql));
            response.setTotal(countSearch(sysAuthorization, null, null));
            return response;
        }

        // 对象为空 ，开始时间为空
        if (sysAuthorizationIsBlank && timeIsBlank) {
            // 构建返回数据
            response.setRows(sysAuthorizationDao.listSysAuthorizationToKey(null, null, null, pageSql));
            response.setTotal(countSearch(null, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 构建返回数据
        response.setRows(sysAuthorizationDao.listSysAuthorizationToKey(sysAuthorization, str[0], str[1], pageSql));
        response.setTotal(countSearch(sysAuthorization, str[0], str[1]));
        return response;
    }

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
    @Override
    public List<SysAuthorization> listSysAuthorization(SysAuthorization sysAuthorization, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listSysAuthorizationToKey(sysAuthorization, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 查找所有系统权限管理模块
     *
     * @return 返回所有的系统权限管理模块集合数据
     */
    @Override
    public List<SysAuthorization> listSysAuthorizationFindAll() {
        return sysAuthorizationDao.listSysAuthorizationToKey(null, null, null, null);
    }

    /**
     * 返回搜索结果的总数
     *
     * @param sysAuthorization 需要查询的系统权限管理模块实体
     * @param startTime        需要查询的开始时间(如果有)
     * @param endTime          需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    @Override
    public Long countSearch(SysAuthorization sysAuthorization, String startTime, String endTime) {
        return sysAuthorizationDao.countSearch(sysAuthorization, startTime, endTime);
    }
}
