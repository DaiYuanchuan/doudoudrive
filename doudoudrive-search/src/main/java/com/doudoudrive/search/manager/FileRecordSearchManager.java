package com.doudoudrive.search.manager;

import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.search.model.elasticsearch.FileRecordDTO;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;

import java.util.List;

/**
 * <p>文件临时操作记录信息搜索服务的通用业务处理层接口</p>
 * <p>2023-07-27 14:47</p>
 *
 * @author Dan
 **/
public interface FileRecordSearchManager {

    /**
     * <p>批量保存文件临时操作记录信息</p>
     *
     * @param fileRecordDTO 文件临时操作记录信息ES数据模型
     */
    void saveFileRecord(List<FileRecordDTO> fileRecordDTO);

    /**
     * 删除指定状态的文件操作记录数据
     *
     * @param userId     指定用户
     * @param action     动作，参见{@link ConstantConfig.FileRecordAction.ActionEnum}
     * @param actionType 动作类型，参见{@link ConstantConfig.FileRecordAction.ActionTypeEnum}
     * @param etag       文件唯一标识
     * @return 删除的文件临时操作记录信息
     */
    ByQueryResponse deleteAction(String userId, String action, String actionType, List<String> etag);

    /**
     * 获取指定状态的文件操作记录数据
     *
     * @param userId     指定用户
     * @param action     动作，参见{@link ConstantConfig.FileRecordAction.ActionEnum}
     * @param actionType 动作类型，参见{@link ConstantConfig.FileRecordAction.ActionTypeEnum}
     * @param etag       文件唯一标识
     * @return 返回指定状态的文件操作记录数据
     */
    SearchHits<FileRecordDTO> fileRecordSearch(String userId, String action, String actionType, String etag);

}
