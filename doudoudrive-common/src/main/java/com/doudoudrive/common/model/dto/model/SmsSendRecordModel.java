package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>SMS发送记录BO模型</p>
 * <p>2022-04-15 18:43</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsSendRecordModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 入表时的后缀参数
     */
    private String tableSuffix;

    /**
     * 业务标识
     */
    private String businessId;

}
