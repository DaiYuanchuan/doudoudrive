package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.model.dto.model.DiskFileModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>删除文件内部调用时的请求数据模型</p>
 * <p>2022-08-10 18:43</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteFileInternalRequestDTO {

    /**
     * 需要删除的文件列表信息
     */
    private List<DiskFileModel> content;

    /**
     * 用户系统内唯一标识
     */
    private String userId;
}
