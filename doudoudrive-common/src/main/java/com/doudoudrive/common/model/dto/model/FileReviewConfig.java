package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>文件审核配置</p>
 * <p>AI智能鉴黄配置</p>
 * <p>2022-05-26 11:28</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileReviewConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 可以参加内容审核的视频类型文件mime type
     */
    private List<String> videoTypes;

    /**
     * 可以参加内容审核的图片类型文件mime type
     */
    private List<String> imageTypes;

    /**
     * 内容审核请求超时时间(单位:毫秒)
     */
    private Integer requestTimeout;

}
