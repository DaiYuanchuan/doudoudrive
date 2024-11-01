package com.doudoudrive.common.model.dto.model.minio;

import com.alibaba.fastjson.annotation.JSONField;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.common.util.sign.s3.AwsSignerUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>API_CopyObject: 复制一个对象请求</p>
 * <p>2024-04-27 23:37</p>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_CopyObject.html">CopyObject</a>
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyObjectRequest {

    /**
     * 源存储桶名称
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String sourceBucket;

    /**
     * 源对象键
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String sourceKey;

    /**
     * 源对象版本id
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String sourceVersionId;

    /**
     * 目标存储桶名称
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String targetBucket;

    /**
     * 目标对象键
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String targetKey;

    /**
     * 目标对象的内容类型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String targetContentType;

    /**
     * 目标对象的内容编码
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String targetContentEncoding;

    /**
     * 用于存储对象的存储类型
     * STANDARD | REDUCED_REDUNDANCY 等
     */
    @JSONField(name = "x-amz-storage-class")
    private String storageClass;

    /**
     * 用于确保只有当源对象（即你要从中复制的对象）的ETag与提供的值匹配时，复制操作才会进行
     * 使用场景通常是为了防止在复制过程中源对象的内容被意外更改
     * 如果源对象的ETag与提供的 x-amz-copy-source-if-match 值不匹配，则S3会返回一个 412 Precondition Failed 错误，表示复制操作未能进行
     */
    @JSONField(name = "x-amz-copy-source-if-match")
    private String copySourceIfMatch;

    /**
     * 确保只有当源对象（即你要从中复制的对象）的ETag与提供的值不匹配时，复制操作才会进行。
     * 通常用于防止在源对象内容未发生变化时执行不必要的复制操作
     */
    @JSONField(name = "x-amz-copy-source-if-none-match")
    private String copySourceIfNoneMatch;

    /**
     * 元数据指令
     * 这个请求头有两个可选值：COPY、REPLACE
     * 当设置为COPY时，表示将源对象的元数据复制到目标对象，并保持不变
     * 当设置为REPLACE时，表示用请求中提供的元数据替换目标对象的现有元数据
     */
    @JSONField(name = "x-amz-metadata-directive")
    private String metadataDirective;

    /**
     * 指定复制操作的源对象，源对象 最大可达 5 GB
     * 源存储桶的名称 和源对象的键，用斜杠 （/） 分隔
     *
     * @return 源对象地址
     */
    @JSONField(name = "x-amz-copy-source")
    public String copySource() {
        String copySource = ConstantConfig.SpecialSymbols.SLASH + AwsSignerUtil.signerUrlEncode(sourceBucket);

        // 源对象键
        if (StringUtils.isNotBlank(sourceKey)) {
            if (!sourceKey.startsWith(ConstantConfig.SpecialSymbols.SLASH)) {
                copySource += ConstantConfig.SpecialSymbols.SLASH;
            }
            copySource += AwsSignerUtil.signerUrlEncode(sourceKey);
        }

        // 源对象版本id
        if (StringUtils.isNotBlank(sourceVersionId)) {
            copySource += ConstantConfig.SpecialSymbols.QUESTION_MARK
                    + ConstantConfig.AwsSigner.CopyObject.VERSION_ID + ConstantConfig.SpecialSymbols.EQUALS + sourceVersionId;
        }
        // 返回源对象地址
        return copySource;
    }
}
