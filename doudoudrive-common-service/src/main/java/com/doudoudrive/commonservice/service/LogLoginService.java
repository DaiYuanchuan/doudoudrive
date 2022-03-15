package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.LogLogin;

import java.util.List;

/**
 * <p>登录日志服务层接口</p>
 * <p>2022-03-07 17:32</p>
 *
 * @author Dan
 **/
public interface LogLoginService {

    /**
     * 新增登录日志
     *
     * @param logLogin 需要新增的登录日志实体
     */
    void insert(LogLogin logLogin);

    /**
     * 批量新增登录日志
     *
     * @param list 需要新增的登录日志集合
     */
    void insertBatch(List<LogLogin> list);

    /**
     * 删除登录日志
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 批量删除登录日志
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    void deleteBatch(List<String> list);

    /**
     * 修改登录日志
     *
     * @param logLogin 需要进行修改的登录日志实体
     * @return 返回修改的条数
     */
    Integer update(LogLogin logLogin);

    /**
     * 批量修改登录日志
     *
     * @param list 需要进行修改的登录日志集合
     */
    void updateBatch(List<LogLogin> list);

    /**
     * 查找登录日志
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的登录日志实体
     */
    LogLogin getLogLogin(String businessId);

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的登录日志实体
     */
    LogLogin getLogLoginToModel(String modelName, Object value);

    /**
     * 批量查找登录日志
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的登录日志数据集合
     */
    List<LogLogin> listLogLogin(List<String> list);

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
    PageResponse<LogLogin> listLogLoginToKey(LogLogin logLogin, String startTime, String endTime, Integer page, Integer pageSize);

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
    List<LogLogin> listLogLogin(LogLogin logLogin, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 查找所有登录日志
     *
     * @return 返回所有的登录日志集合数据
     */
    List<LogLogin> listLogLoginFindAll();

    /**
     * 返回搜索结果的总数
     *
     * @param logLogin  需要查询的登录日志实体
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(LogLogin logLogin, String startTime, String endTime);

}
