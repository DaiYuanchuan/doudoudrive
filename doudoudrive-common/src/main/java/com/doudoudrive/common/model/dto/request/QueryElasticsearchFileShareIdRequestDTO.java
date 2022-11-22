package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * <p>搜索es用户文件分享标识数据时的请求数据模型</p>
 * <p>2022-09-28 00:04</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchFileShareIdRequestDTO {

    @NotEmpty(message = "参数集合为空")
    @Size(max = 1000, message = "请不要一次性操作太多数据~")
    private List<String> shareId;

    /**
     * 是否需要更新当前链接的浏览次数
     */
    private Boolean updateViewCount;

    /**
     * 是否需要更新当前链接的保存、转存次数
     */
    private Boolean updateSaveCount;

    /**
     * 上一页游标，为空时默认第一页
     */
    private List<Object> searchAfter;

    /**
     * 单次查询的数量、每页大小
     */
    private Integer count;

    /**
     * 排序配置
     */
    @Size(max = 3, message = "不支持的排序")
    private List<OrderByBuilder> sort;

    /**
     * 是否需要更新当前链接的浏览次数
     *
     * @return 默认为false 不更新
     */
    public Boolean getUpdateViewCount() {
        return Optional.ofNullable(updateViewCount).orElse(Boolean.FALSE);
    }

    /**
     * 是否需要更新当前链接的保存、转存次数
     *
     * @return 默认为false 不更新
     */
    public Boolean getUpdateSaveCount() {
        return Optional.ofNullable(updateSaveCount).orElse(Boolean.FALSE);
    }

    /**
     * 单次查询的数量、每页大小
     *
     * @return 返回每页的大小，默认为10，最小为1，最大10000
     */
    public Integer getCount() {
        return Math.min(Math.max(Optional.ofNullable(count).orElse(NumberConstant.INTEGER_TEN),
                NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_TEN_THOUSAND);
    }
}
