package com.doudoudrive.common.model.dto.request;

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
     * 是否需要更新当前链接的浏览次数
     *
     * @return 默认为false 不更新
     */
    public Boolean getUpdateViewCount() {
        return Optional.ofNullable(updateViewCount).orElse(Boolean.FALSE);
    }
}
