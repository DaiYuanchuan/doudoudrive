package com.doudoudrive.common.model.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>客户端-服务端彼此传输的数据的数据体</p>
 * <p>2022-11-11 16:35</p>
 *
 * @author Dan
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TracerLogbackModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 多个系统日志批量打包后批量发送
     */
    private List<SysLogMessage> logMessageList;

}
