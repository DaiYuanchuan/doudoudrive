package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.DiskUserAttr;

import java.util.List;
import java.util.Map;

/**
 * <p>用户属性模块服务层接口</p>
 * <p>2022-05-12 14:52</p>
 *
 * @author Dan
 **/
public interface DiskUserAttrService {

    /**
     * 新增用户属性模块
     *
     * @param diskUserAttr 需要新增的用户属性模块实体
     */
    void insert(DiskUserAttr diskUserAttr);

    /**
     * 批量新增用户属性模块
     *
     * @param list 需要新增的用户属性模块集合
     */
    void insertBatch(List<DiskUserAttr> list);

    /**
     * 删除指定用户所有属性数据
     *
     * @param userId 根据用户业务id删除数据
     * @return 返回删除的条数
     */
    Integer deleteUserAttr(String userId);

    /**
     * 修改用户属性模块
     *
     * @param diskUserAttr 需要进行修改的用户属性模块实体
     * @return 返回修改的条数
     */
    Integer update(DiskUserAttr diskUserAttr);

    /**
     * 根据用户标识查询指定用户下所有属性信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户属性数据Map对象
     */
    Map<String, String> listDiskUserAttr(String userId);

}
