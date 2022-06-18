package com.doudoudrive.search.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import com.doudoudrive.common.model.dto.request.QueryElasticsearchDiskFileRequestDTO;
import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.ReflectUtil;
import com.doudoudrive.search.manager.DiskFileSearchManager;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private static final String FILE_ETAG = ReflectUtil.property(DiskFile::getFileEtag);
    private static final String FILE_MIME_TYPE = ReflectUtil.property(DiskFile::getFileMimeType);
    private static final String FILE_FOLDER = ReflectUtil.property(DiskFile::getFileFolder);
    private static final String COLLECT = ReflectUtil.property(DiskFile::getCollect);
    private static final String FILE_NAME = ReflectUtil.property(DiskFile::getFileName);

    /**
     * 保存用户文件信息，es中保存用户文件信息
     * <pre>
     *     先入库，然后再往es里存用户文件信息
     * </pre>
     *
     * @param diskFileDTO 用户文件实体信息ES数据模型
     */
    @Override
    public void saveDiskFile(DiskFileDTO diskFileDTO) {
        // 构建保存请求
        restTemplate.save(diskFileDTO);
    }

    /**
     * 根据文件业务标识删除指定文件信息
     * <pre>
     *     先删库，然后再删es里存的用户文件信息
     * </pre>
     *
     * @param id 文件业务标识
     */
    @Override
    public void deleteDiskFile(String id) {
        restTemplate.delete(id, DiskFileDTO.class);
    }

    /**
     * 根据文件业务标识去更新用户文件信息
     *
     * @param id          用户系统内唯一标识
     * @param diskFileDTO 用户文件实体信息ES数据模型
     */
    @Override
    public void updateDiskFile(String id, DiskFileDTO diskFileDTO) {
        // 将用户文件信息转为map
        Map<String, Object> diskFileMap = BeanUtil.beanToMap(diskFileDTO, Boolean.FALSE, Boolean.TRUE);
        // 构建更新的es请求
        restTemplate.update(UpdateQuery
                .builder(id)
                .withDocument(Document.from(diskFileMap))
                .build(), IndexCoordinates.of(ConstantConfig.IndexName.DISK_FILE));
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
            // 文件名使用分词查询，使用AND方式连接分词(我爱中华人民共和国国歌，我爱(AND|OR)国歌)
            builder.must(QueryBuilders.matchQuery(FILE_NAME, requestDTO.getFileName()).operator(Operator.AND));
        }

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 游标不为空时，加入游标查询
        if (CollectionUtil.isNotEmpty(requestDTO.getSearchAfter())) {
            queryBuilder.withSearchAfter(requestDTO.getSearchAfter());
        }

        // 指定字段排序
        List<SortBuilder<?>> fieldSortBuilderList = new ArrayList<>();
        if (CollectionUtil.isEmpty(requestDTO.getSort())) {
            // 添加默认排序字段，默认按照业务标识正序排列
            fieldSortBuilderList.add(SortBuilders.fieldSort(AUTO_ID).order(SortOrder.ASC));
        } else {
            for (OrderByBuilder orderByBuilder : requestDTO.getSort()) {
                if (StringUtils.isNotBlank(orderByBuilder.getOrderBy()) && StringUtils.isNotBlank(orderByBuilder.getOrderDirection())) {
                    // 判断字段名是否存在于枚举中
                    if (ConstantConfig.DiskFileSearchOrderBy.noneMatch(orderByBuilder.getOrderBy())
                            || ConstantConfig.OrderDirection.noneMatch(orderByBuilder.getOrderDirection())) {
                        BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.UNSUPPORTED_SORT);
                    }
                    fieldSortBuilderList.add(SortBuilders.fieldSort(orderByBuilder.getOrderBy())
                            .order(ConstantConfig.OrderDirection.ASC.direction.equals(orderByBuilder.getOrderDirection()) ? SortOrder.ASC : SortOrder.DESC));
                }
            }
        }

        // 排序字段构建
        queryBuilder.withSorts(fieldSortBuilderList);

        // 构建分页语句
        queryBuilder.withPageable(PageRequest.of(NumberConstant.INTEGER_ZERO, requestDTO.getCount()));

        // 执行搜素请求
        return restTemplate.search(queryBuilder.build(), DiskFileDTO.class);
    }
}
