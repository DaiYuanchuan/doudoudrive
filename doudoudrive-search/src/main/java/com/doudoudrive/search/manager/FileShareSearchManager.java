package com.doudoudrive.search.manager;

import com.doudoudrive.search.model.elasticsearch.FileShareDTO;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;

import java.util.List;

/**
 * <p>用户文件分享信息搜索服务的通用业务处理层接口</p>
 * <p>2022-09-24 20:24</p>
 *
 * @author Dan
 **/
public interface FileShareSearchManager {

    /**
     * <p>保存用户文件分享信息</p>
     * <pre>
     *     先入库，然后再往es里存用户文件分享信息
     * </pre>
     *
     * @param fileShareDTO 用户文件分享信息ES数据模型
     */
    void createShare(FileShareDTO fileShareDTO);

    /**
     * <p>根据分享标识批量删除用户文件分享信息</p>
     * <pre>
     *     先删库，然后再删es里存的用户文件分享信息
     * </pre>
     *
     * @param shareId 用户文件分享标识
     * @return 删除的文件信息
     */
    ByQueryResponse cancelShare(List<String> shareId);

    /**
     * 根据用户文件分享标识去更新用户文件分享信息
     *
     * @param id        分享的短链接id
     * @param fileShare 用户文件分享信息ES数据模型
     */
    void updateFileShare(String id, FileShareDTO fileShare);

    /**
     * 根据用户标识查询指定用户下的文件分享信息，使用游标滚动翻页
     *
     * @param userId      用户系统内唯一标识
     * @param searchAfter 上一页游标，为空时默认第一页
     * @param count       单次查询的数量、每页大小
     * @return 用户文件分享信息ES数据模型
     */
    SearchHits<FileShareDTO> shareUserIdSearch(String userId, List<Object> searchAfter, Integer count);

    /**
     * 根据用户文件分享标识批量查询用户文件信息
     *
     * @param shareId 用户文件分享标识
     * @return 用户文件分享记录信息ES数据模型
     */
    SearchHits<FileShareDTO> shareIdSearch(List<String> shareId);

}
