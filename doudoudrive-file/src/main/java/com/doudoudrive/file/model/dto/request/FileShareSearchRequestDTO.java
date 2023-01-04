package com.doudoudrive.file.model.dto.request;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * <p>文件分享数据搜索请求数据模型</p>
 * <p>2023-01-04 05:22</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShareSearchRequestDTO {

    /**
     * 上一页游标，为空时默认第一页
     */
    private String marker;

    /**
     * 单次查询的数量、每页大小
     */
    private Integer count;

    /**
     * 排序字段配置
     */
    @Valid
    @NotNull(message = "不支持的排序")
    private OrderByBuilder sort;

    /**
     * 单次查询的数量、每页大小
     *
     * @return 返回每页的大小，默认为10，最小为1，最大100
     */
    public Integer getCount() {
        return Math.min(Math.max(Optional.ofNullable(count).orElse(NumberConstant.INTEGER_TEN), NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_HUNDRED);
    }
}
