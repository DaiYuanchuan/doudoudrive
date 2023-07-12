package com.doudoudrive.file.model.dto.response;

import com.doudoudrive.common.model.dto.model.FileShareModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>文件分享数据搜索响应数据模型</p>
 * <p>2023-01-04 05:39</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileShareSearchResponseDTO {

    /**
     * 文件分享信息内容
     */
    private List<FileShareModel> content;

    /**
     * 下一页的游标值
     */
    private String marker;

}
