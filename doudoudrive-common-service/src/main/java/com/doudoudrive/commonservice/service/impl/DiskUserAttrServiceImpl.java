package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.pojo.DiskUserAttr;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.DiskUserAttrDao;
import com.doudoudrive.commonservice.service.DiskUserAttrService;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
     * 原子性服务，扣除指定字段的数量
     *
     * @param userId       需要进行操作的用户标识
     * @param userAttrEnum 需要扣除的字段属性枚举值
     * @param size         需要扣除的数量
     * @return 返回修改的条数，根据返回值判断是否修改成功
     */
    @Override
    public Integer deducted(String userId, ConstantConfig.UserAttrEnum userAttrEnum, String size) {
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_USER_ATTR);
        try {
            // 获取更新结果
            return diskUserAttrDao.deducted(userId, userAttrEnum.param, size, tableSuffix);
        } catch (Exception e) {
            return NumberConstant.INTEGER_ZERO;
        }
    }

    /**
     * 原子性服务，增加指定字段的数量
     *
     * @param userId       需要进行操作的用户标识
     * @param userAttrEnum 需要扣除的字段属性枚举值
     * @param size         需要扣除的数量
     * @return 返回修改的条数，根据返回值判断是否修改成功
     */
    @Override
    public Integer increase(String userId, ConstantConfig.UserAttrEnum userAttrEnum, String size) {
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_USER_ATTR);
        try {
            // 获取更新结果
            return diskUserAttrDao.increase(userId, userAttrEnum.param, size, tableSuffix);
        } catch (Exception e) {
            return NumberConstant.INTEGER_ZERO;
        }
    }

    /**
     * 根据用户标识查询指定用户下所有属性信息
     *
     * @param userId 根据用户业务id查找
     * @return 返回查找到的用户属性数据Map对象
     */
    @Override
    public Map<String, String> listDiskUserAttr(@NonNull String userId) {
        if (StringUtils.isBlank(userId)) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_USER_ATTR);
        // 根据用户标识查询指定用户下所有属性信息
        List<DiskUserAttr> attrList = diskUserAttrDao.listDiskUserAttr(userId, tableSuffix);

        // 将属性信息转成Map对象
        Map<String, String> attrMap = attrList.stream().collect(Collectors.toMap(DiskUserAttr::getAttributeName,
                value -> value.getAttributeValue().toPlainString(), (key1, key2) -> key2));

        // 用户属性枚举构建的Map
        Map<String, String> defaultMap = ConstantConfig.UserAttrEnum.builderMap();

        // 用来比较两个Map以获取所有不同点。该方法返回MapDifference对象
        MapDifference<String, String> difference = Maps.difference(defaultMap, attrMap);
        // 获取键只存在于左边Map而右边没有的映射项
        Map<String, String> entriesOnlyOnLeft = difference.entriesOnlyOnLeft();

        // 如果不存在差值时，直接返回属性对象Map
        if (entriesOnlyOnLeft.isEmpty()) {
            return attrMap;
        }

        // 将差值Map转为List对象，同时批量保存List对象
        this.insertBatch(entriesOnlyOnLeft.entrySet().stream().map(map -> DiskUserAttr.builder()
                .userId(userId)
                .attributeName(map.getKey())
                .attributeValue(new BigDecimal(map.getValue()))
                .build()).toList());
        // 合并两个Map内容，同时返回合并后的内容
        attrMap.putAll(entriesOnlyOnLeft);
        return attrMap;
    }
}
