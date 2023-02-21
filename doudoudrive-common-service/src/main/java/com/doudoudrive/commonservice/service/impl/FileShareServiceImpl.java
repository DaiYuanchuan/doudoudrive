package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.FileShare;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.FileShareDao;
import com.doudoudrive.commonservice.service.FileShareService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * <p>文件分享信息服务层实现</p>
 * <p>2023-01-03 18:41</p>
 *
 * @author Dan
 **/
@Scope("singleton")
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {

    private FileShareDao fileShareDao;

    @Autowired
    public void setFileShareDao(FileShareDao fileShareDao) {
        this.fileShareDao = fileShareDao;
    }

    /**
     * 新增文件分享信息
     *
     * @param fileShare 需要新增的文件分享信息实体
     * @return 返回新增的条数
     */
    @Override
    public Integer insert(FileShare fileShare) {
        if (ObjectUtils.isEmpty(fileShare) || StringUtils.isBlank(fileShare.getUserId())) {
            return NumberConstant.INTEGER_ZERO;
        }
        if (StringUtils.isBlank(fileShare.getBusinessId())) {
            fileShare.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.FILE_SHARE));
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(fileShare.getUserId(), ConstantConfig.TableSuffix.FILE_SHARE);
        return fileShareDao.insert(fileShare, tableSuffix);
    }

    /**
     * 批量删除文件分享信息
     *
     * @param shareIdList 需要删除的分享的短链接标识(shareId)数据集合
     * @param userId      所属的用户标识
     */
    @Override
    public void deleteBatch(List<String> shareIdList, String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.FILE_SHARE);
        CollectionUtil.collectionCutting(shareIdList, ConstantConfig.MAX_BATCH_TASKS_QUANTITY)
                .forEach(shareId -> fileShareDao.deleteBatch(shareId, tableSuffix));
    }

    /**
     * 修改文件分享信息
     *
     * @param fileShare 需要进行修改的文件分享信息实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(FileShare fileShare, String userId) {
        if (ObjectUtils.isEmpty(fileShare) || !StringUtils.isNoneBlank(fileShare.getBusinessId(), userId)) {
            return NumberConstant.INTEGER_ZERO;
        }
        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.FILE_SHARE);
        return fileShareDao.update(fileShare, tableSuffix);
    }

    /**
     * 对指定的字段自增，如: browse_count、save_count、download_count
     *
     * @param shareId   分享标识
     * @param fieldName 字段名(browse_count、save_count、download_count)
     * @param userId    所属的用户标识
     */
    @Override
    public void increase(String shareId, ConstantConfig.FileShareIncreaseEnum fieldName, String userId) {
        if (ObjectUtils.isEmpty(fieldName) || !StringUtils.isNoneBlank(shareId, userId)) {
            return;
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.FILE_SHARE);
        // 对指定的字段自增
        fileShareDao.increase(shareId, fieldName.getFieldName(), tableSuffix);
    }

    /**
     * 更新所有过期的分享链接
     */
    @Override
    public void updateExpiredShare() {
        List<String> tableSuffixList = SequenceUtil.tableSuffixList(ConstantConfig.TableSuffix.FILE_SHARE);
        fileShareDao.updateExpiredShare(LocalDateTime.now(), tableSuffixList);
    }

    /**
     * 查找文件分享信息
     *
     * @param businessId 根据业务id(businessId)查找
     * @param userId     所属的用户标识
     * @return 返回查找到的文件分享信息实体
     */
    @Override
    public FileShare getFileShare(String businessId, String userId) {
        if (!StringUtils.isNoneBlank(businessId, userId)) {
            return null;
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.FILE_SHARE);
        // 对指定的字段自增
        return fileShareDao.getFileShare(businessId, tableSuffix);
    }

    /**
     * 批量查找文件分享信息
     *
     * @param list   需要进行查找的分享的短链接标识(shareId)数据集合
     * @param userId 所属的用户标识
     * @return 返回查找到的文件分享信息数据集合
     */
    @Override
    public List<FileShare> listFileShare(List<String> list, String userId) {
        List<FileShare> batchQueryResult = Lists.newArrayListWithExpectedSize(list.size());
        if (StringUtils.isBlank(userId)) {
            return batchQueryResult;
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.tableSuffix(userId, ConstantConfig.TableSuffix.FILE_SHARE);
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(shareId -> {
            List<FileShare> queryResult = fileShareDao.listFileShare(shareId, tableSuffix);
            if (CollectionUtil.isNotEmpty(queryResult)) {
                batchQueryResult.addAll(queryResult);
            }
        });

        // 批量查询结果按照传入的顺序进行排序
        batchQueryResult.sort(Comparator.comparingInt(share -> list.indexOf(share.getShareId())));
        return batchQueryResult;
    }
}
