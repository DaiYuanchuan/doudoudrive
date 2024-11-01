package com.doudoudrive.commonservice.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
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

import java.util.List;

/**
 * <p>API操作日志服务层实现</p>
 * <p>2022-03-04 13:08</p>
 *
 * @author Dan
 **/
@Service("logOpService")
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
            List<LogOp> logOpList = logOp.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (LogOp logOpInfo : logOpList) {
                if (StringUtils.isBlank(logOpInfo.getBusinessId())) {
                    logOpInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.LOG_OP));
                }
            }
            if (CollectionUtil.isNotEmpty(logOpList)) {
                logOpDao.insertBatch(logOpList);
            }
        });
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
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
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
