package com.doudoudrive.search.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.util.lang.ReflectUtil;
import com.doudoudrive.search.manager.UserInfoSearchManager;
import com.doudoudrive.search.model.dto.response.UserInfoKeyExistsSearchResponseDTO;
import com.doudoudrive.search.model.elasticsearch.UserInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
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
     * 用户名、邮箱、电话对应字段的字符串
     */
    private static final String USER_NAME = ReflectUtil.property(UserInfoDTO::getUserName);
    private static final String USER_EMAIL = ReflectUtil.property(UserInfoDTO::getUserEmail);
    private static final String USER_TEL = ReflectUtil.property(UserInfoDTO::getUserTel);

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
        // 构建保存请求
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
        Map<String, Object> userInfoMap = BeanUtil.beanToMap(userInfoDTO, Boolean.FALSE, Boolean.TRUE);
        // 构建更新的es请求
        restTemplate.update(UpdateQuery
                .builder(id)
                .withDocument(Document.from(userInfoMap))
                .build(), IndexCoordinates.of(ConstantConfig.IndexName.USERINFO));
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
                        .should(QueryBuilders.termQuery(USER_NAME, username))
                        .should(QueryBuilders.termQuery(USER_EMAIL, username))
                        .should(QueryBuilders.termQuery(USER_TEL, username)))
                .build(), UserInfoDTO.class);
        return search.isEmpty() ? null : search.getSearchHits().get(NumberConstant.INTEGER_ZERO).getContent();
    }

    /**
     * 查询用户关键信息是否存在
     *
     * @param username  用户名
     * @param userEmail 用户邮箱
     * @param userTel   用户手机号
     * @return 查询用户关键信息是否存在的响应数据模型
     */
    @Override
    public UserInfoKeyExistsSearchResponseDTO userInfoKeyExistsSearch(String username, String userEmail, String userTel) {
        // 查询信息构建
        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(username)) {
            builder.should(QueryBuilders.termQuery(USER_NAME, username));
        }

        if (StringUtils.isNotBlank(userEmail)) {
            builder.should(QueryBuilders.termQuery(USER_EMAIL, userEmail));
        }

        if (StringUtils.isNotBlank(userTel)) {
            builder.should(QueryBuilders.termQuery(USER_TEL, userTel));
        }

        // 执行搜索
        SearchHits<UserInfoDTO> search = restTemplate.search(new NativeSearchQueryBuilder()
                .withQuery(builder)
                .build(), UserInfoDTO.class);

        // 构建响应数据模型
        UserInfoKeyExistsSearchResponseDTO responseDTO = UserInfoKeyExistsSearchResponseDTO.builder()
                .exists(Boolean.TRUE)
                .searchHits(search)
                .build();

        // 搜素结果不存在
        if (search.isEmpty()) {
            responseDTO.setExists(Boolean.FALSE);
            return responseDTO;
        }

        // 判断搜索结果
        for (SearchHit<UserInfoDTO> userInfo : search.getSearchHits()) {
            if (StringUtils.isNotBlank(username) && username.equals(userInfo.getContent().getUserName())) {
                responseDTO.setDescribe(StatusCodeEnum.USER_ALREADY_EXIST);
            }
            if (StringUtils.isNotBlank(userEmail) && userEmail.equals(userInfo.getContent().getUserEmail())) {
                responseDTO.setDescribe(StatusCodeEnum.USER_EMAIL_ALREADY_EXIST);
            }
            if (StringUtils.isNotBlank(userTel) && userTel.equals(userInfo.getContent().getUserTel())) {
                responseDTO.setDescribe(StatusCodeEnum.USER_TEL_ALREADY_EXIST);
            }
        }
        return responseDTO;
    }

    /**
     * 根据用户系统内唯一标识查询指定用户信息
     *
     * @param userId 用户系统内唯一标识
     * @return 用户实体信息ES数据模型，无法查询到时返回null
     */
    @Override
    public UserInfoDTO userIdQuery(String userId) {
        // 查询信息构建
        IdsQueryBuilder builder = QueryBuilders.idsQuery();
        builder.addIds(userId);

        // 查询请求构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(builder);

        // 执行搜素请求
        SearchHits<UserInfoDTO> query = restTemplate.search(queryBuilder.build(), UserInfoDTO.class);
        return query.isEmpty() ? null : query.getSearchHits().get(NumberConstant.INTEGER_ZERO).getContent();
    }
}
