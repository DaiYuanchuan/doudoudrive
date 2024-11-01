package com.doudoudrive.common.model.dto.model.minio;

import com.doudoudrive.common.constant.ConstantConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>API_PutObject: 上传一个小文件到存储桶响应结果</p>
 * <p>2024-04-28 10:55</p>
 *
 * @author Dan
 * @see <a
 * href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/API/API_PutObject.html">PutObject</a>
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutObjectResult {

    /**
     * 如果存储桶启用版本控制，会自动生成唯一的版本 ID
     */
    private String versionId;

    /**
     * 上传对象的实体标记
     */
    private String etag;

    /**
     * 获取etag，去除双引号
     */
    public String getEtag() {
        if (StringUtils.isBlank(etag)) {
            return StringUtils.EMPTY;
        }
        return etag.replaceAll(ConstantConfig.SpecialSymbols.DOUBLE_QUOTATION_MARKS, StringUtils.EMPTY);
    }
}
