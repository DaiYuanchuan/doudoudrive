package com.doudoudrive.common.model.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>分页查询的统一出参</p>
 * <p>2022-03-08 09:51</p>
 *
 * @author Dan
 **/
@Data
@Builder
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * 当前页码，当前处于第几页
     */
    private Integer page;

    /**
     * 当前一页的大小
     */
    private Integer pageSize;

    /**
     * 分页的总数
     */
    private Long total;

    /**
     * 数据行
     */
    private List<T> rows;

    /**
     * 初始化分页响应对象
     */
    public PageResponse() {
        this.rows = new ArrayList<>();
        this.total = 0L;
    }
}
