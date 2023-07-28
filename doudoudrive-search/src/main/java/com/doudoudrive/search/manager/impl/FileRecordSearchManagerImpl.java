package com.doudoudrive.search.manager.impl;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.util.lang.CollectionUtil;
import com.doudoudrive.common.util.lang.ReflectUtil;
import com.doudoudrive.search.manager.FileRecordSearchManager;
import com.doudoudrive.search.model.elasticsearch.FileRecordDTO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>文件临时操作记录信息搜索服务的通用业务处理层接口实现</p>
 * <p>2023-07-27 14:47</p>
 *
 * @author Dan
 **/
@Service("fileRecordSearchManager")
public class FileRecordSearchManagerImpl implements FileRecordSearchManager {

    private static final String USER_ID = ReflectUtil.property(FileRecordDTO::getUserId);
    private static final String FILE_ETAG = ReflectUtil.property(FileRecordDTO::getFileEtag);
    private static final String ACTION = ReflectUtil.property(FileRecordDTO::getAction);
    private static final String ACTION_TYPE = ReflectUtil.property(FileRecordDTO::getActionType);

    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * <p>批量保存文件临时操作记录信息</p>
     *
     * @param fileRecordDTO 文件临时操作记录信息ES数据模型
     */
    @Override
    public void saveFileRecord(List<FileRecordDTO> fileRecordDTO) {
        if (CollectionUtil.isNotEmpty(fileRecordDTO)) {
            // 构建保存请求
            restTemplate.save(fileRecordDTO);
        }
    }

    /**
     * 删除指定状态的文件操作记录数据
     *
     * @param userId     指定用户
     * @param action     动作，参见{@link ConstantConfig.FileRecordAction.ActionEnum}
     * @param actionType 动作类型，参见{@link ConstantConfig.FileRecordAction.ActionTypeEnum}
     * @param etag       文件唯一标识
     * @return 删除的文件临时操作记录信息
     */
    @Override
    public ByQueryResponse deleteAction(String userId, String action, String actionType, List<String> etag) {
        // 查询信息构建
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        // 根据用户标识和动作类型删除
        if (StringUtils.isNotBlank(userId)) {
            builder.must(QueryBuilders.termQuery(USER_ID, userId));
        }

        builder.must(QueryBuilders.termQuery(ACTION, action));
        builder.must(QueryBuilders.termQuery(ACTION_TYPE, actionType));
        builder.must(QueryBuilders.termsQuery(FILE_ETAG, etag));

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);
        // 批量删除数据
        return restTemplate.delete(queryBuilder.build(), FileRecordDTO.class);
    }

    /**
     * 获取指定状态的文件操作记录数据
     *
     * @param userId     指定用户
     * @param action     动作，参见{@link ConstantConfig.FileRecordAction.ActionEnum}
     * @param actionType 动作类型，参见{@link ConstantConfig.FileRecordAction.ActionTypeEnum}
     * @param etag       文件唯一标识
     * @return 返回指定状态的文件操作记录数据
     */
    @Override
    public SearchHits<FileRecordDTO> fileRecordSearch(String userId, String action, String actionType, String etag) {
        // 查询信息构建
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(userId)) {
            builder.must(QueryBuilders.termQuery(USER_ID, userId));
        }

        builder.must(QueryBuilders.termQuery(ACTION, action));
        builder.must(QueryBuilders.termQuery(ACTION_TYPE, actionType));

        if (StringUtils.isNotBlank(etag)) {
            builder.must(QueryBuilders.termQuery(FILE_ETAG, etag));
        }

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);
        // 执行搜素请求
        return restTemplate.search(queryBuilder.build(), FileRecordDTO.class);
    }
}
