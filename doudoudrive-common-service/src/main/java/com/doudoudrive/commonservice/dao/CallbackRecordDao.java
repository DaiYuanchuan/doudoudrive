package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.CallbackRecord;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>外部系统回调记录数据访问层</p>
 * <p>2023-03-30 14:32:28</p>
 *
 * @author Dan
 */
@Repository
@DataSource(DataSourceEnum.LOG)
public interface CallbackRecordDao {

    /**
     * 新增外部系统回调记录
     *
     * @param callbackRecord 需要新增的外部系统回调记录实体
     * @param tableSuffix    表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("callbackRecord") CallbackRecord callbackRecord, @Param("tableSuffix") String tableSuffix);

    /**
     * 修改外部系统回调记录
     *
     * @param callbackRecord 需要进行修改的外部系统回调记录实体
     * @param tableSuffix    表后缀
     * @return 返回修改的条数
     */
    Integer update(@Param("callbackRecord") CallbackRecord callbackRecord, @Param("tableSuffix") String tableSuffix);

    /**
     * 将外部系统回调记录状态从等待修改为执行中
     *
     * @param businessId  业务id
     * @param tableSuffix 表后缀
     * @return 返回修改的条数
     */
    Integer updateStatusToExecute(@Param("businessId") String businessId, @Param("tableSuffix") String tableSuffix);

    /**
     * 查找外部系统回调记录
     *
     * @param businessId  根据业务id(businessId)查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的外部系统回调记录实体
     */
    CallbackRecord getCallbackRecord(@Param("businessId") String businessId, @Param("tableSuffix") String tableSuffix);
}
