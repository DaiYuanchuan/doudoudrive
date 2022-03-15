package com.doudoudrive.commonservice.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.LogLogin;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.LogLoginDao;
import com.doudoudrive.commonservice.service.LogLoginService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>登录日志服务层实现</p>
 * <p>2022-03-07 19:34</p>
 *
 * @author Dan
 **/
@Service("logLoginService")
@Transactional(rollbackFor = Exception.class)
public class LogLoginServiceImpl implements LogLoginService {

    private LogLoginDao logLoginDao;

    @Autowired
    public void setLogLoginDao(LogLoginDao logLoginDao) {
        this.logLoginDao = logLoginDao;
    }

    /**
     * 新增登录日志
     *
     * @param logLogin 需要新增的登录日志实体
     */
    @Override
    public void insert(LogLogin logLogin) {
        if (ObjectUtils.isEmpty(logLogin)) {
            return;
        }
        if (StringUtils.isBlank(logLogin.getBusinessId())) {
            logLogin.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.LOG_LOGIN));
        }
        logLoginDao.insert(logLogin);
    }

    /**
     * 批量新增登录日志
     *
     * @param list 需要新增的登录日志集合
     */
    @Override
    public void insertBatch(List<LogLogin> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(logLogin -> {
            List<LogLogin> logLoginList = logLogin.stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            for (LogLogin logLoginInfo : logLoginList) {
                if (StringUtils.isBlank(logLoginInfo.getBusinessId())) {
                    logLoginInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.LOG_LOGIN));
                }
            }
            if (CollectionUtil.isNotEmpty(logLoginList)) {
                logLoginDao.insertBatch(logLogin);
            }
        });
    }

    /**
     * 删除登录日志
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return logLoginDao.delete(businessId);
    }

    /**
     * 批量删除登录日志
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    @Override
    public void deleteBatch(List<String> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                logLoginDao.deleteBatch(businessIdList);
            }
        });
    }

    /**
     * 修改登录日志
     *
     * @param logLogin 需要进行修改的登录日志实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(LogLogin logLogin) {
        if (ObjectUtils.isEmpty(logLogin) || StringUtils.isBlank(logLogin.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return logLoginDao.update(logLogin);
    }

    /**
     * 批量修改登录日志
     *
     * @param list 需要进行修改的登录日志集合
     */
    @Override
    public void updateBatch(List<LogLogin> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(logLogin -> {
            List<LogLogin> logLoginList = logLogin.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(logLoginInfo -> StringUtils.isNotBlank(logLoginInfo.getBusinessId()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(logLoginList)) {
                logLoginDao.updateBatch(logLoginList);
            }
        });
    }

    /**
     * 查找登录日志
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的登录日志实体
     */
    @Override
    public LogLogin getLogLogin(String businessId) {
        return logLoginDao.getLogLogin(businessId);
    }

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的登录日志实体
     */
    @Override
    public LogLogin getLogLoginToModel(String modelName, Object value) {
        return logLoginDao.getLogLoginToModel(modelName.replaceAll("([A-Z])", "_$1").toLowerCase(), value);
    }

    /**
     * 批量查找登录日志
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的登录日志数据集合
     */
    @Override
    public List<LogLogin> listLogLogin(List<String> list) {
        List<LogLogin> logLoginList = new ArrayList<>();
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> logLoginList
                .addAll(logLoginDao.listLogLogin(businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return logLoginList;
    }

    /**
     * 指定条件查找登录日志
     *
     * @param logLogin  需要查询的登录日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 登录日志搜索响应数据模型
     */
    @Override
    public PageResponse<LogLogin> listLogLoginToKey(LogLogin logLogin, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<LogLogin> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);
        // 对象是否为空
        boolean logLoginIsBlank = logLogin == null || JSON.parseObject(JSONObject.toJSONString(logLogin)).isEmpty();

        // 对象不为空 ，开始时间为空
        if (!logLoginIsBlank && timeIsBlank) {
            response.setRows(logLoginDao.listLogLoginToKey(logLogin, null, null, pageSql));
            response.setTotal(countSearch(logLogin, null, null));
            return response;
        }

        // 对象为空 ，开始时间为空
        if (logLoginIsBlank && timeIsBlank) {
            // 构建返回数据
            response.setRows(logLoginDao.listLogLoginToKey(null, null, null, pageSql));
            response.setTotal(countSearch(null, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            throw new IllegalArgumentException("日期格式错误");
        }

        // 构建返回数据
        response.setRows(logLoginDao.listLogLoginToKey(logLogin, str[0], str[1], pageSql));
        response.setTotal(countSearch(logLogin, str[0], str[1]));
        return response;
    }

    /**
     * 指定条件查找登录日志
     * 返回登录日志集合数据
     *
     * @param logLogin  需要查询的登录日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 返回登录日志集合
     */
    @Override
    public List<LogLogin> listLogLogin(LogLogin logLogin, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listLogLoginToKey(logLogin, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 查找所有登录日志
     *
     * @return 返回所有的登录日志集合数据
     */
    @Override
    public List<LogLogin> listLogLoginFindAll() {
        return logLoginDao.listLogLoginToKey(null, null, null, null);
    }

    /**
     * 返回搜索结果的总数
     *
     * @param logLogin  需要查询的登录日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    @Override
    public Long countSearch(LogLogin logLogin, String startTime, String endTime) {
        return logLoginDao.countSearch(logLogin, startTime, endTime);
    }

}
