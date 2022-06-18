package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>构建字段排序实体对象</p>
 * <p>2022-06-18 03:16</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderByBuilder {

    /**
     * 需要排序的字段
     */
    @NotBlank(message = "不支持的排序")
    @Size(max = 20, message = "不支持的排序")
    private String orderBy;

    /**
     * 需要排序的方向(DESC、ASC)
     */
    @NotBlank(message = "不支持的排序")
    @Size(max = 4, message = "不支持的排序")
    private String orderDirection;

}
