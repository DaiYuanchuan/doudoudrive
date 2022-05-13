package com.doudoudrive.common.model.dto.request;

import com.doudoudrive.common.model.dto.model.SmsSendRecordModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * <p>邮件发送请求数据模型</p>
 * <p>2022-04-15 22:29</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMailRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 与数据标识对应的自定义参数配置
     */
    private Map<String, Object> model;

    /**
     * SMS发送记录BO模型
     */
    private SmsSendRecordModel sendRecordModel;

}
