package com.doudoudrive.search.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.ReflectUtil;
import com.doudoudrive.search.manager.FileShareSearchManager;
import com.doudoudrive.search.model.elasticsearch.FileShareDTO;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>用户文件分享信息搜索服务的通用业务处理层接口实现</p>
 * <p>2022-09-24 20:24</p>
 *
 * @author Dan
 **/
@Service("fileShareSearchManager")
public class FileShareSearchManagerImpl implements FileShareSearchManager {

    /**
     * 用户标识、创建时间
     */
    private static final String USER_ID = ReflectUtil.property(FileShareDTO::getUserId);
    private static final String CREATE_TIME = ReflectUtil.property(FileShareDTO::getCreateTime);
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <p>保存用户文件分享信息</p>
     * <pre>
     *     先入库，然后再往es里存用户文件分享信息
     * </pre>
     *
     * @param fileShareDTO 用户文件分享信息ES数据模型
     */
    @Override
    public void createShare(FileShareDTO fileShareDTO) {
        if (ObjectUtils.isNotEmpty(fileShareDTO)) {
            // 构建保存请求
            restTemplate.save(fileShareDTO);
        }
    }

    /**
     * <p>根据分享标识批量删除用户文件分享信息</p>
     * <pre>
     *     先删库，然后再删es里存的用户文件分享信息
     * </pre>
     *
     * @param shareId 用户文件分享标识
     * @return 删除的文件信息
     */
    @Override
    public ByQueryResponse cancelShare(List<String> shareId) {
        // 查询信息构建
        IdsQueryBuilder builder = QueryBuilders.idsQuery();
        builder.addIds(shareId.toArray(String[]::new));

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 批量删除数据
        return restTemplate.delete(queryBuilder.build(), FileShareDTO.class);
    }

    /**
     * 根据用户文件分享标识去更新用户文件分享信息
     *
     * @param id        分享的短链接id
     * @param fileShare 用户文件分享信息ES数据模型
     */
    @Override
    public void updateFileShare(String id, FileShareDTO fileShare) {
        // 将用户文件信息转为map
        Map<String, Object> fileShareMap = BeanUtil.beanToMap(fileShare, Boolean.FALSE, Boolean.TRUE);
        // 构建更新的es请求
        restTemplate.update(UpdateQuery
                .builder(id)
                .withDocument(Document.from(fileShareMap))
                .build(), IndexCoordinates.of(ConstantConfig.IndexName.DISK_SHARE_FILE));
    }

    /**
     * 根据用户标识查询指定用户下的文件分享信息，使用游标滚动翻页
     *
     * @param userId      用户系统内唯一标识
     * @param searchAfter 上一页游标，为空时默认第一页
     * @param count       单次查询的数量、每页大小
     * @return 用户文件分享信息ES数据模型
     */
    @Override
    public SearchHits<FileShareDTO> shareUserIdSearch(String userId, List<Object> searchAfter, Integer count) {
        // 查询信息构建
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery(USER_ID, userId));

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withQuery(builder);

        // 游标不为空时，加入游标查询
        if (CollectionUtil.isNotEmpty(searchAfter)) {
            queryBuilder.withSearchAfter(searchAfter);
        }

        // 指定字段排序构建，默认按照创建时间倒叙排列
        queryBuilder.withSorts(SortBuilders.fieldSort(CREATE_TIME).order(SortOrder.DESC));

        // 构建分页语句
        queryBuilder.withPageable(PageRequest.of(NumberConstant.INTEGER_ZERO, count));

        // 执行搜素请求
        return restTemplate.search(queryBuilder.build(), FileShareDTO.class);
    }

    /**
     * 根据用户文件分享标识批量查询用户文件信息
     *
     * @param shareId 用户文件分享标识
     * @return 用户文件分享记录信息ES数据模型
     */
    @Override
    public SearchHits<FileShareDTO> shareIdSearch(List<String> shareId) {
        // 查询信息构建
        IdsQueryBuilder builder = QueryBuilders.idsQuery();
        builder.addIds(shareId.toArray(String[]::new));

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 执行搜素请求
        return restTemplate.search(queryBuilder.build(), FileShareDTO.class);
    }
}
