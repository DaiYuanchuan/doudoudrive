package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.OssFile;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>OSS文件对象存储数据访问层</p>
 * <p>2022-05-20 10:56</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.FILE)
public interface OssFileDao {

    /**
     * 新增OSS文件对象存储
     *
     * @param ossFile     需要新增的OSS文件对象存储实体
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insert(@Param("ossFile") OssFile ossFile, @Param("tableSuffix") String tableSuffix);

    /**
     * 删除OSS文件对象存储
     *
     * @param etag        根据文件的ETag(资源的唯一标识)删除对象
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer delete(@Param("etag") String etag, @Param("tableSuffix") String tableSuffix);

    /**
     * 修改OSS文件对象存储
     *
     * @param ossFile 需要进行修改的OSS文件对象存储实体
     * @return 返回修改的条数
     */
    Integer update(@Param("ossFile") OssFile ossFile);

    /**
     * 查找OSS文件对象存储
     *
     * @param etag        根据文件的ETag(资源的唯一标识)查找
     * @param tableSuffix 表后缀
     * @return 返回查找到的OSS文件对象存储实体
     */
    OssFile getOssFile(@Param("etag") String etag, @Param("tableSuffix") String tableSuffix);
}
