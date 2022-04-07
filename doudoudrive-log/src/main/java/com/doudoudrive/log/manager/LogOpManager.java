package com.doudoudrive.log.manager;

import com.doudoudrive.common.model.pojo.LogLogin;
import com.doudoudrive.common.model.pojo.LogOp;

/**
 * <p>API操作日志的通用业务处理层接口</p>
 * <p>2022-03-13 17:44</p>
 *
 * @author Dan
 **/
public interface LogOpManager {

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     */
    void insert(LogOp logOp);

    /**
     * 新增登录日志
     *
     * @param logLogin 需要新增的登录日志实体
     */
    void insert(LogLogin logLogin);

}
