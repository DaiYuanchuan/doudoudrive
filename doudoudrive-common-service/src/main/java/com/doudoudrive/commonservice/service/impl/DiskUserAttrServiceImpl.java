package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.DiskUserAttr;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.DiskUserAttrDao;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>用户属性模块服务层实现</p>
 * <p>2022-05-12 14:56</p>
 *
 * @author Dan
 **/
@Service("diskUserAttrService")
@DataSource(DataSourceEnum.USERINFO)
public class DiskUserAttrServiceImpl implements DiskUserAttrService {

    private DiskUserAttrDao diskUserAttrDao;

    @Autowired
    public void setDiskUserAttrDao(DiskUserAttrDao diskUserAttrDao) {
        this.diskUserAttrDao = diskUserAttrDao;
    }

    /**
     * 新增用户属性模块
     *
     * @param diskUserAttr 需要新增的用户属性模块实体
     */
    @Override
    public void insert(DiskUserAttr diskUserAttr) {
        if (ObjectUtils.isEmpty(diskUserAttr) || StringUtils.isBlank(diskUserAttr.getUserId())) {
            return;
        }
        if (StringUtils.isBlank(diskUserAttr.getBusinessId())) {
            diskUserAttr.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER_ATTR));
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskUserAttr.getUserId(), ConstantConfig.TableSuffix.DISK_USER_ATTR);
        diskUserAttrDao.insert(diskUserAttr, tableSuffix);
    }

    /**
     * 批量新增用户属性模块
     *
     * @param list 需要新增的用户属性模块集合
     */
    @Override
    public void insertBatch(List<DiskUserAttr> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(diskUserAttr -> {
            List<DiskUserAttr> diskUserAttrList = diskUserAttr.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (DiskUserAttr diskUserAttrInfo : diskUserAttrList) {
                if (StringUtils.isBlank(diskUserAttrInfo.getBusinessId())) {
                    diskUserAttrInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER_ATTR));
                }
            }
            // 将 用户属性关系的集合 按照 用户分组
            Map<String, List<DiskUserAttr>> diskUserAttrMap = diskUserAttrList.stream().collect(Collectors.groupingBy(DiskUserAttr::getUserId));
            diskUserAttrMap.forEach((key, value) -> {
                // 获取表后缀
                String tableSuffix = SequenceUtil.tableSuffix(key, ConstantConfig.TableSuffix.DISK_USER_ATTR);
                if (CollectionUtil.isNotEmpty(value)) {
                    diskUserAttrDao.insertBatch(value, tableSuffix);
                }
            });
        });
    }

    /**
     * 删除指定用户所有属性数据
     *
     * @param userId 根据用户业务id删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer deleteUserAttr(String userId) {
        if (StringUtils.isBlank(userId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_USER_ATTR);
        return diskUserAttrDao.deleteUserAttr(userId, tableSuffix);
    }

    /**
     * 修改用户属性模块
     *
     * @param diskUserAttr 需要进行修改的用户属性模块实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(DiskUserAttr diskUserAttr) {
        if (ObjectUtils.isEmpty(diskUserAttr) || StringUtils.isBlank(diskUserAttr.getBusinessId())
                || StringUtils.isBlank(diskUserAttr.getUserId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskUserAttr.getUserId(), ConstantConfig.TableSuffix.DISK_USER_ATTR);
        return diskUserAttrDao.update(diskUserAttr, tableSuffix);
    }

    /**
     * 根据用户标识查询指定用户下所有属性信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户属性数据集合
     */
    @Override
    public List<DiskUserAttr> listDiskUserAttr(String userId) {
        if (StringUtils.isBlank(userId)) {
            return new ArrayList<>();
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_USER_ATTR);
        return diskUserAttrDao.listDiskUserAttr(userId, tableSuffix);
    }
}
