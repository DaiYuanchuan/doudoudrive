package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

/**
 * <p>框架服务缓存信息刷新时使用的数据模型</p>
 * <p>2022-04-22 19:29</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheRefreshModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 需要从本地缓存中删除的缓存key值
     */
    private String cacheKey;

    /**
     * 是否删除全部本地缓存数据
     */
    private Boolean clear;

    /**
     * 获取是否删除全部本地缓存数据的标识，默认为false
     *
     * @return 是否删除全部本地缓存数据标识
     */
    public Boolean getClear() {
        return Optional.ofNullable(clear).orElse(Boolean.FALSE);
    }
}
