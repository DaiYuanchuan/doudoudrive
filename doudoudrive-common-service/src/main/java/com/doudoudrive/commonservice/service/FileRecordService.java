package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.FileRecord;

import java.util.List;

/**
 * <p>文件操作记录服务层接口</p>
 * <p>2022-05-26 10:57</p>
 *
 * @author Dan
 **/
public interface FileRecordService {

    /**
     * 新增文件操作记录
     *
     * @param fileRecord 需要新增的文件操作记录实体
     */
    void insert(FileRecord fileRecord);

    /**
     * 批量新增文件操作记录
     *
     * @param list 需要新增的文件操作记录集合
     */
    void insertBatch(List<FileRecord> list);

    /**
     * 删除文件操作记录
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    Integer delete(String businessId);

    /**
     * 批量删除文件操作记录
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    void deleteBatch(List<String> list);

    /**
     * 修改文件操作记录
     *
     * @param fileRecord 需要进行修改的文件操作记录实体
     * @return 返回修改的条数
     */
    Integer update(FileRecord fileRecord);

    /**
     * 批量修改文件操作记录
     *
     * @param list 需要进行修改的文件操作记录集合
     */
    void updateBatch(List<FileRecord> list);

    /**
     * 查找文件操作记录
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的文件操作记录实体
     */
    FileRecord getFileRecord(String businessId);

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的文件操作记录实体
     */
    FileRecord getFileRecordToModel(String modelName, Object value);

    /**
     * 批量查找文件操作记录
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的文件操作记录数据集合
     */
    List<FileRecord> listFileRecord(List<String> list);

    /**
     * 指定条件查找文件操作记录
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @param page       页码
     * @param pageSize   每页大小
     * @return 文件操作记录搜索响应数据模型
     */
    PageResponse<FileRecord> listFileRecordToKey(FileRecord fileRecord, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 指定条件查找文件操作记录
     * 返回文件操作记录集合数据
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @param page       页码
     * @param pageSize   每页大小
     * @return 返回文件操作记录集合
     */
    List<FileRecord> listFileRecord(FileRecord fileRecord, String startTime, String endTime, Integer page, Integer pageSize);

    /**
     * 查找所有文件操作记录
     *
     * @return 返回所有的文件操作记录集合数据
     */
    List<FileRecord> listFileRecordFindAll();

    /**
     * 返回搜索结果的总数
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    Long countSearch(FileRecord fileRecord, String startTime, String endTime);

    /**
     * 删除指定状态的文件操作记录
     *
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回删除的条数
     */
    Integer deleteAction(String action, String actionType);

    /**
     * 获取指定状态的文件操作记录，只会获取1条
     *
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 指定状态的文件操作记录对象
     */
    FileRecord getFileRecordByAction(String action, String actionType);

}
