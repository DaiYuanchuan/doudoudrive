package com.doudoudrive.search.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.doudoudrive.search.constant.SearchConstantConfig;
import com.doudoudrive.search.manager.UserInfoSearchManager;
import com.doudoudrive.search.model.elasticsearch.UserInfoDTO;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>用户信息搜索服务的通用业务处理层接口实现</p>
 * <p>2022-03-20 22:54</p>
 *
 * @author Dan
 **/
@Service("userInfoSearchManager")
public class UserInfoSearchManagerImpl implements UserInfoSearchManager {

    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 保存用户信息，es中保存用户信息
     * <pre>
     *     先入库，然后再往es里存用户信息
     * </pre>
     *
     * @param userInfoDTO 用户实体信息ES数据模型
     */
    @Override
    public void saveUserInfo(UserInfoDTO userInfoDTO) {
        // 构建查询请求
        restTemplate.save(userInfoDTO);
    }

    /**
     * 根据用户系统内唯一标识删除指定用户信息
     * <pre>
     *     先删库，然后再删es里存的用户信息
     * </pre>
     *
     * @param id 用户系统内唯一标识
     */
    @Override
    public void deleteUserInfo(String id) {
        restTemplate.delete(id, UserInfoDTO.class);
    }

    /**
     * 根据用户系统内唯一标识去更新用户信息
     *
     * @param id          用户系统内唯一标识
     * @param userInfoDTO 用户实体信息ES数据模型
     */
    @Override
    public void updateUserInfo(String id, UserInfoDTO userInfoDTO) {
        // 将用户信息转为map
        Map<String, Object> userInfoMap = BeanUtil.beanToMap(userInfoDTO, false, true);
        // 构建更新的es请求
        restTemplate.update(UpdateQuery
                .builder(id)
                .withDocument(Document.from(userInfoMap))
                .build(), IndexCoordinates.of(SearchConstantConfig.IndexName.USERINFO));
    }

    /**
     * 用户登录信息搜索
     * <pre>
     *     根据用户名、用户邮箱、用户手机号进行精确搜索
     * </pre>
     *
     * @param username 用户登录的用户名(用户名、用户邮箱、用户手机号)
     * @return 用户实体信息ES数据模型
     */
    @Override
    public UserInfoDTO userLoginInfoSearch(String username) {
        // 构建查询请求
        SearchHits<UserInfoDTO> search = restTemplate.search(new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("userName", username))
                        .should(QueryBuilders.termQuery("userEmail", username))
                        .should(QueryBuilders.termQuery("userTel", username)))
                .build(), UserInfoDTO.class);
        return search.isEmpty() ? null : search.getSearchHits().get(0).getContent();
    }
}
