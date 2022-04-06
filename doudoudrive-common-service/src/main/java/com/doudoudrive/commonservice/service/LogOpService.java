package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.LogOp;

/**
 * <p>API操作日志服务层接口</p>
 * <p>2022-03-04 13:07</p>
 *
 * @author Dan
 **/
public interface LogOpService {

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     */
    void insert(LogOp logOp);
}
