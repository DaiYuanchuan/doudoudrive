package com.doudoudrive.commonservice.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * <p>动态数据源路由配置</p>
 * <p>2022-03-03 22:49</p>
 *
 * @author Dan
 **/
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * 确定当前查找的数据源
     * 在访问数据库时会调用此方法，用来获取当前数据库实例的 key
     *
     * @return 返回当前线程的数据源的key
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.peek();
    }
}
