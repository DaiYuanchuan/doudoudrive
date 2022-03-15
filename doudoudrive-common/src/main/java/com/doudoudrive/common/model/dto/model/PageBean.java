package com.doudoudrive.common.model.dto.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Optional;

/**
 * <p>分页的Bean</p>
 * <p>2022-03-08 09:33</p>
 *
 * @author Dan
 **/
@Builder
@AllArgsConstructor
public class PageBean {

    /**
     * 页码, 第几页
     */
    private Integer page;

    /**
     * 每页大小、记录数
     */
    private Integer pageSize;

    /**
     * 起始页
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Integer start;

    public PageBean(Integer page, Integer pageSize) {
        super();
        this.page = page;
        this.pageSize = pageSize;
    }

    /**
     * 默认每页条数
     *
     * @param page 页码
     */
    public PageBean(Integer page) {
        super();
        this.page = page;
    }

    public PageBean() {
        super();
    }

    /**
     * 第几页
     *
     * @return 返回页码，默认为1，最小为1
     */
    public Integer getPage() {
        return Math.max(Optional.ofNullable(page).orElse(1), 1);
    }

    /**
     * 第几页
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * 每页记录数
     *
     * @return 返回每页的大小，默认为10，最小为1，最大100
     */
    public Integer getPageSize() {
        return Math.min(Math.max(Optional.ofNullable(pageSize).orElse(10), 1), 100);
    }

    /**
     * 每页记录数
     *
     * @param pageSize 每页的大小
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 起始页
     *
     * @return 获取SQL中的起始页
     */
    public Integer getStart() {
        return (page - 1) * pageSize;
    }
}