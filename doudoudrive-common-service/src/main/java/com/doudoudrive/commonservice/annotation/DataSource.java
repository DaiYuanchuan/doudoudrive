package com.doudoudrive.commonservice.annotation;

import com.doudoudrive.commonservice.constant.DataSourceEnum;

import java.lang.annotation.*;

/**
 * <p>动态切换数据注解，用于动态切换数据源</p>
 * <p>遵循方法 > 类 ，Mapper 层 > Service 层</p>
 *
 * <p>2022-03-03 18:16</p>
 *
 * @author Dan
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface DataSource {

    /**
     * 定义要切换的数据源标识
     *
     * @return 要切换的数据源标识
     */
    DataSourceEnum value() default DataSourceEnum.DEFAULT;

}
