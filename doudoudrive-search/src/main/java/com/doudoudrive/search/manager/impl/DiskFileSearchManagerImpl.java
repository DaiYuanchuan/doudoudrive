package com.doudoudrive.search.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import com.doudoudrive.common.constant.ConstantConfig;
import com.doudoudrive.search.manager.DiskFileSearchManager;
import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>用户文件信息搜索服务的通用业务处理层接口实现</p>
 * <p>2022-05-22 14:15</p>
 *
 * @author Dan
 **/
@Service("diskFileSearchManager")
public class DiskFileSearchManagerImpl implements DiskFileSearchManager {

    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 保存用户文件信息，es中保存用户文件信息
     * <pre>
     *     先入库，然后再往es里存用户文件信息
     * </pre>
     *
     * @param diskFileDTO 用户文件实体信息ES数据模型
     */
    @Override
    public void saveDiskFile(DiskFileDTO diskFileDTO) {
        // 构建保存请求
        restTemplate.save(diskFileDTO);
    }

    /**
     * 根据文件业务标识删除指定文件信息
     * <pre>
     *     先删库，然后再删es里存的用户文件信息
     * </pre>
     *
     * @param id 文件业务标识
     */
    @Override
    public void deleteDiskFile(String id) {
        restTemplate.delete(id, DiskFileDTO.class);
    }

    /**
     * 根据文件业务标识去更新用户文件信息
     *
     * @param id          用户系统内唯一标识
     * @param diskFileDTO 用户文件实体信息ES数据模型
     */
    @Override
    public void updateDiskFile(String id, DiskFileDTO diskFileDTO) {
        // 将用户文件信息转为map
        Map<String, Object> diskFileMap = BeanUtil.beanToMap(diskFileDTO, false, true);
        // 构建更新的es请求
        restTemplate.update(UpdateQuery
                .builder(id)
                .withDocument(Document.from(diskFileMap))
                .build(), IndexCoordinates.of(ConstantConfig.IndexName.DISK_FILE));
    }
}
