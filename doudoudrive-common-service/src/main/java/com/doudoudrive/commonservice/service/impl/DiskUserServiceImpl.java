package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.DiskUserDao;
import com.doudoudrive.commonservice.service.DiskUserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>用户模块服务层实现</p>
 * <p>2022-03-04 12:50</p>
 *
 * @author Dan
 **/
@Service("diskUserService")
@DataSource(DataSourceEnum.USERINFO)
public class DiskUserServiceImpl implements DiskUserService {

    private DiskUserDao diskUserDao;

    @Autowired
    public void setDiskUserDao(DiskUserDao diskUserDao) {
        this.diskUserDao = diskUserDao;
    }

    /**
     * 新增用户模块
     *
     * @param diskUser 需要新增的用户模块实体
     */
    @Override
    public void insert(DiskUser diskUser) {
        if (ObjectUtils.isEmpty(diskUser)) {
            return;
        }
        if (StringUtils.isBlank(diskUser.getBusinessId())) {
            diskUser.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER));
        }
        diskUserDao.insert(diskUser);
    }

    /**
     * 删除用户模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return diskUserDao.delete(businessId);
    }

    /**
     * 修改用户模块
     *
     * @param diskUser 需要进行修改的用户模块实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(DiskUser diskUser) {
        if (ObjectUtils.isEmpty(diskUser) || StringUtils.isBlank(diskUser.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return diskUserDao.update(diskUser);
    }

    /**
     * 查找用户模块
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的用户模块实体
     */
    @Override
    public DiskUser getDiskUser(String businessId) {
        return diskUserDao.getDiskUser(businessId);
    }
}
