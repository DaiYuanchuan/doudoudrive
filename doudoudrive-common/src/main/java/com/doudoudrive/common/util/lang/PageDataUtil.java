package com.doudoudrive.common.util.lang;

import cn.hutool.core.util.StrUtil;
import com.doudoudrive.common.model.dto.model.PageBean;
import com.doudoudrive.common.model.dto.response.PageResponse;

import java.util.Optional;

/**
 * <p>构建分页的sql语句</p>
 * <p>2022-03-08 09:33</p>
 *
 * @author Dan
 **/
public class PageDataUtil {

    /**
     * 返回分页limit数据
     *
     * @param pageBean 用来分页的实体类
     * @return 返回最终拼接处来的SQL分页语句
     */
    public static String pangingSql(PageBean pageBean) {
        if (pageBean != null) {
            return StrUtil.format(" limit {},{} ", pageBean.getStart(), pageBean.getPageSize());
        } else {
            return "";
        }
    }

    /**
     * 封装 方法 传入页码直接生成 limit 语句
     * pageSize = 10
     *
     * @param page 页码
     * @return 返回最终拼接处来的SQL分页语句
     */
    public static String pangingSql(Integer page) {
        // 页码 最小为1
        page = Math.max(page, 1);
        return pangingSql(PageBean.builder().page(page).pageSize(10).build());
    }

    /**
     * 封装 方法 传入页码、页码大小直接生成 limit 语句
     *
     * @param page         页码
     * @param pageSize     页码大小
     * @param pageResponse 分页查询的响应参数
     * @return 返回最终拼接处来的SQL分页语句
     */
    public static String pangingSql(Integer page, Integer pageSize, PageResponse<?> pageResponse) {
        if (page == null || pageSize == null) {
            return "";
        }
        page = Math.max(page, 1);
        pageSize = Math.max(pageSize, 1);
        PageBean pageBean = PageBean.builder().page(page).pageSize(pageSize).build();
        // 构建分页响应对象中关于页码部分
        pageResponse = Optional.ofNullable(pageResponse).orElse(new PageResponse<>());
        pageResponse.setPage(pageBean.getPage());
        pageResponse.setPageSize(pageBean.getPageSize());
        return pangingSql(pageBean);
    }
}