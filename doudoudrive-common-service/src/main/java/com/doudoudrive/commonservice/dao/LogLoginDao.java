package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.LogLogin;
import org.springframework.stereotype.Repository;

/**
 * <p>登录日志数据访问层</p>
 * <p>2022-03-06 19:58</p>
 *
 * @author Dan
 **/
@Repository
public interface LogLoginDao {

    /**
     * 新增登录日志
     *
     * @param logLogin 需要新增的登录日志实体
     * @return 返回新增的条数
     */
    Integer insert(LogLogin logLogin);

}
