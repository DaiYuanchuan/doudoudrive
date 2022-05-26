package com.doudoudrive.file.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>创建文件时请求数据模型</p>
 * <p>2022-05-22 21:27</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateFileRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 参数加密后的签名字符串
     */
    private String sign;

}
