package com.doudoudrive.commonservice.dao;

import com.doudoudrive.common.model.pojo.FileShareDetail;
import com.doudoudrive.commonservice.annotation.DataSource;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>文件分享记录详情数据访问层</p>
 * <p>2022-09-29 02:50</p>
 *
 * @author Dan
 **/
@Repository
@DataSource(DataSourceEnum.FILE_SHARE)
public interface FileShareDetailDao {

    /**
     * 批量新增文件分享记录详情
     *
     * @param list        需要新增的文件分享记录详情集合
     * @param tableSuffix 表后缀
     * @return 返回新增的条数
     */
    Integer insertBatch(@Param("list") List<FileShareDetail> list, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据分享的短链接标识(shareId)批量删除文件分享记录详情数据
     *
     * @param userId      当前分享的用户标识
     * @param shareId     分享的短链接标识
     * @param tableSuffix 表后缀
     * @return 返回删除的条数
     */
    Integer delete(@Param("userId") String userId, @Param("list") List<String> shareId, @Param("tableSuffix") String tableSuffix);

    /**
     * 根据分享的短链接标识(shareId)查找文件分享记录详情数据
     *
     * @param shareId     需要进行查找的分享的短链接标识
     * @param tableSuffix 表后缀
     * @return 返回查找到的文件分享记录详情数据集合
     */
    List<FileShareDetail> listFileShareDetail(@Param("shareId") String shareId, @Param("tableSuffix") String tableSuffix);

}
