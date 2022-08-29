package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.constant.NumberConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * <p>搜索es用户父级文件标识信息时的请求数据模型</p>
 * <p>2022-08-29 15:56</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchDiskFileParentIdRequestDTO {

    /**
     * 用户系统内唯一标识
     */
    @NotBlank(message = "用户标识不能为空")
    @Size(max = 35, message = "用户标识长度错误")
    private String userId;

    /**
     * 文件父级标识
     */
    @NotEmpty(message = "参数集合为空")
    @Size(max = 1000, message = "请不要一次性操作太多数据~")
    private List<String> parentId;

    /**
     * 单次查询的数量、每页大小
     */
    private Integer count;

    /**
     * 上一页游标，为空时默认第一页
     */
    private List<Object> searchAfter;

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
