package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.LogOp;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.LogOpDao;
import com.doudoudrive.commonservice.service.LogOpService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>API操作日志服务层实现</p>
 * <p>2022-03-04 13:08</p>
 *
 * @author Dan
 **/
@Service("logOpService")
public class LogOpServiceImpl implements LogOpService {

    private LogOpDao logOpDao;

    @Autowired
    public void setLogOpDao(LogOpDao logOpDao) {
        this.logOpDao = logOpDao;
    }

    /**
     * 新增API操作日志
     *
     * @param logOp 需要新增的API操作日志实体
     */
    @Override
    public void insert(LogOp logOp) {
        if (ObjectUtils.isEmpty(logOp)) {
            return;
        }
        if (StringUtils.isBlank(logOp.getBusinessId())) {
            logOp.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.LOG_OP));
        }
        logOpDao.insert(logOp);
    }
}
