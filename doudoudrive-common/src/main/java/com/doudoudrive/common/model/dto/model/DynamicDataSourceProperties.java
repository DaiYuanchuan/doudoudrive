package com.doudoudrive.common.model.dto.model;

import lombok.*;

/**
 * <p>动态数据源相关配置参数属性</p>
 * <p>2022-03-03 17:38</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicDataSourceProperties {

    /**
     * 数据源标识
     */
    private String key;

    /**
     * 数据源url地址
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
