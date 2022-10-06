package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.FileShareDetail;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.FileShareDetailDao;
import com.doudoudrive.commonservice.service.FileShareDetailService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>文件分享记录详情服务层实现</p>
 * <p>2022-09-29 03:10</p>
 *
 * @author Dan
 **/
@Scope("singleton")
@Service("fileShareDetailService")
public class FileShareDetailServiceImpl implements FileShareDetailService {

    private FileShareDetailDao fileShareDetailDao;

    @Autowired
    public void setFileShareDetailDao(FileShareDetailDao fileShareDetailDao) {
        this.fileShareDetailDao = fileShareDetailDao;
    }

    /**
     * 批量新增文件分享记录详情
     *
     * @param list 需要新增的文件分享记录详情集合
     */
    @Override
    public void insertBatch(List<FileShareDetail> list) {
        CollectionUtil.collectionCutting(list, ConstantConfig.MAX_BATCH_TASKS_QUANTITY).forEach(fileShareDetail -> {
            List<FileShareDetail> fileShareDetailList = fileShareDetail.stream().filter(ObjectUtils::isNotEmpty).toList();
            for (FileShareDetail fileShareDetailInfo : fileShareDetailList) {
                if (StringUtils.isBlank(fileShareDetailInfo.getBusinessId())) {
                    fileShareDetailInfo.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.FILE_SHARE));
                }
            }
            // 将 文件分享记录详情的集合 按照 分享的短链接标识 分组
            Map<String, List<FileShareDetail>> fileShareDetailMap = fileShareDetailList.stream().collect(Collectors.groupingBy(FileShareDetail::getShareId));
            fileShareDetailMap.forEach((key, value) -> {
                // 获取表后缀
                String tableSuffix = SequenceUtil.asciiSuffix(key, ConstantConfig.TableSuffix.FILE_SHARE);
                if (CollectionUtil.isNotEmpty(value)) {
                    fileShareDetailDao.insertBatch(value, tableSuffix);
                }
            });
        });
    }

    /**
     * 根据分享的短链接标识(shareId)批量删除文件分享记录详情数据
     *
     * @param shareId 分享的短链接标识
     */
    @Override
    public void delete(List<String> shareId) {
        // 将 分享的短链接标识的集合 按照 分片路由 分组
        Map<String, List<String>> shareSuffixMap = shareId.stream().filter(StringUtils::isNotBlank)
                .collect(Collectors.groupingBy(share -> SequenceUtil.asciiSuffix(share, ConstantConfig.TableSuffix.FILE_SHARE)));
        shareSuffixMap.forEach((key, value) -> fileShareDetailDao.delete(value, key));
    }

    /**
     * 根据分享的短链接标识(shareId)查找文件分享记录详情数据
     *
     * @param shareId 需要进行查找的分享的短链接标识
     * @return 返回查找到的文件分享记录详情数据集合
     */
    @Override
    public List<FileShareDetail> listFileShareDetail(String shareId) {
        // 获取表后缀
        String tableSuffix = SequenceUtil.asciiSuffix(shareId, ConstantConfig.TableSuffix.FILE_SHARE);
        return fileShareDetailDao.listFileShareDetail(shareId, tableSuffix);
    }
}
