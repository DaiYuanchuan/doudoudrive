package com.doudoudrive.commonservice.service.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.common.util.lang.SequenceUtil;
import com.doudoudrive.commonservice.dao.OssFileDao;
import com.doudoudrive.commonservice.service.OssFileService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * <p>OSS文件对象存储服务层实现</p>
 * <p>2022-05-21 00:07</p>
 *
 * @author Dan
 **/
@Scope("singleton")
@Service("ossFileService")
public class OssFileServiceImpl implements OssFileService {

    private OssFileDao ossFileDao;

    @Autowired
    public void setOssFileDao(OssFileDao ossFileDao) {
        this.ossFileDao = ossFileDao;
    }

    /**
     * 新增OSS文件对象存储
     *
     * @param ossFile 需要新增的OSS文件对象存储实体
     */
    @Override
    public void insert(OssFile ossFile) {
        if (ObjectUtils.isEmpty(ossFile) || StringUtils.isBlank(ossFile.getEtag())) {
            return;
        }
        if (StringUtils.isBlank(ossFile.getBusinessId())) {
            ossFile.setBusinessId(SequenceUtil.nextId(SequenceModuleEnum.OSS_FILE));
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.asciiSuffix(ossFile.getEtag(), ConstantConfig.TableSuffix.OSS_FILE);
        ossFileDao.insert(ossFile, tableSuffix);
    }

    /**
     * 删除OSS文件对象存储
     *
     * @param etag 根据文件的ETag(资源的唯一标识)删除数据
     * @return 返回删除的条数
     */
    @Override
    public Integer delete(String etag) {
        if (StringUtils.isBlank(etag)) {
            return NumberConstant.INTEGER_ZERO;
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.asciiSuffix(etag, ConstantConfig.TableSuffix.OSS_FILE);
        return ossFileDao.delete(etag, tableSuffix);
    }

    /**
     * 修改OSS文件对象存储
     *
     * @param ossFile 需要进行修改的OSS文件对象存储实体
     * @return 返回修改的条数
     */
    @Override
    public Integer update(OssFile ossFile) {
        if (ObjectUtils.isEmpty(ossFile) || StringUtils.isBlank(ossFile.getEtag())) {
            return NumberConstant.INTEGER_ZERO;
        }
        return ossFileDao.update(ossFile);
    }

    /**
     * 查找OSS文件对象存储
     *
     * @param etag 根据文件的ETag(资源的唯一标识)查找
     * @return 返回查找到的OSS文件对象存储实体
     */
    @Override
    public OssFile getOssFile(String etag) {
        if (StringUtils.isBlank(etag)) {
            return null;
        }

        // 获取表后缀
        String tableSuffix = SequenceUtil.asciiSuffix(etag, ConstantConfig.TableSuffix.OSS_FILE);
        return ossFileDao.getOssFile(etag, tableSuffix);
    }
}
