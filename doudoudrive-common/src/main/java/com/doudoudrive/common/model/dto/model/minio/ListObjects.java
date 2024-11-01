package com.doudoudrive.common.model.dto.model.minio;

import com.alibaba.fastjson.annotation.JSONField;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.constant.NumberConstant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * <p>列出存储桶中的部分或全部（最多 1000 个）对象</p>
 * <p>2024-04-24 15:04</p>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_ListObjects.html">ListObjects</a>
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListObjects implements Serializable {

    @Serial
    private static final long serialVersionUID = 8755883851528616573L;

    /**
     * 需要查询的存储桶
     */
    private String bucket;

    /**
     * 分页标识，可以对列表的结果进行分页
     * 仅适用于ListObjectsV2接口
     */
    @JSONField(name = "continuation-token")
    private String continuationToken;

    /**
     * 分隔符是用于对键进行分组的字符
     * 对于目录存储桶，唯一受支持的分隔符: /
     */
    private String delimiter;

    /**
     * 用于对响应中的对象键进行编码的编码类型
     */
    @JSONField(name = "encoding-type")
    private String encodingType;

    /**
     * 返回结果中每个键的所有者信息
     * 对于目录存储桶，存储桶拥有者将作为对象所有者返回
     */
    @JSONField(name = "fetch-owner")
    private Boolean fetchOwner;

    /**
     * 设置响应中返回的最大键数量
     * 默认情况下，该操作将返回多达 1000 个键名
     */
    @JSONField(name = "max-keys")
    private Integer maxKeys;

    /**
     * 前缀，将响应限制为以指定前缀开头的键
     */
    private String prefix;

    /**
     * 从指定键之后开始返回键，
     * 在此之后开始查询 指定的键，StartAfter 可以是存储桶中的任何键
     * 仅适用于ListObjectsV2
     */
    @JSONField(name = "start-after")
    private String startAfter;

    /**
     * 标记，用于标识查询的起始位置，和startAfter一样
     * 仅适用于ListObjectsV1
     */
    private String marker;

    /**
     * 是否需要递归列出对象信息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Boolean isRecursive;

    /**
     * 是否使用 API 版本 1，就是ListObjectsV1
     * 默认使用ListObjectsV2
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Boolean useApiVersion1;

    /**
     * 获取分隔符，默认为 /
     *
     * @return 返回分隔符
     */
    public String getDelimiter() {
        Boolean recursive = Optional.ofNullable(isRecursive).orElse(Boolean.TRUE);
        if (recursive) {
            return StringUtils.EMPTY;
        }
        return StringUtils.isBlank(delimiter) ? ConstantConfig.SpecialSymbols.SLASH : delimiter;
    }

    /**
     * 获取最大键数量，默认为100，最小为1，最大1000
     *
     * @return 返回最大键数量
     */
    public Integer getMaxKeys() {
        // 默认值
        Integer defaultMaxKeys = Optional.ofNullable(maxKeys).orElse(NumberConstant.INTEGER_HUNDRED);
        return Math.min(Math.max(defaultMaxKeys, NumberConstant.INTEGER_ONE), NumberConstant.INTEGER_ONE_THOUSAND);
    }

    /**
     * 是否使用 API 版本 1，就是ListObjectsV1
     * 默认使用ListObjectsV2
     *
     * @return 返回是否使用 API 版本 1
     */
    public Boolean getUseApiVersion1() {
        return Optional.ofNullable(useApiVersion1).orElse(Boolean.FALSE);
    }

    /**
     * 该参数标识 使用 ListObjectsV2 还是 ListObjects
     * 使用 ListObjectsV2 时取值为 2，使用 ListObjects 时取值为 空
     * 默认使用 ListObjectsV2
     *
     * @return 返回 list-type 参数
     */
    @JSONField(name = "list-type")
    public String listType() {
        return getUseApiVersion1() ? null : NumberConstant.STRING_TWO;
    }
}
