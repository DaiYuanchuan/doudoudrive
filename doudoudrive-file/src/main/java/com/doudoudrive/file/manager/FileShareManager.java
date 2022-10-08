package com.doudoudrive.file.manager;

import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchFileShareResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.file.model.dto.request.CreateFileShareRequestDTO;
import com.doudoudrive.file.model.dto.request.FileShareAnonymousRequestDTO;
import com.doudoudrive.file.model.dto.response.CreateFileShareResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareAnonymousResponseDTO;

import java.util.List;

/**
 * <p>用户文件分享记录信息服务的通用业务处理层接口</p>
 * <p>2022-09-28 23:16</p>
 *
 * @author Dan
 **/
public interface FileShareManager {

    /**
     * 新增用户文件分享记录信息
     *
     * @param userId                 用户标识
     * @param createFileShareRequest 网盘文件创建分享链接时的请求数据模型
     * @param shareFileList          需要分享的文件列表
     * @return 网盘文件创建分享链接时的响应数据模型
     */
    CreateFileShareResponseDTO createShare(String userId, CreateFileShareRequestDTO createFileShareRequest, List<DiskFile> shareFileList);

    /**
     * 取消文件分享链接
     *
     * @param shareId  分享链接标识
     * @param userinfo 当前分享的用户信息
     */
    DeleteElasticsearchFileShareResponseDTO cancelShare(List<String> shareId, DiskUserModel userinfo);

    /**
     * 根据分享链接的唯一标识获取分享链接的详细信息，包括分享的文件列表
     *
     * @param anonymousRequest 网盘文件分享链接匿名访问时的请求数据模型
     * @return 网盘文件分享链接匿名访问时的响应数据模型
     */
    Result<FileShareAnonymousResponseDTO> anonymous(FileShareAnonymousRequestDTO anonymousRequest);

}
