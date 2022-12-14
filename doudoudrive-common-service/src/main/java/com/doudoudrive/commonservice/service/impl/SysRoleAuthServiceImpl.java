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
import com.doudoudrive.common.model.pojo.SysRoleAuth;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.SysRoleAuthDao;
import com.doudoudrive.commonservice.service.SysRoleAuthService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>角色、权限关联模块服务层实现</p>
 * <p>2022-04-06 15:35</p>
 *
 * @author Dan
 **/
@Service("sysRoleAuthService")
public class SysRoleAuthServiceImpl implements SysRoleAuthService {

    private SysRoleAuthDao sysRoleAuthDao;

    @Autowired
    public void setSysRoleAuthDao(SysRoleAuthDao sysRoleAuthDao) {
        this.sysRoleAuthDao = sysRoleAuthDao;
    }

    /**
     * 新增角色、权限关联模块
     *
     * @param sysRoleAuth 需要新增的角色、权限关联模块实体
     */
    @Override
    public void insert(SysRoleAuth sysRoleAuth) {
        if (ObjectUtils.isEmpty(sysRoleAuth)) {
            return;
        }
        if (StringUtils.isBlank(sysRoleAuth.getBusinessId())) {
            sysRoleAuth.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_ROLE_AUTH));
        }
        sysRoleAuthDao.insert(sysRoleAuth);
    }

    /**
     * 批量新增角色、权限关联模块
     *
     * @param list 需要新增的角色、权限关联模块集合
     */
    @Override
    public void insertBatch(List<SysRoleAuth> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(sysRoleAuth -> {
            List<SysRoleAuth> sysRoleAuthList = sysRoleAuth.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (SysRoleAuth sysRoleAuthInfo : sysRoleAuthList) {
                if (StringUtils.isBlank(sysRoleAuthInfo.getBusinessId())) {
                    sysRoleAuthInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_ROLE_AUTH));
                }
            }
            if (CollectionUtil.isNotEmpty(sysRoleAuthList)) {
                sysRoleAuthDao.insertBatch(sysRoleAuthList);
            }
        });
    }

    /**
     * 删除角色、权限关联模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return sysRoleAuthDao.delete(businessId);
    }

    /**
     * 批量删除角色、权限关联模块
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    @Override
    public void deleteBatch(List<String> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).toList();
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                sysRoleAuthDao.deleteBatch(businessIdList);
            }
        });
    }

    /**
     * 修改角色、权限关联模块
     *
     * @param sysRoleAuth 需要进行修改的角色、权限关联模块实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(SysRoleAuth sysRoleAuth) {
        if (ObjectUtils.isEmpty(sysRoleAuth) || StringUtils.isBlank(sysRoleAuth.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return sysRoleAuthDao.update(sysRoleAuth);
    }

    /**
     * 批量修改角色、权限关联模块
     *
     * @param list 需要进行修改的角色、权限关联模块集合
     */
    @Override
    public void updateBatch(List<SysRoleAuth> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(sysRoleAuth -> {
            List<SysRoleAuth> sysRoleAuthList = sysRoleAuth.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(sysRoleAuthInfo -> StringUtils.isNotBlank(sysRoleAuthInfo.getBusinessId()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(sysRoleAuthList)) {
                sysRoleAuthDao.updateBatch(sysRoleAuthList);
            }
        });
    }

    /**
     * 查找角色、权限关联模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的角色、权限关联模块实体
     */
    @Override
    public SysRoleAuth getSysRoleAuth(String businessId) {
        return sysRoleAuthDao.getSysRoleAuth(businessId);
    }

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的角色、权限关联模块实体
     */
    @Override
    public SysRoleAuth getSysRoleAuthToModel(String modelName, Object value) {
        return sysRoleAuthDao.getSysRoleAuthToModel(modelName.replaceAll("([A-Z])", "_$1").toLowerCase(), value);
    }

    /**
     * 批量查找角色、权限关联模块
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的角色、权限关联模块数据集合
     */
    @Override
    public List<SysRoleAuth> listSysRoleAuth(List<String> list) {
        List<SysRoleAuth> sysRoleAuthList = new ArrayList<>();
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> sysRoleAuthList
                .addAll(sysRoleAuthDao.listSysRoleAuth(businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return sysRoleAuthList;
    }

    /**
     * 指定条件查找角色、权限关联模块
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @param page        页码
     * @param pageSize    每页大小
     * @return 角色、权限关联模块搜索响应数据模型
     */
    @Override
    public PageResponse<SysRoleAuth> listSysRoleAuthToKey(SysRoleAuth sysRoleAuth, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<SysRoleAuth> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);
        // 对象是否为空
        boolean sysRoleAuthIsBlank = sysRoleAuth == null || JSON.parseObject(JSONObject.toJSONString(sysRoleAuth)).isEmpty();

        // 对象不为空 ，开始时间为空
        if (!sysRoleAuthIsBlank && timeIsBlank) {
            response.setRows(sysRoleAuthDao.listSysRoleAuthToKey(sysRoleAuth, null, null, pageSql));
            response.setTotal(countSearch(sysRoleAuth, null, null));
            return response;
        }

        // 对象为空 ，开始时间为空
        if (sysRoleAuthIsBlank && timeIsBlank) {
            // 构建返回数据
            response.setRows(sysRoleAuthDao.listSysRoleAuthToKey(null, null, null, pageSql));
            response.setTotal(countSearch(null, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 构建返回数据
        response.setRows(sysRoleAuthDao.listSysRoleAuthToKey(sysRoleAuth, str[0], str[1], pageSql));
        response.setTotal(countSearch(sysRoleAuth, str[0], str[1]));
        return response;
    }

    /**
     * 指定条件查找角色、权限关联模块
     * 返回角色、权限关联模块集合数据
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @param page        页码
     * @param pageSize    每页大小
     * @return 返回角色、权限关联模块集合
     */
    @Override
    public List<SysRoleAuth> listSysRoleAuth(SysRoleAuth sysRoleAuth, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listSysRoleAuthToKey(sysRoleAuth, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 查找所有角色、权限关联模块
     *
     * @return 返回所有的角色、权限关联模块集合数据
     */
    @Override
    public List<SysRoleAuth> listSysRoleAuthFindAll() {
        return sysRoleAuthDao.listSysRoleAuthToKey(null, null, null, null);
    }

    /**
     * 返回搜索结果的总数
     *
     * @param sysRoleAuth 需要查询的角色、权限关联模块实体
     * @param startTime   需要查询的开始时间(如果有)
     * @param endTime     需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    @Override
    public Long countSearch(SysRoleAuth sysRoleAuth, String startTime, String endTime) {
        return sysRoleAuthDao.countSearch(sysRoleAuth, startTime, endTime);
    }

    /**
     * 根据角色编码批量查询当前角色下绑定的所有权限编码
     *
     * @param roleCodeList 角色编码列表
     * @return 返回指定角色下所有的权限编码
     */
    @Override
    public List<SysRoleAuth> listSysRoleAuthToRoleCode(List<String> roleCodeList) {
        List<SysRoleAuth> sysRoleAuthList = new ArrayList<>();
        CollectionUtil.collectionCutting(roleCodeList, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(roleCode -> sysRoleAuthList
                .addAll(sysRoleAuthDao.listSysRoleAuthToRoleCode(roleCode.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return sysRoleAuthList;
    }

    /**
     * 根据权限编码查询所有拥有此权限的角色数据
     *
     * @param authCode 权限编码
     * @return 返回所有拥有指定权限的角色数据
     */
    @Override
    public List<SysRoleAuth> listSysRoleAuthToAuthCode(String authCode) {
        if (StringUtils.isBlank(authCode)) {
            return new ArrayList<>();
        }
        return sysRoleAuthDao.listSysRoleAuthToAuthCode(authCode);
    }
}
