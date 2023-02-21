package com.doudoudrive.search.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.ReflectUtil;
import com.doudoudrive.search.manager.DiskFileSearchManager;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import com.doudoudrive.search.util.ElasticUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>用户文件信息搜索服务的通用业务处理层接口实现</p>
 * <p>2022-05-22 14:15</p>
 *
 * @author Dan
 **/
@Service("diskFileSearchManager")
public class DiskFileSearchManagerImpl implements DiskFileSearchManager {

    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 自增长标识、用户标识、业务标识、父级标识、文件名、mime类型、ETag、等字段对应的字符串
     */
    private static final String AUTO_ID = ReflectUtil.property(DiskFile::getAutoId);
    private static final String USER_ID = ReflectUtil.property(DiskFile::getUserId);
    private static final String BUSINESS_ID = ReflectUtil.property(DiskFile::getBusinessId);
    private static final String FILE_PARENT_ID = ReflectUtil.property(DiskFile::getFileParentId);
    private static final String FILE_SIZE = ReflectUtil.property(DiskFile::getFileSize);
    private static final String FILE_ETAG = ReflectUtil.property(DiskFile::getFileEtag);
    private static final String FILE_MIME_TYPE = ReflectUtil.property(DiskFile::getFileMimeType);
    private static final String FILE_FOLDER = ReflectUtil.property(DiskFile::getFileFolder);
    private static final String COLLECT = ReflectUtil.property(DiskFile::getCollect);
    private static final String FILE_NAME = ReflectUtil.property(DiskFile::getFileName);
    private static final String CREATE_TIME = ReflectUtil.property(DiskFile::getCreateTime);
    private static final String UPDATE_TIME = ReflectUtil.property(DiskFile::getUpdateTime);

    /**
     * 文件搜索时的支持的排序字段
     */
    private static final List<String> FILE_SEARCH_SORT_FIELD = Lists.newArrayList(BUSINESS_ID, FILE_SIZE, CREATE_TIME, UPDATE_TIME);

    /**
     * 模糊搜索文件名时的通配符
     */
    private static final String FUZZY_SEARCH = "*%s*";

    /**
     * 保存用户文件信息，es中保存用户文件信息
     * <pre>
     *     先入库，然后再往es里存用户文件信息
     * </pre>
     *
     * @param diskFileDTO 用户文件实体信息ES数据模型
     */
    @Override
    public void saveDiskFile(List<DiskFileDTO> diskFileDTO) {
        // 构建保存请求
        restTemplate.save(diskFileDTO);
    }

    /**
     * 根据文件业务标识删除指定文件信息
     * <pre>
     *     先删库，然后再删es里存的用户文件信息
     * </pre>
     *
     * @param businessId 文件业务标识
     * @return 删除的文件信息
     */
    @Override
    public ByQueryResponse deleteDiskFile(List<String> businessId) {
        // 查询信息构建
        IdsQueryBuilder builder = QueryBuilders.idsQuery();
        builder.addIds(businessId.toArray(String[]::new));

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 批量删除数据
        return restTemplate.delete(queryBuilder.build(), DiskFileDTO.class);
    }

    /**
     * 根据文件业务标识去更新用户文件信息
     * <pre>
     *     先更新库，然后再更新es里存的用户文件数据
     * </pre>
     *
     * @param diskFileList 用户文件实体信息ES数据模型
     */
    @Override
    public void updateDiskFile(List<DiskFileDTO> diskFileList) {
        List<UpdateQuery> queries = Lists.newArrayListWithExpectedSize(diskFileList.size());
        for (DiskFileDTO diskFile : diskFileList) {
            // 将用户文件信息转为map
            Map<String, Object> diskFileMap = BeanUtil.beanToMap(diskFile, Boolean.FALSE, Boolean.TRUE);
            queries.add(UpdateQuery
                    .builder(diskFile.getBusinessId())
                    .withDocument(Document.from(diskFileMap))
                    .build());
        }

        // 构建批量更新的es请求
        restTemplate.bulkUpdate(queries, DiskFileDTO.class);
    }

    /**
     * 文件信息搜索，使用游标滚动翻页
     *
     * @param requestDTO 搜索es用户文件信息时的请求数据模型
     * @return 用户文件实体信息ES数据模型
     */
    @Override
    public SearchHits<DiskFileDTO> fileInfoSearch(QueryElasticsearchDiskFileRequestDTO requestDTO) {
        // 查询信息构建
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery(USER_ID, requestDTO.getUserId()));

        if (StringUtils.isNotBlank(requestDTO.getBusinessId())) {
            builder.must(QueryBuilders.termQuery(BUSINESS_ID, requestDTO.getBusinessId()));
        }

        if (StringUtils.isNotBlank(requestDTO.getFileParentId())) {
            builder.must(QueryBuilders.termQuery(FILE_PARENT_ID, requestDTO.getFileParentId()));
        }

        if (StringUtils.isNotBlank(requestDTO.getFileEtag())) {
            builder.must(QueryBuilders.termQuery(FILE_ETAG, requestDTO.getFileEtag()));
        }

        if (StringUtils.isNotBlank(requestDTO.getFileMimeType())) {
            builder.must(QueryBuilders.termQuery(FILE_MIME_TYPE, requestDTO.getFileMimeType()));
        }

        if (requestDTO.getFileFolder() != null) {
            builder.must(QueryBuilders.termQuery(FILE_FOLDER, requestDTO.getFileFolder()));
        }

        if (requestDTO.getCollect() != null) {
            builder.must(QueryBuilders.termQuery(COLLECT, requestDTO.getCollect()));
        }

        if (StringUtils.isNotBlank(requestDTO.getFileName())) {
            // 文件名使用模糊搜索
            builder.must(QueryBuilders.wildcardQuery(FILE_NAME, String.format(FUZZY_SEARCH, requestDTO.getFileName())));
        }

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);
        // 根据排序分页参数构建排序分页对象
        ElasticUtil.builderSortPageable(requestDTO.getSort(), FILE_SEARCH_SORT_FIELD, AUTO_ID, requestDTO.getSearchAfter(), requestDTO.getCount(), queryBuilder);

        // 执行搜素请求
        return restTemplate.search(queryBuilder.build(), DiskFileDTO.class);
    }

    /**
     * 根据文件业务标识批量查询用户文件信息
     *
     * @param businessId  文件业务标识
     * @param sort        排序字段
     * @param count       每页数量
     * @param searchAfter 游标
     * @return 用户文件实体信息ES数据模型
     */
    @Override
    public SearchHits<DiskFileDTO> fileIdSearch(List<String> businessId, List<OrderByBuilder> sort, Integer count, List<Object> searchAfter) {
        // 查询信息构建
        IdsQueryBuilder builder = QueryBuilders.idsQuery();
        builder.addIds(businessId.toArray(String[]::new));

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 根据排序分页参数构建排序分页对象
        ElasticUtil.builderSortPageable(sort, FILE_SEARCH_SORT_FIELD, AUTO_ID, searchAfter, count, queryBuilder);

        // 执行搜素请求
        return restTemplate.search(queryBuilder.build(), DiskFileDTO.class);
    }

    /**
     * 根据文件父级业务标识批量查询用户文件信息
     *
     * @param userId      用户系统内唯一标识
     * @param parentId    文件父级业务标识
     * @param count       单次查询的数量、每页大小
     * @param searchAfter 上一页游标，为空时默认第一页
     * @return 用户文件实体信息ES数据模型
     */
    @Override
    public SearchHits<DiskFileDTO> fileParentIdSearch(String userId, List<String> parentId, Integer count, List<Object> searchAfter) {
        // 查询信息构建
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.termQuery(USER_ID, userId));
        builder.must(QueryBuilders.termsQuery(FILE_PARENT_ID, parentId));

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 游标不为空时，加入游标查询
        if (CollectionUtil.isNotEmpty(searchAfter)) {
            queryBuilder.withSearchAfter(searchAfter);
        }

        // 排序字段构建，默认按照业务标识正序排列
        queryBuilder.withSorts(SortBuilders.fieldSort(AUTO_ID).order(SortOrder.ASC));

        // 构建分页语句
        queryBuilder.withPageable(PageRequest.of(NumberConstant.INTEGER_ZERO, count));

        // 执行搜素请求
        return restTemplate.search(queryBuilder.build(), DiskFileDTO.class);
    }
}
