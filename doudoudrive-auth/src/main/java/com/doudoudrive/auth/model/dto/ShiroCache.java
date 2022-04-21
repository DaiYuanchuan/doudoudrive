package com.doudoudrive.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>shiro鉴权框架服务的缓存对象</p>
 * <p>2022-04-20 21:58</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiroCache implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 临时缓存对象
     */
    private Object cache;

    /**
     * 临时缓存对象保存到Jvm缓存中的时间戳，这里是缓存创建的时间戳
     */
    private Long createTime;

}
