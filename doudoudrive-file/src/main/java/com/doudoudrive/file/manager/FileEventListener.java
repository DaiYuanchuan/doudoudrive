package com.doudoudrive.file.manager;

import com.doudoudrive.common.model.pojo.DiskFile;
import com.doudoudrive.file.model.dto.request.CreateFileConsumerRequestDTO;

/**
 * <p>文件变更事件监听</p>
 * <p>2023-03-30 15:31</p>
 *
 * @author Dan
 **/
public interface FileEventListener {

    /**
     * 创建文件
     *
     * @param consumerRequest 创建文件时的消费者请求数据模型
     */
    void create(CreateFileConsumerRequestDTO consumerRequest);

    /**
     * 删除文件
     *
     * @param file 文件信息
     */
    void delete(DiskFile file);

}
