package com.doudoudrive.file.manager;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.model.FileNestedModel;
import com.doudoudrive.common.model.dto.response.DeleteElasticsearchFileShareResponseDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.http.Result;
import com.doudoudrive.file.model.dto.request.CreateFileShareRequestDTO;
import com.doudoudrive.file.model.dto.request.FileCopyRequestDTO;
import com.doudoudrive.file.model.dto.request.FileShareAnonymousRequestDTO;
import com.doudoudrive.file.model.dto.request.FileShareSearchRequestDTO;
import com.doudoudrive.file.model.dto.response.CreateFileShareResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareAnonymousResponseDTO;
import com.doudoudrive.file.model.dto.response.FileShareSearchResponseDTO;

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
     * @return 删除es文件分享记录信息时的响应数据模型
     */
    DeleteElasticsearchFileShareResponseDTO cancelShare(List<String> shareId, DiskUserModel userinfo);

    /**
     * 根据分享链接的唯一标识获取分享链接的详细信息，包括分享的文件列表
     *
     * @param anonymousRequest 网盘文件分享链接匿名访问时的请求数据模型
     * @return 网盘文件分享链接匿名访问时的响应数据模型
     */
    Result<FileShareAnonymousResponseDTO> anonymous(FileShareAnonymousRequestDTO anonymousRequest);

    /**
     * 文件分享信息搜索
     *
     * @param fileShareSearchRequest 文件分享数据搜索请求数据模型
     * @param userinfo               当前登录的用户信息
     * @return 文件分享数据搜索响应数据模型
     */
    FileShareSearchResponseDTO fileShareSearch(FileShareSearchRequestDTO fileShareSearchRequest, DiskUserModel userinfo);

    /**
     * 分享文件保存到我的
     * <pre>
     *     1.校验分享链接是否存在
     *     2.校验分享链接是否过期
     *     3.校验分享提取码是否正确
     *     4.校验分享中文件key值是否正确
     *     5.实现异步复制文件到指定文件目录下
     * </pre>
     *
     * @param fileCopyRequest 文件复制(保存到我的)时的请求数据模型
     * @param userinfo        当前登录的用户信息
     */
    void copy(FileCopyRequestDTO fileCopyRequest, DiskUserModel userinfo);

    /**
     * 对指定的字段自增，如: browse_count、save_count、download_count
     *
     * @param shareId   分享标识
     * @param fieldName 字段名(browse_count、save_count、download_count)
     * @param userId    所属的用户标识
     */
    void increase(String shareId, ConstantConfig.FileShareIncreaseEnum fieldName, String userId);

    /**
     * 校验分享链接的key值是否正确
     *
     * @param shareId  分享短链
     * @param fileInfo 需要校验的文件信息
     * @return true:校验通过，false:校验失败
     */
    Boolean shareKeyCheck(String shareId, List<FileNestedModel> fileInfo);
}
