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
     * 删除API操作日志
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 批量删除API操作日志
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    void deleteBatch(List<String> list);

    /**
     * 修改API操作日志
     *
     * @param logOp 需要进行修改的API操作日志实体
     * @return 返回修改的条数
     */
    Integer update(LogOp logOp);

    /**
     * 批量修改API操作日志
     *
     * @param list 需要进行修改的API操作日志集合
     */
    void updateBatch(List<LogOp> list);

    /**
     * 查找API操作日志
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的API操作日志实体
     */
    LogOp getLogOp(String businessId);

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的API操作日志实体
     */
    LogOp getLogOpToModel(String modelName, Object value);

    /**
     * 批量查找API操作日志
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的API操作日志数据集合
     */
    List<LogOp> listLogOp(List<String> list);

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
     * 查找所有API操作日志
     *
     * @return 返回所有的API操作日志集合数据
     */
    List<LogOp> listLogOpFindAll();

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
