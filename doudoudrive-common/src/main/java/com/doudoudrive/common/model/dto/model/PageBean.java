package com.doudoudrive.common.model.dto.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.doudoudrive.common.constant.NumberConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import javax.validation.constraints.Max;
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
    @Setter
    @Max(value = 500, message = "超出最大支持分页数500")
    private Integer page;

    /**
     * 每页大小、记录数
     */
    @Setter
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
        return Math.max(Optional.ofNullable(page).orElse(NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_ONE);
    }

    /**
     * 每页记录数
     *
     * @return 返回每页的大小，默认为10，最小为1，最大100
     */
    public Integer getPageSize() {
        return Math.min(Math.max(Optional.ofNullable(pageSize).orElse(NumberConstant.INTEGER_TEN), NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_HUNDRED);
    }

    /**
     * 起始页
     *
     * @return 获取SQL中的起始页
     */
    public Integer getStart() {
        return (page - NumberConstant.INTEGER_ONE) * pageSize;
    }
}