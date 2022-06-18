package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * <p>搜索es用户文件信息时的请求数据模型</p>
 * <p>2022-06-17 18:21</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryElasticsearchDiskFileRequestDTO {

    /**
     * 用户系统内唯一标识
     */
    @NotBlank(message = "用户标识不能为空")
    @Size(max = 35, message = "用户标识长度错误")
    private String userId;

    /**
     * 业务标识
     */
    @Size(max = 35, message = "未找到指定文件")
    private String businessId;

    /**
     * 文件父级标识
     */
    @Size(max = 35, message = "未找到指定文件夹")
    private String fileParentId;

    /**
     * 文件名(模糊搜索)
     */
    @Size(max = 80, message = "未找到指定文件")
    private String fileName;

    /**
     * 文件的mime类型
     */
    @Size(max = 100, message = "未找到指定文件")
    private String fileMimeType;

    /**
     * 文件的ETag(资源的唯一标识)
     */
    @Size(max = 50, message = "未找到指定文件")
    private String fileEtag;

    /**
     * 是否为文件夹(0:false；1:true)
     */
    private Boolean fileFolder;

    /**
     * 当前文件是否被收藏(0:false；1:true)
     */
    private Boolean collect;

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
     * 单次查询的数量、每页大小
     *
     * @return 返回每页的大小，默认为10，最小为1，最大100
     */
    public Integer getCount() {
        return Math.min(Math.max(Optional.ofNullable(count).orElse(NumberConstant.INTEGER_TEN), NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_HUNDRED);
    }
}
