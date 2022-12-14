package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.springframework.stereotype.Repository;

/**
 * <p>API操作日志数据访问层</p>
 * <p>2022-03-04 13:05</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.LOG)
public interface LogOpDao {

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     * @return 返回新增的条数
     */
    Integer insert(LogOp logOp);

    // ====================================================== 截断 =====================================================

}
