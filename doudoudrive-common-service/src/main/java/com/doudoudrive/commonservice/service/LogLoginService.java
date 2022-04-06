package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.LogLogin;

/**
 * <p>登录日志服务层接口</p>
 * <p>2022-03-07 17:32</p>
 *
 * @author Dan
 **/
public interface LogLoginService {

    /**
     * 新增登录日志
     *
     * @param logLogin 需要新增的登录日志实体
     */
    void insert(LogLogin logLogin);

}
