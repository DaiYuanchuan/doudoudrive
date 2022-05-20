package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.DiskFile;

import java.util.List;

/**
 * <p>用户文件模块服务层接口</p>
 * <p>2022-05-19 23:22</p>
 *
 * @author Dan
 **/
public interface DiskFileService {

    /**
     * 新增用户文件模块
     *
     * @param diskFile 需要新增的用户文件模块实体
     */
    void insert(DiskFile diskFile);

    /**
     * 批量新增用户文件模块
     *
     * @param list 需要新增的用户文件模块集合
     */
    void insertBatch(List<DiskFile> list);

    /**
     * 删除用户文件模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @param userId     业务id对应的用户标识
     * @return 返回删除的条数
     */
    Integer delete(String businessId, String userId);

    /**
     * 批量删除用户文件模块
     *
     * @param list   需要删除的业务id(businessId)数据集合
     * @param userId 业务id对应的用户标识
     */
    void deleteBatch(List<String> list, String userId);

    /**
     * 修改用户文件模块
     *
     * @param diskFile 需要进行修改的用户文件模块实体
     * @return 返回修改的条数
     */
    Integer update(DiskFile diskFile);

    /**
     * 批量修改用户文件模块
     *
     * @param list 需要进行修改的用户文件模块集合
     */
    void updateBatch(List<DiskFile> list);

    /**
     * 指定条件查找用户文件模块
     *
     * @param diskFile  需要查询的用户文件模块实体(这里不能为NULL，且必须包含用户id)
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 用户文件模块搜索响应数据模型
     */
    PageResponse<DiskFile> listDiskFileToKey(DiskFile diskFile, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 指定条件查找用户文件模块
     * 返回用户文件模块集合数据
     *
     * @param diskFile  需要查询的用户文件模块实体(这里不能为NULL，且必须包含用户id)
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 返回用户文件模块集合
     */
    List<DiskFile> listDiskFile(DiskFile diskFile, String startTime, String endTime, Integer page, Integer pageSize);
}
