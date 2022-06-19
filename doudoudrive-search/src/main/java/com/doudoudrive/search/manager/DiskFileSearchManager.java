package com.doudoudrive.search.manager;

import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import org.springframework.data.elasticsearch.core.SearchHits;

/**
 * <p>用户文件信息搜索服务的通用业务处理层接口</p>
 * <p>2022-05-22 14:14</p>
 *
 * @author Dan
 **/
public interface DiskFileSearchManager {

    /**
     * 保存用户文件信息，es中保存用户文件信息
     * <pre>
     *     先入库，然后再往es里存用户文件信息
     * </pre>
     *
     * @param diskFileDTO 用户文件实体信息ES数据模型
     */
    void saveDiskFile(DiskFileDTO diskFileDTO);

    /**
     * 根据文件业务标识删除指定文件信息
     * <pre>
     *     先删库，然后再删es里存的用户文件信息
     * </pre>
     *
     * @param id 文件业务标识
     */
    void deleteDiskFile(String id);

    /**
     * 根据文件业务标识去更新用户文件信息
     *
     * @param id          用户系统内唯一标识
     * @param diskFileDTO 用户文件实体信息ES数据模型
     */
    void updateDiskFile(String id, DiskFileDTO diskFileDTO);

    /**
     * 文件信息搜索，使用游标滚动翻页
     *
     * @param requestDTO 搜索es用户文件信息时的请求数据模型
     * @return 用户文件实体信息ES数据模型
     */
    SearchHits<DiskFileDTO> fileInfoSearch(QueryElasticsearchDiskFileRequestDTO requestDTO);
}
