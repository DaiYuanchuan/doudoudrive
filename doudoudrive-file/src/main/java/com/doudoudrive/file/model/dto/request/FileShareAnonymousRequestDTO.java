package com.doudoudrive.file.model.dto.request;

import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.model.dto.model.OrderByBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

/**
 * <p>获取匿名分享的文件请求数据模型</p>
 * <p>2022-09-29 19:23</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShareAnonymousRequestDTO {

    /**
     * 分享的短链接标识
     */
    @NotBlank(message = "你打开的链接有误，请重试")
    @Size(max = 35, message = "你打开的链接有误，请重试")
    private String shareId;

    /**
     * 提取码(为空时表示不需要提取码)
     */
    @Size(max = 6, message = "请输入6位数字或字母")
    private String sharePwd;

    /**
     * 文件父级标识
     */
    @Size(max = 35, message = "未找到指定文件夹")
    private String fileParentId;

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
     * 是否需要更新当前链接的浏览次数
     */
    private Boolean updateViewCount;

    /**
     * 分享记录中文件夹的key值，用于获取文件夹下的文件列表，以及文件的校验
     */
    private String key;

    /**
     * 单次查询的数量、每页大小
     *
     * @return 返回每页的大小，默认为10，最小为1，最大100
     */
    public Integer getCount() {
        return Math.min(Math.max(Optional.ofNullable(count).orElse(NumberConstant.INTEGER_TEN), NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_HUNDRED);
    }

    /**
     * 是否需要更新当前链接的浏览次数
     *
     * @return 默认为false 不更新
     */
    public Boolean getUpdateViewCount() {
        return Optional.ofNullable(updateViewCount).orElse(Boolean.FALSE);
    }
}
