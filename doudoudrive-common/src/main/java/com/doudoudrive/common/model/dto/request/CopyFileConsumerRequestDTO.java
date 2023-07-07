package com.doudoudrive.common.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>批量复制文件信息时的消费者请求数据模型</p>
 * <p>2022-12-16 10:30</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CopyFileConsumerRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 目标文件夹所属的用户标识
     */
    private String targetUserId;

    /**
     * 需要进行复制的文件所属的用户标识
     */
    private String fromUserId;

    /**
     * 目标文件夹的业务标识
     */
    private String targetFolderId;

    /**
     * 用来保存树形结构的Map<原有的数据标识, 新数据返回的数据标识>
     */
    private Map<String, String> treeStructureMap;

    /**
     * 指定需要进行复制的文件信息
     */
    private List<String> preCopyFileList;

}
