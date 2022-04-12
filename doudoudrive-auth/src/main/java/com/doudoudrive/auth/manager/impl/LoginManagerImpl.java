package com.doudoudrive.auth.manager.impl;

import com.doudoudrive.auth.manager.LoginManager;
import com.doudoudrive.auth.manager.SysUserRoleManager;
import com.doudoudrive.auth.model.convert.UserInfoConvert;
import com.doudoudrive.auth.model.dto.UserInfoDTO;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.model.dto.model.DiskUserModel;
import com.doudoudrive.common.model.dto.response.UserLoginResponseDTO;
import com.doudoudrive.common.model.pojo.DiskUser;
import com.doudoudrive.common.util.lang.ReflectUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

/**
 * <p>登录服务通用业务处理层接口实现</p>
 * <p>2022-04-05 18:39</p>
 *
 * @author Dan
 **/
@Service("loginManager")
public class LoginManagerImpl implements LoginManager {

    private UserInfoConvert userInfoConvert;

    private SysUserRoleManager sysUserRoleManager;

    private ElasticsearchRestTemplate restTemplate;

    @Autowired(required = false)
    public void setUserInfoConvert(UserInfoConvert userInfoConvert) {
        this.userInfoConvert = userInfoConvert;
    }

    @Autowired
    public void setSysUserRoleManager(SysUserRoleManager sysUserRoleManager) {
        this.sysUserRoleManager = sysUserRoleManager;
    }

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 用户名、邮箱、密码对应字段的字符串
     */
    private static final String USER_NAME = ReflectUtil.property(DiskUser::getUserName);
    private static final String USER_EMAIL = ReflectUtil.property(DiskUser::getUserEmail);
    private static final String USER_TEL = ReflectUtil.property(DiskUser::getUserTel);

    /**
     * 从session中获取当前登录的用户信息
     * 不能从缓存中获取用户信息时，通过当前登录的用户名去搜素用户信息
     *
     * @return 返回用户登录模块响应数据DTO模型
     */
    @Override
    public UserLoginResponseDTO getUserInfoToSession() {
        try {
            if (SecurityUtils.getSubject().isAuthenticated()) {
                Subject subject = SecurityUtils.getSubject();
                Session session = subject.getSession(true);
                DiskUserModel userInfo = (DiskUserModel) session.getAttribute(ConstantConfig.Cache.USERINFO_CACHE);
                if (userInfo == null) {
                    String username = (String) SecurityUtils.getSubject().getPrincipal();
                    // 通过登陆的 用户名 查找对应的用户信息
                    UserInfoDTO esUserInfo = usernameSearch(username);
                    if (esUserInfo == null) {
                        return null;
                    }
                    userInfo = userInfoConvert.usernameSearchResponseConvert(esUserInfo);
                    // 获取当前登录用户所有角色、权限信息
                    userInfo.setRoleInfo(sysUserRoleManager.listSysUserRoleInfo(userInfo.getBusinessId()));
                    session.setAttribute(ConstantConfig.Cache.USERINFO_CACHE, userInfo);
                }

                // 构建用户登录模块响应数据DTO模型
                return UserLoginResponseDTO.builder()
                        .userInfo(userInfo)
                        .token(session.getId().toString())
                        .build();
            }
        } catch (UnknownSessionException ignored) {
        }
        return null;
    }

    /**
     * 用户登录信息搜索，此接口专为登录鉴权时使用，其他场景请调用搜索服务接口
     * <pre>
     *     根据用户名、用户邮箱、用户手机号进行精确搜索
     * </pre>
     *
     * @param username 用户登录的用户名(用户名、用户邮箱、用户手机号)
     * @return 用户实体信息ES数据模型
     */
    @Override
    public UserInfoDTO usernameSearch(String username) {
        // 构建查询请求
        SearchHits<UserInfoDTO> search = restTemplate.search(new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery(USER_NAME, username))
                        .should(QueryBuilders.termQuery(USER_EMAIL, username))
                        .should(QueryBuilders.termQuery(USER_TEL, username)))
                .build(), UserInfoDTO.class);
        return search.isEmpty() ? null : search.getSearchHits().get(0).getContent();
    }
}
