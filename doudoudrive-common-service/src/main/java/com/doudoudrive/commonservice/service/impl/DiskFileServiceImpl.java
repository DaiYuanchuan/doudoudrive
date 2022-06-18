package com.doudoudrive.commonservice.service.impl;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.response.PageResponse;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.doudoudrive.commonservice.dao.DiskFileDao;
import com.doudoudrive.commonservice.service.DiskFileService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>用户文件模块服务层实现</p>
 * <p>2022-05-19 23:23</p>
 *
 * @author Dan
 **/
@Service("diskFileService")
@DataSource(DataSourceEnum.FILE)
public class DiskFileServiceImpl implements DiskFileService {

    private DiskFileDao diskFileDao;

    @Autowired
    public void setDiskFileDao(DiskFileDao diskFileDao) {
        this.diskFileDao = diskFileDao;
    }

    /**
     * 新增用户文件模块
     *
     * @param diskFile 需要新增的用户文件模块实体
     */
    @Override
    public void insert(DiskFile diskFile) {
        if (ObjectUtils.isEmpty(diskFile) || StringUtils.isBlank(diskFile.getUserId())) {
            return;
        }
        if (StringUtils.isBlank(diskFile.getBusinessId())) {
            diskFile.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE));
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskFile.getUserId(), ConstantConfig.TableSuffix.DISK_FILE);
        diskFileDao.insert(diskFile, tableSuffix);
    }

    /**
     * 批量新增用户文件模块
     *
     * @param list 需要新增的用户文件模块集合
     */
    @Override
    public void insertBatch(List<DiskFile> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(diskFile -> {
            List<DiskFile> diskFileList = diskFile.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (DiskFile diskFileInfo : diskFileList) {
                if (StringUtils.isBlank(diskFileInfo.getBusinessId())) {
                    diskFileInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.DISK_FILE));
                }
            }
            // 将 用户文件模块的集合 按照 用户分组
            Map<String, List<DiskFile>> diskFileMap = diskFileList.stream().collect(Collectors.groupingBy(DiskFile::getUserId));
            diskFileMap.forEach((key, value) -> {
                // 获取表后缀
                String tableSuffix = SequenceUtil.tableSuffix(key, ConstantConfig.TableSuffix.DISK_FILE);
                if (CollectionUtil.isNotEmpty(value)) {
                    diskFileDao.insertBatch(value, tableSuffix);
                }
            });
        });
    }

    /**
     * 删除用户文件模块
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @param userId     业务id对应的用户标识
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId, String userId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_FILE);
        return diskFileDao.delete(businessId, tableSuffix);
    }

    /**
     * 批量删除用户文件模块
     *
     * @param list   需要删除的业务id(businessId)数据集合
     * @param userId 业务id对应的用户标识
     */
    @Override
    public void deleteBatch(List<String> list, String userId) {
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_FILE);
        // 批量删除用户文件模块
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).toList();
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                diskFileDao.deleteBatch(businessIdList, tableSuffix);
            }
        });
    }

    /**
     * 修改用户文件模块
     *
     * @param diskFile 需要进行修改的用户文件模块实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(DiskFile diskFile) {
        if (ObjectUtils.isEmpty(diskFile) || StringUtils.isBlank(diskFile.getBusinessId())
                || StringUtils.isBlank(diskFile.getUserId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskFile.getUserId(), ConstantConfig.TableSuffix.DISK_FILE);
        return diskFileDao.update(diskFile, tableSuffix);
    }

    /**
     * 批量修改用户文件模块
     *
     * @param list 需要进行修改的用户文件模块集合
     */
    @Override
    public void updateBatch(List<DiskFile> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(diskFile -> {
            List<DiskFile> diskFileList = diskFile.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(diskFileInfo -> StringUtils.isNotBlank(diskFileInfo.getBusinessId()) && StringUtils.isNotBlank(diskFileInfo.getUserId())).toList();
            // 将 用户文件模块的集合 按照 用户分组
            Map<String, List<DiskFile>> diskFileMap = diskFileList.stream().collect(Collectors.groupingBy(DiskFile::getUserId));
            diskFileMap.forEach((key, value) -> {
                // 获取表后缀
                String tableSuffix = SequenceUtil.tableSuffix(key, ConstantConfig.TableSuffix.DISK_FILE);
                if (CollectionUtil.isNotEmpty(value)) {
                    diskFileDao.updateBatch(value, tableSuffix);
                }
            });
        });
    }

    /**
     * 根据业务标识查找指定用户下的文件信息
     *
     * @param userId     指定的用户标识
     * @param businessId 需要查询的文件标识
     * @return 用户文件模块信息
     */
    @Override
    public DiskFile getDiskFile(String userId, String businessId) {
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_FILE);
        return diskFileDao.getDiskFile(userId, businessId, tableSuffix);
    }

    /**
     * 根据parentId查询指定目录下是否存在指定的文件名
     *
     * @param parentId   文件的父级标识
     * @param fileName   文件、文件夹名称
     * @param userId     指定的用户标识
     * @param fileFolder 是否为文件夹
     * @return 在指定目录下存在相同的文件名时返回 1 ，否则返回 0 或者 null
     */
    @Override
    public Integer getRepeatFileName(String parentId, String fileName, String userId, Boolean fileFolder) {
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.DISK_FILE);
        return diskFileDao.getRepeatFileName(parentId, fileName, userId, fileFolder, tableSuffix);
    }

    /**
     * 指定条件查找用户文件模块
     *
     * @param diskFile  需要查询的用户文件模块实体(这里不能为NULL，且必须包含用户id)
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 用户文件模块搜索响应数据模型
     */
    @Override
    public PageResponse<DiskFile> listDiskFileToKey(DiskFile diskFile, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<DiskFile> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);

        // 对象是否为空
        boolean diskFileIsBlank = diskFile == null || JSON.parseObject(JSONObject.toJSONString(diskFile)).isEmpty();
        if (diskFileIsBlank) {
            // 对象为空时这里直接抛出异常
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(diskFile.getUserId(), ConstantConfig.TableSuffix.DISK_FILE);

        // 对象不为空 ，开始时间为空
        if (timeIsBlank) {
            response.setRows(diskFileDao.listDiskFileToKey(diskFile, tableSuffix, null, null, pageSql));
            response.setTotal(this.countSearch(diskFile, tableSuffix, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 构建返回数据
        response.setRows(diskFileDao.listDiskFileToKey(diskFile, tableSuffix, str[0], str[1], pageSql));
        response.setTotal(this.countSearch(diskFile, tableSuffix, str[0], str[1]));
        return response;
    }

    /**
     * 指定条件查找用户文件模块
     * 返回用户文件模块集合数据
     *
     * @param diskFile  需要查询的用户文件模块实体(这里不能为NULL，且必须包含用户id)
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @param page      页码
     * @param pageSize  每页大小
     * @return 返回用户文件模块集合
     */
    @Override
    public List<DiskFile> listDiskFile(DiskFile diskFile, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listDiskFileToKey(diskFile, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 返回搜索结果的总数
     *
     * @param diskFile  需要查询的用户文件模块实体(这里不能为NULL，且必须包含用户id)
     * @param startTime 需要查询的开始时间(如果有)
     * @param endTime   需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    private Long countSearch(DiskFile diskFile, String tableSuffix, String startTime, String endTime) {
        return diskFileDao.countSearch(diskFile, tableSuffix, startTime, endTime);
    }
}
