package com.doudoudrive.common.annotation;

import java.lang.annotation.*;

/**
 * <p>自定义本地锁的注解</p>
 * <p>2021-03-07 23:21</p>
 *
 * @author Dan
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LocalLock {
    /**
     * 本地锁的key值
     */
    String key() default "";
}