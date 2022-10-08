package com.doudoudrive.commonservice.service;

import com.doudoudrive.common.model.pojo.FileShareDetail;

import java.util.List;

/**
 * <p>文件分享记录详情服务层接口</p>
 * <p>2022-09-29 03:08</p>
 *
 * @author Dan
 **/
public interface FileShareDetailService {

    /**
     * 批量新增文件分享记录详情
     *
     * @param list 需要新增的文件分享记录详情集合
     */
    void insertBatch(List<FileShareDetail> list);

    /**
     * 根据分享的短链接标识(shareId)批量删除文件分享记录详情数据
     *
     * @param shareId 分享的短链接标识
     * @param userId  当前分享的用户标识
     */
    void delete(List<String> shareId, String userId);

    /**
     * 根据分享的短链接标识(shareId)查找文件分享记录详情数据
     *
     * @param shareId 需要进行查找的分享的短链接标识
     * @return 返回查找到的文件分享记录详情数据集合
     */
    List<FileShareDetail> listFileShareDetail(String shareId);

}
