package com.doudoudrive.common.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>删除文件时的消费者请求数据模型</p>
 * <p>2022-08-26 15:08</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteFileConsumerRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 指定需要删除的文件所属的用户id
     */
    private String userId;

    /**
     * 指定需要删除的文件的业务id(这里应该是文件夹的业务id)
     */
    private List<String> businessId;

}
