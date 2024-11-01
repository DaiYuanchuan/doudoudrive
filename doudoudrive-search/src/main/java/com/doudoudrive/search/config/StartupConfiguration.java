package com.doudoudrive.search.config;

import com.doudoudrive.search.model.elasticsearch.DiskFileDTO;
import com.doudoudrive.search.model.elasticsearch.FileRecordDTO;
import com.doudoudrive.search.model.elasticsearch.FileShareDTO;
import com.doudoudrive.search.model.elasticsearch.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>SpringBoot启动时自动执行ES索引库的检查</p>
 * <p>索引库不存在，创建索引库，以及映射关系</p>
 * <p>2022-03-20 21:51</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class StartupConfiguration implements CommandLineRunner {

    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(ElasticsearchRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 需要进行初始化的class类集合列表
     */
    private static final List<Class<?>> CLASS_INIT_LIST = new ArrayList<>();

    static {
        CLASS_INIT_LIST.add(UserInfoDTO.class);
        CLASS_INIT_LIST.add(DiskFileDTO.class);
        CLASS_INIT_LIST.add(FileShareDTO.class);
        CLASS_INIT_LIST.add(FileRecordDTO.class);
    }

    @Override
    public void run(String... args) {
        for (Class<?> clazz : CLASS_INIT_LIST) {
            IndexOperations indexOperations = restTemplate.indexOps(clazz);
            // 判断索引是否存在
            if (!indexOperations.exists()) {
                // 创建索引
                indexOperations.create();
                // 为该索引操作绑定到的实体创建索引映射
                indexOperations.createMapping();
                // 将映射写入此IndexOperations绑定到的类的索引
                indexOperations.putMapping();
            }
        }
    }
}
