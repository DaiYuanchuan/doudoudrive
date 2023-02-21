package com.doudoudrive.search.util;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.global.BusinessExceptionUtil;
import com.doudoudrive.common.global.StatusCodeEnum;
import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import com.doudoudrive.common.util.lang.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>ES搜索通用工具类</p>
 * <p>2022-10-06 12:09</p>
 *
 * @author Dan
 **/
public class ElasticUtil {

    /**
     * 根据排序分页参数构建排序分页对象
     *
     * @param sort         排序参数
     * @param sortField    业务中允许排序的字段
     * @param defaultField 默认排序字段
     * @param searchAfter  分页参数
     * @param count        每页数量
     * @param queryBuilder 查询构建器
     */
    public static void builderSortPageable(List<OrderByBuilder> sort, List<String> sortField, String defaultField,
                                           List<Object> searchAfter, Integer count, NativeSearchQueryBuilder queryBuilder) {
        // 游标不为空时，加入游标查询
        if (CollectionUtil.isNotEmpty(searchAfter)) {
            queryBuilder.withSearchAfter(searchAfter);
        }

        // 排序不为空时，加入排序
        builderSort(sort, sortField, defaultField, queryBuilder);

        // 构建分页语句
        queryBuilder.withPageable(PageRequest.of(NumberConstant.INTEGER_ZERO, count));
    }

    /**
     * 根据排序参数构建排序对象
     *
     * @param sort         入参中需要进行排序的参数字段
     * @param sortField    业务中允许排序的字段
     * @param defaultField 默认排序字段
     * @param queryBuilder 查询构建器
     */
    public static void builderSort(List<OrderByBuilder> sort, List<String> sortField, String defaultField, NativeSearchQueryBuilder queryBuilder) {
        List<SortBuilder<?>> fieldSortBuilderList = new ArrayList<>();
        if (CollectionUtil.isEmpty(sort)) {
            // 添加默认排序字段，默认按照业务标识正序排列
            fieldSortBuilderList.add(SortBuilders.fieldSort(defaultField).order(SortOrder.ASC));
        } else {
            for (OrderByBuilder orderByBuilder : sort) {
                if (StringUtils.isNotBlank(orderByBuilder.getOrderBy()) && StringUtils.isNotBlank(orderByBuilder.getOrderDirection())) {
                    // 判断字段名是否存在于枚举中
                    if (sortField.stream().noneMatch(field -> field.equals(orderByBuilder.getOrderBy()))
                            || ConstantConfig.OrderDirection.noneMatch(orderByBuilder.getOrderDirection())) {
                        BusinessExceptionUtil.throwBusinessException(StatusCodeEnum.UNSUPPORTED_SORT);
                    }
                    fieldSortBuilderList.add(SortBuilders.fieldSort(orderByBuilder.getOrderBy())
                            .order(ConstantConfig.OrderDirection.ASC.getDirection().equals(orderByBuilder.getOrderDirection()) ? SortOrder.ASC : SortOrder.DESC));
                }
            }
        }
        // 排序字段构建
        queryBuilder.withSorts(fieldSortBuilderList);
    }
}
