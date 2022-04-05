package com.doudoudrive.commonservice.config;

import com.doudoudrive.common.model.dto.model.DynamicDataSourceProperties;
import com.doudoudrive.common.util.lang.SpringBeanFactoryUtils;
import com.doudoudrive.commonservice.constant.DataSourceEnum;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>服务层数据源配置</p>
 * <p>2022-03-02 23:39</p>
 *
 * @author Dan
 **/
@Slf4j
@Configuration
@Import({SqlPrintInterceptor.class})
@MapperScan(basePackages = {"com.doudoudrive.commonservice.dao"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class DataSourceConfiguration implements Closeable {

    private SqlPrintInterceptor sqlPrintInterceptor;

    @Autowired
    public void setSqlPrintInterceptor(SqlPrintInterceptor sqlPrintInterceptor) {
        this.sqlPrintInterceptor = sqlPrintInterceptor;
    }

    /**
     * 获取配置类中所有的数据源相关配置
     * 获取到的第一个数据源为默认数据源配置
     *
     * @return 动态数据源相关配置参数属性集合
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public List<DynamicDataSourceProperties> getDynamicDataSourceProperties() {
        return new ArrayList<>();
    }

    /**
     * HikariCP 数据源配置对象Map
     */
    private final Map<String, HikariDataSource> HIKARI_DATA_SOURCE = Maps.newLinkedHashMapWithExpectedSize(2);

    /**
     * 动态数据来源.
     *
     * @return the data source
     */
    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        // 获取数据源对象
        Map<String, HikariDataSource> hikariDataSourceMap = getHikariDataSourceConfig();

        // 转换数据源对象类型
        Map<Object, Object> targetDataSources = Maps.newLinkedHashMapWithExpectedSize(hikariDataSourceMap.size());
        targetDataSources.putAll(hikariDataSourceMap);

        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        // 将第一个数据源作为默认数据源
        dynamicRoutingDataSource.setDefaultTargetDataSource(hikariDataSourceMap.get(DataSourceEnum.DEFAULT.dataSource));
        // 其余数据源作为指定的数据源
        dynamicRoutingDataSource.setTargetDataSources(targetDataSources);
        dynamicRoutingDataSource.afterPropertiesSet();
        return dynamicRoutingDataSource;
    }

    /**
     * 获取 sqlSessionTemplate
     *
     * @return sqlSessionTemplate
     */
    @Bean(name = "sqlSessionTemplate")
    public MySqlSessionTemplate sqlSessionTemplate() {
        // 获取数据源对象
        Map<String, HikariDataSource> hikariDataSourceMap = getHikariDataSourceConfig();

        // 一个存储sql会话工厂的Map
        Map<String, SqlSessionFactory> targetSqlSessionFactories = Maps.newLinkedHashMapWithExpectedSize(hikariDataSourceMap.size());
        hikariDataSourceMap.forEach((key, value) -> targetSqlSessionFactories.put(key, createSqlSessionFactory(value)));

        MySqlSessionTemplate sqlSessionTemplate = new MySqlSessionTemplate(createSqlSessionFactory(hikariDataSourceMap.get(DataSourceEnum.DEFAULT.dataSource)));
        sqlSessionTemplate.setTargetSqlSessionFactories(targetSqlSessionFactories);
        return sqlSessionTemplate;
    }

    /**
     * 配置事务管理器
     * 这里的事务只实现单库的本地事务，跨库事务可使用分布式事务如:Atomikos、Seata等
     * 如果使用 Atomikos 需要将 Hikari 数据源替换为 AtomikosNonXADataSourceBean
     *
     * @return 平台事务管理程序
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        // 获取数据源对象
        Map<String, HikariDataSource> hikariDataSourceMap = getHikariDataSourceConfig();
        hikariDataSourceMap.forEach((key, value) -> {
            // 不获取默认数据源
            if (!DataSourceEnum.DEFAULT.dataSource.equals(key)) {
                // 配置事务管理器
                PlatformTransactionManager transactionManager = new DataSourceTransactionManager(value);
                // 向spring中动态的注册Bean
                SpringBeanFactoryUtils.registerBean(DataSourceEnum.getTransactionValue(key), transactionManager);
            }
        });
        // 响应一个默认数据源
        return new DataSourceTransactionManager(hikariDataSourceMap.get(DataSourceEnum.DEFAULT.dataSource));
    }

    /**
     * 关闭数据源时调用，依次关闭所有数据源
     */
    @Override
    public void close() {
        log.warn("prepare to shutdown all data sources");
        // 循环关闭所有的数据源
        HIKARI_DATA_SOURCE.forEach((key, value) -> value.close());
    }

    // ============================================== private ==========================================================

    /**
     * 创建一个SqlSessionFactory
     *
     * @param hikariDataSource HikariCP 数据源对象
     * @return 一个 SqlSessionFactory
     */
    @SneakyThrows
    private SqlSessionFactory createSqlSessionFactory(HikariDataSource hikariDataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 设置SQL语句拦截器
        sqlSessionFactoryBean.setPlugins(sqlPrintInterceptor);

        sqlSessionFactoryBean.setDataSource(hikariDataSource);

        // 框架相关的Mapper.xmL 资源文件，地址固定
        Resource[] bitsResources = new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*Mapper.xml");
        sqlSessionFactoryBean.setMapperLocations(bitsResources);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        sqlSessionFactoryBean.afterPropertiesSet();

        return sqlSessionFactoryBean.getObject();
    }


    /**
     * 获取 HikariCP 数据源相关配置项
     *
     * @return HikariCP 数据源配置对象Map<数据源标识, 数据源对象>
     */
    private Map<String, HikariDataSource> getHikariDataSourceConfig() {
        List<DynamicDataSourceProperties> dynamicDataSourcePropertiesList = getDynamicDataSourceProperties();
        if (CollectionUtils.isEmpty(dynamicDataSourcePropertiesList)) {
            throw new IllegalArgumentException("No data source configuration entry found ! ! !");
        }

        // 查看默认数据源配置是否存在
        if (HIKARI_DATA_SOURCE.get(DataSourceEnum.DEFAULT.dataSource) == null) {
            // 初始化默认数据源
            HIKARI_DATA_SOURCE.put(DataSourceEnum.DEFAULT.dataSource, getDefaultTargetDataSource());
        }

        // 构建 HikariDataSource 数据源配置
        for (DynamicDataSourceProperties dataSourceProperties : dynamicDataSourcePropertiesList) {
            if (HIKARI_DATA_SOURCE.get(dataSourceProperties.getKey()) == null) {
                log.warn("initializing the data source:{}", dataSourceProperties.getKey());
                HIKARI_DATA_SOURCE.put(dataSourceProperties.getKey(), getHikariDataSource(dataSourceProperties));
            }
        }

        return HIKARI_DATA_SOURCE;
    }

    /**
     * 获取一个 HikariCP 数据源
     *
     * @param dataSourceProperties 动态数据源相关配置参数属性对象
     * @return 一个 HikariCP 数据源
     */
    private HikariDataSource getHikariDataSource(DynamicDataSourceProperties dataSourceProperties) {
        HikariDataSource hikariDataSource = new HikariDataSource();

        // 设置数据库连接信息
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariDataSource.setJdbcUrl(dataSourceProperties.getUrl());
        hikariDataSource.setUsername(dataSourceProperties.getUsername());
        hikariDataSource.setPassword(dataSourceProperties.getPassword());

        // 最小空闲连接，默认值10，小于0或大于maximum-pool-size，都会重置为maximum-pool-size
        hikariDataSource.setMinimumIdle(5);
        // 连接池的最大连接数，小于等于0会被重置为默认值10；大于零小于1会被重置为minimum-idle的值
        hikariDataSource.setMaximumPoolSize(50);
        // 自动提交从池中返回的连接
        hikariDataSource.setAutoCommit(Boolean.TRUE);
        // 空闲连接超时时间，默认值600000（10分钟）
        hikariDataSource.setIdleTimeout(30000);
        // 连接池的用户定义名称，主要出现在日志记录和JMX管理控制台中以识别池和池配置
        hikariDataSource.setPoolName("HikariCP-" + dataSourceProperties.getKey());
        // 连接最大存活时间，不等于0且小于30秒，会被重置为默认值30分钟.设置应该比mysql设置的超时时间短
        hikariDataSource.setMaxLifetime(1800000);
        // 连接超时时间：毫秒，默认值30秒
        hikariDataSource.setConnectionTimeout(30000);
        // 用于测试连接是否可用的查询语句
        hikariDataSource.setConnectionTestQuery("SELECT 1");

        return hikariDataSource;
    }

    /**
     * 获取默认数据源配置
     * 将获取到的第一个数据源为默认数据源配置
     *
     * @return 一个默认 HikariCP 数据源配置
     */
    private HikariDataSource getDefaultTargetDataSource() {
        List<DynamicDataSourceProperties> dynamicDataSourcePropertiesList = getDynamicDataSourceProperties();
        return getHikariDataSource(dynamicDataSourcePropertiesList.get(0));
    }

}
