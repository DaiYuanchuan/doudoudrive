package com.doudoudrive.log.manager.impl;

import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.commonservice.service.LogOpService;
import com.doudoudrive.log.manager.LogOpManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>API操作日志的通用业务处理层实现</p>
 * <p>2022-03-13 17:44</p>
 *
 * @author Dan
 **/
@Service("logOpManager")
public class LogOpManagerImpl implements LogOpManager {

    private LogOpService logOpService;

    @Autowired
    public void setLogOpService(LogOpService logOpService) {
        this.logOpService = logOpService;
    }

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     */
    @Override
    public void insert(LogOp logOp) {
        logOpService.insert(logOp);
    }
}
