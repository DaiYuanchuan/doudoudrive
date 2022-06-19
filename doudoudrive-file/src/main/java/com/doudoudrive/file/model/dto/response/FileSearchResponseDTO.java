package com.doudoudrive.file.model.dto.response;

import com.doudoudrive.common.model.dto.model.DiskFileModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>文件搜索响应数据模型</p>
 * <p>2022-06-06 18:30</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileSearchResponseDTO {

    /**
     * 搜索结果
     */
    private List<DiskFileModel> content;

    /**
     * 下一页的游标值
     */
    private String marker;

}
