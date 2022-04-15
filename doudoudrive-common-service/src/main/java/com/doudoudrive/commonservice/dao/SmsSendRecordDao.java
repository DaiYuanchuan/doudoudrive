package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.SmsSendRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>SMS发送记录数据访问层</p>
 * <p>2022-04-15 00:03:25</p>
 *
 * @author Dan
 */
@Repository
public interface SmsSendRecordDao {

    /**
     * 新增SMS发送记录
     *
     * @param smsSendRecord 需要新增的SMS发送记录实体
     * @param tableSuffix   表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("smsSendRecord") SmsSendRecord smsSendRecord, @Param("tableSuffix") String tableSuffix);

    /**
     * 修改SMS发送记录
     *
     * @param smsSendRecord 需要进行修改的SMS发送记录实体
     * @param tableSuffix   表后缀
     * @return 返回修改的条数
     */
    Integer update(@Param("smsSendRecord") SmsSendRecord smsSendRecord, @Param("tableSuffix") String tableSuffix);

    /**
     * 查找SMS发送记录
     *
     * @param businessId  根据业务id(businessId)查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的SMS发送记录实体
     */
    SmsSendRecord getSmsSendRecord(@Param("businessId") String businessId, @Param("tableSuffix") String tableSuffix);
}