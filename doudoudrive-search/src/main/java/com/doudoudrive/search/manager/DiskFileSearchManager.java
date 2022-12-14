package com.doudoudrive.search.manager;

import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;

import java.util.List;

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
    void saveDiskFile(List<DiskFileDTO> diskFileDTO);

    /**
     * 根据文件业务标识删除指定文件信息
     * <pre>
     *     先删库，然后再删es里存的用户文件信息
     * </pre>
     *
     * @param businessId 文件业务标识
     * @return 删除的文件信息
     */
    ByQueryResponse deleteDiskFile(List<String> businessId);

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

    /**
     * 根据文件业务标识批量查询用户文件信息
     *
     * @param businessId  文件业务标识
     * @param sort        排序字段
     * @param count       每页数量
     * @param searchAfter 游标
     * @return 用户文件实体信息ES数据模型
     */
    SearchHits<DiskFileDTO> fileIdSearch(List<String> businessId, List<OrderByBuilder> sort, Integer count, List<Object> searchAfter);

    /**
     * 根据文件父级业务标识批量查询用户文件信息
     *
     * @param userId      用户系统内唯一标识
     * @param parentId    文件父级业务标识
     * @param count       单次查询的数量、每页大小
     * @param searchAfter 上一页游标，为空时默认第一页
     * @return 用户文件实体信息ES数据模型
     */
    SearchHits<DiskFileDTO> fileParentIdSearch(String userId, List<String> parentId, Integer count, List<Object> searchAfter);
}
