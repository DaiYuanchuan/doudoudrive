package com.doudoudrive.common.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;

/**
 * <p>创建MQ消费记录信息的请求数据模型</p>
 * <p>2023-07-25 18:32</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateMqConsumerRecordRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5190126912903387380L;

    /**
     * Redis延迟队列枚举信息
     */
    @NotBlank(message = "队列枚举为空")
    private String delayedQueue;

    /**
     * 到期的元素内容
     */
    @NotBlank(message = "消息内容为空")
    private String element;

}
