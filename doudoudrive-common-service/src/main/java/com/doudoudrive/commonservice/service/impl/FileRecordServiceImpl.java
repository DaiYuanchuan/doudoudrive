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
import com.doudoudrive.common.model.pojo.FileRecord;
import com.doudoudrive.common.util.date.DateUtils;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.PageDataUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.FileRecordDao;
import com.doudoudrive.commonservice.service.FileRecordService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>文件操作记录服务层实现</p>
 * <p>2022-05-26 10:58</p>
 *
 * @author Dan
 **/
@Scope("singleton")
@Service("fileRecordService")
public class FileRecordServiceImpl implements FileRecordService {

    private FileRecordDao fileRecordDao;

    @Autowired
    public void setFileRecordDao(FileRecordDao fileRecordDao) {
        this.fileRecordDao = fileRecordDao;
    }

    /**
     * 新增文件操作记录
     *
     * @param fileRecord 需要新增的文件操作记录实体
     */
    @Override
    public void insert(FileRecord fileRecord) {
        if (ObjectUtils.isEmpty(fileRecord)) {
            return;
        }
        if (StringUtils.isBlank(fileRecord.getBusinessId())) {
            fileRecord.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.FILE_RECORD));
        }
        fileRecordDao.insert(fileRecord);
    }

    /**
     * 批量新增文件操作记录
     *
     * @param list 需要新增的文件操作记录集合
     */
    @Override
    public void insertBatch(List<FileRecord> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(fileRecord -> {
            List<FileRecord> fileRecordList = fileRecord.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (FileRecord fileRecordInfo : fileRecordList) {
                if (StringUtils.isBlank(fileRecordInfo.getBusinessId())) {
                    fileRecordInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.FILE_RECORD));
                }
            }
            if (CollectionUtil.isNotEmpty(fileRecordList)) {
                fileRecordDao.insertBatch(fileRecordList);
            }
        });
    }

    /**
     * 删除文件操作记录
     *
     * @param businessId 根据业务id(businessId)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String businessId) {
        if (StringUtils.isBlank(businessId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        return fileRecordDao.delete(businessId);
    }

    /**
     * 批量删除文件操作记录
     *
     * @param list 需要删除的业务id(businessId)数据集合
     */
    @Override
    public void deleteBatch(List<String> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).toList();
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                fileRecordDao.deleteBatch(businessIdList);
            }
        });
    }

    /**
     * 修改文件操作记录
     *
     * @param fileRecord 需要进行修改的文件操作记录实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(FileRecord fileRecord) {
        if (ObjectUtils.isEmpty(fileRecord) || StringUtils.isBlank(fileRecord.getBusinessId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return fileRecordDao.update(fileRecord);
    }

    /**
     * 批量修改文件操作记录
     *
     * @param list 需要进行修改的文件操作记录集合
     */
    @Override
    public void updateBatch(List<FileRecord> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(fileRecord -> {
            List<FileRecord> fileRecordList = fileRecord.stream().filter(ObjectUtils::isNotEmpty)
                    .filter(fileRecordInfo -> StringUtils.isNotBlank(fileRecordInfo.getBusinessId()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(fileRecordList)) {
                fileRecordDao.updateBatch(fileRecordList);
            }
        });
    }

    /**
     * 查找文件操作记录
     *
     * @param businessId 根据业务id(businessId)查找
     * @return 返回查找到的文件操作记录实体
     */
    @Override
    public FileRecord getFileRecord(String businessId) {
        return fileRecordDao.getFileRecord(businessId);
    }

    /**
     * 根据 Model 中某个成员变量名称(非数据表中column的名称)查找(value需符合unique约束)
     *
     * @param modelName Model中某个成员变量名称,非数据表中column的名称[如:createTime]
     * @param value     需要查找的值
     * @return 返回查找到的文件操作记录实体
     */
    @Override
    public FileRecord getFileRecordToModel(String modelName, Object value) {
        return fileRecordDao.getFileRecordToModel(modelName.replaceAll("([A-Z])", "_$1").toLowerCase(), value);
    }

    /**
     * 批量查找文件操作记录
     *
     * @param list 需要进行查找的业务id(businessId)数据集合
     * @return 返回查找到的文件操作记录数据集合
     */
    @Override
    public List<FileRecord> listFileRecord(List<String> list) {
        List<FileRecord> fileRecordList = new ArrayList<>();
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> fileRecordList
                .addAll(fileRecordDao.listFileRecord(businessId.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()))));
        return fileRecordList;
    }

    /**
     * 指定条件查找文件操作记录
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @param page       页码
     * @param pageSize   每页大小
     * @return 文件操作记录搜索响应数据模型
     */
    @Override
    public PageResponse<FileRecord> listFileRecordToKey(FileRecord fileRecord, String startTime, String endTime, Integer page, Integer pageSize) {
        // 构建返回对象
        PageResponse<FileRecord> response = new PageResponse<>();

        // 构建分页语句
        String pageSql = PageDataUtil.pangingSql(page, pageSize, response);

        // 开始时间是否为空
        boolean timeIsBlank = StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime);
        // 对象是否为空
        boolean fileRecordIsBlank = fileRecord == null || JSON.parseObject(JSONObject.toJSONString(fileRecord)).isEmpty();

        // 对象不为空 ，开始时间为空
        if (!fileRecordIsBlank && timeIsBlank) {
            response.setRows(fileRecordDao.listFileRecordToKey(fileRecord, null, null, pageSql));
            response.setTotal(countSearch(fileRecord, null, null));
            return response;
        }

        // 对象为空 ，开始时间为空
        if (fileRecordIsBlank && timeIsBlank) {
            // 构建返回数据
            response.setRows(fileRecordDao.listFileRecordToKey(null, null, null, pageSql));
            response.setTotal(countSearch(null, null, null));
            return response;
        }

        // 获取到正确的时间顺序
        String[] str = DateUtils.sortByDate(startTime, endTime, DatePattern.NORM_DATE_PATTERN);
        if (str == null) {
            BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.SYSTEM_ERROR);
        }

        // 构建返回数据
        response.setRows(fileRecordDao.listFileRecordToKey(fileRecord, str[0], str[1], pageSql));
        response.setTotal(countSearch(fileRecord, str[0], str[1]));
        return response;
    }

    /**
     * 指定条件查找文件操作记录
     * 返回文件操作记录集合数据
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @param page       页码
     * @param pageSize   每页大小
     * @return 返回文件操作记录集合
     */
    @Override
    public List<FileRecord> listFileRecord(FileRecord fileRecord, String startTime, String endTime, Integer page, Integer pageSize) {
        // 获取根据指定条件查找到的数据
        return listFileRecordToKey(fileRecord, startTime, endTime, page, pageSize).getRows();
    }

    /**
     * 查找所有文件操作记录
     *
     * @return 返回所有的文件操作记录集合数据
     */
    @Override
    public List<FileRecord> listFileRecordFindAll() {
        return fileRecordDao.listFileRecordToKey(null, null, null, null);
    }

    /**
     * 返回搜索结果的总数
     *
     * @param fileRecord 需要查询的文件操作记录实体
     * @param startTime  需要查询的开始时间(如果有)
     * @param endTime    需要查询的结束时间(如果有)
     * @return 返回搜索结果的总数
     */
    @Override
    public Long countSearch(FileRecord fileRecord, String startTime, String endTime) {
        return fileRecordDao.countSearch(fileRecord, startTime, endTime);
    }

    /**
     * 删除指定状态的文件操作记录
     *
     * @param userId     用户id
     * @param etag       文件唯一标识
     * @param action     动作
     * @param actionType 动作对应的动作类型
     */
    @Override
    public void deleteAction(String userId, List<String> etag, String action, String actionType) {
        CollectionUtil.collectionCutting(etag, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(businessId -> {
            List<String> businessIdList = businessId.stream().filter(StringUtils::isNotBlank).toList();
            if (CollectionUtil.isNotEmpty(businessIdList)) {
                fileRecordDao.deleteAction(userId, businessIdList, action, actionType);
            }
        });
    }

    /**
     * 判断指定状态的文件操作记录是否存在
     *
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 存在指定状态的文件操作记录时返回 true ，否则返回 false
     */
    @Override
    public Boolean isFileRecordExist(String userId, String action, String actionType) {
        return NumberConstant.INTEGER_ONE.equals(fileRecordDao.isFileRecordExist(userId, action, actionType));
    }

    /**
     * 获取指定状态的文件操作记录数据
     *
     * @param userId     用户id
     * @param etag       文件唯一标识
     * @param action     动作
     * @param actionType 动作对应的动作类型
     * @return 返回指定状态的文件操作记录数据
     */
    @Override
    public FileRecord getFileRecordByAction(String userId, String etag, String action, String actionType) {
        return fileRecordDao.getFileRecordByAction(userId, etag, action, actionType);
    }

    /**
     * 更新 指定动作类型 的文件操作记录的 动作类型
     *
     * @param businessId     文件操作记录系统内唯一标识
     * @param fromAction     原动作
     * @param fromActionType 原动作对应的动作类型
     * @param toAction       新动作
     * @param toActionType   新动作对应的动作类型
     * @return 返回更新的条数
     */
    @Override
    public Integer updateFileRecordByAction(String businessId, String fromAction, String fromActionType,
                                            String toAction, String toActionType) {
        return fileRecordDao.updateFileRecordByAction(businessId, fromAction, fromActionType, toAction, toActionType);
    }
}
