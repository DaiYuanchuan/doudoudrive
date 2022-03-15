package com.doudoudrive.common.log;

import com.doudoudrive.common.model.dto.model.OpLogInfo;

/**
 * <p>操作日志处理完成后的回调接口，通过实现此接口获取日志处理结果</p>
 * <p>2022-03-14 23:05</p>
 *
 * @author Dan
 **/
public interface OpLogCompletionHandler {

    /**
     * 操作日志信息处理完成后自动回调该接口
     * 此接口主要用于用户将日志信息存入MySQL、Redis等等
     *
     * @param opLogInfo 处理完成后的 操作日志实体信息
     */
    void complete(OpLogInfo opLogInfo);

}
