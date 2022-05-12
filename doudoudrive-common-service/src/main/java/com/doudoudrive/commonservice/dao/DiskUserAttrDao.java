package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.DiskUserAttr;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>用户属性数据访问层</p>
 * <p>2022-05-08 18:34</p>
 *
 * @author Dan
 **/
@Repository
public interface DiskUserAttrDao {

    /**
     * 新增用户属性
     *
     * @param diskUserAttr 需要新增的用户属性实体
     * @param tableSuffix  表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("diskUserAttr") DiskUserAttr diskUserAttr, @Param("tableSuffix") String tableSuffix);

    /**
     * 批量新增用户属性
     *
     * @param list        需要新增的用户属性集合
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<DiskUserAttr> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 删除指定用户所有属性数据
     *
     * @param userId      根据用户业务id删除数据
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer deleteUserAttr(@Param("userId") String userId, @Param("tableSuffix") String tableSuffix);

    /**
     * 修改用户属性
     *
     * @param diskUserAttr 需要进行修改的用户属性实体
     * @param tableSuffix  表后缀
     * @return 返回修改的条数
     */
    Integer update(@Param("diskUserAttr") DiskUserAttr diskUserAttr, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据用户标识查询指定用户下所有属性信息
     *
     * @param userId      根据用户业务id查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的用户属性数据集合
     */
    List<DiskUserAttr> listDiskUserAttr(@Param("userId") String userId, @Param("tableSuffix") String tableSuffix);

}
