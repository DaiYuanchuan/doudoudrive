package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.LogOp;

import java.util.List;

/**
 * <p>API操作日志服务层接口</p>
 * <p>2022-03-04 13:07</p>
 *
 * @author Dan
 **/
public interface LogOpService {

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     */
    void insert(LogOp logOp);

    /**
     * 批量新增API操作日志
     *
     * @param list 需要新增的API操作日志集合
     */
    void insertBatch(List<LogOp> list);

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
    PageResponse<LogOp> listLogOpToKey(LogOp logOp, String startTime, String endTime, Integer page, Integer pageSize);

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
    List<LogOp> listLogOp(LogOp logOp, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 返回搜索结果的总数
     *
     * @param logOp     需要查询的API操作日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(LogOp logOp, String startTime, String endTime);

}
