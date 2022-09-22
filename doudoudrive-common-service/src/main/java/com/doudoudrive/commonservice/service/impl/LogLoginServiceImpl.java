package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.LogLogin;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.LogLoginDao;
import com.doudoudrive.commonservice.service.LogLoginService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>登录日志服务层实现</p>
 * <p>2022-03-07 19:34</p>
 *
 * @author Dan
 **/
@Service("logLoginService")
public class LogLoginServiceImpl implements LogLoginService {

    private LogLoginDao logLoginDao;

    @Autowired
    public void setLogLoginDao(LogLoginDao logLoginDao) {
        this.logLoginDao = logLoginDao;
    }

    /**
     * 新增登录日志
     *
     * @param logLogin 需要新增的登录日志实体
     */
    @Override
    public void insert(LogLogin logLogin) {
        if (ObjectUtils.isEmpty(logLogin)) {
            return;
        }
        if (StringUtils.isBlank(logLogin.getBusinessId())) {
            logLogin.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.LOG_LOGIN));
        }
        logLoginDao.insert(logLogin);
    }
}
