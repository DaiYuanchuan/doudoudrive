package com.doudoudrive.commonservice.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.LogOpDao;
import com.doudoudrive.commonservice.service.LogOpService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>API操作日志服务层实现</p>
 * <p>2022-03-04 13:08</p>
 *
 * @author Dan
 **/
@Service("logOpService")
@Transactional(rollbackFor = Exception.class)
public class LogOpServiceImpl implements LogOpService {

    private LogOpDao logOpDao;

    @Autowired
    public void setLogOpDao(LogOpDao logOpDao) {
        this.logOpDao = logOpDao;
    }

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     */
    @Override
    public void insert(LogOp logOp) {
        if (ObjectUtils.isEmpty(logOp)) {
            return;
        }
        if (StringUtils.isBlank(logOp.getBusinessId())) {
            logOp.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.LOG_OP));
        }
        logOpDao.insert(logOp);
    }

    /**
     * 批量新增API操作日志
     *
     * @param list 需要新增的API操作日志集合
     */
    @Override
    public void insertBatch(List<LogOp> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(logOp -> {
            List<LogOp> logOpList = logOp.stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            for (LogOp logOpInfo : logOpList) {
                if (StringUtils.isBlank(logOpInfo.getBusinessId())) {
                    logOpInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.LOG_OP));
                }
            }
            if (CollectionUtil.isNotEmpty(logOpList)) {
                logOpDao.insertBatch(logOp);
            }
        });
    }

    /**
     * 删除API操作日志
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return logOpDao.delete(businessId);
    }

    /**
     * 批量删除API操作日志
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    @Override
    public void deleteBatch(List<String> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                logOpDao.deleteBatch(businessIdList);
            }
        });
    }

    /**
     * 修改API操作日志
     *
     * @param logOp 需要进行修改的API操作日志实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(LogOp logOp) {
        if (ObjectUtils.isEmpty(logOp) || StringUtils.isBlank(logOp.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return logOpDao.update(logOp);
    }

    /**
     * 批量修改API操作日志
     *
     * @param list 需要进行修改的API操作日志集合
     */
    @Override
    public void updateBatch(List<LogOp> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(logOp -> {
            List<LogOp> logOpList = logOp.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(logOpInfo -> StringUtils.isNotBlank(logOpInfo.getBusinessId()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(logOpList)) {
                logOpDao.updateBatch(logOpList);
            }
        });
    }

    /**
     * 查找API操作日志
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的API操作日志实体
     */
    @Override
    public LogOp getLogOp(String businessId) {
        return logOpDao.getLogOp(businessId);
    }

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的API操作日志实体
     */
    @Override
    public LogOp getLogOpToModel(String modelName, Object value) {
        return logOpDao.getLogOpToModel(modelName.replaceAll("([A-Z])", "_$1").toLowerCase(), value);
    }

    /**
     * 批量查找API操作日志
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的API操作日志数据集合
     */
    @Override
    public List<LogOp> listLogOp(List<String> list) {
        List<LogOp> logOpList = new ArrayList<>();
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> logOpList
                .addAll(logOpDao.listLogOp(businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return logOpList;
    }

    /**
     * 指定条件查找API操作日志
     *
     * @param logOp     需要查询的API操作日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return API操作日志搜索响应数据模型
     */
    @Override
    public PageResponse<LogOp> listLogOpToKey(LogOp logOp, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<LogOp> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);
        // 对象是否为空
        boolean logOpIsBlank = logOp == null || JSON.parseObject(JSONObject.toJSONString(logOp)).isEmpty();

        // 对象不为空 ，开始时间为空
        if (!logOpIsBlank && timeIsBlank) {
            response.setRows(logOpDao.listLogOpToKey(logOp, null, null, pageSql));
            response.setTotal(countSearch(logOp, null, null));
            return response;
        }

        // 对象为空 ，开始时间为空
        if (logOpIsBlank && timeIsBlank) {
            // 构建返回数据
            response.setRows(logOpDao.listLogOpToKey(null, null, null, pageSql));
            response.setTotal(countSearch(null, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            throw new IllegalArgumentException("日期格式错误");
        }

        // 构建返回数据
        response.setRows(logOpDao.listLogOpToKey(logOp, str[0], str[1], pageSql));
        response.setTotal(countSearch(logOp, str[0], str[1]));
        return response;
    }

    /**
     * 指定条件查找API操作日志
     * 返回API操作日志集合数据
     *
     * @param logOp     需要查询的API操作日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 返回API操作日志集合
     */
    @Override
    public List<LogOp> listLogOp(LogOp logOp, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listLogOpToKey(logOp, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 查找所有API操作日志
     *
     * @return 返回所有的API操作日志集合数据
     */
    @Override
    public List<LogOp> listLogOpFindAll() {
        return logOpDao.listLogOpToKey(null, null, null, null);
    }

    /**
     * 返回搜索结果的总数
     *
     * @param logOp     需要查询的API操作日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    @Override
    public Long countSearch(LogOp logOp, String startTime, String endTime) {
        return logOpDao.countSearch(logOp, startTime, endTime);
    }
}
