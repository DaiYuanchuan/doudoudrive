package com.doudoudrive.task.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>文件操作相关定时任务处理程序（Bean模式）</p>
 * <p>2022-08-30 19:30</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class FileJobHandler {

    /**
     * 每秒查询文件删除任务待处理的记录，将任务记录状态改为处理中，然后执行删除操作
     * 配置定时任务 ，每秒执行(cron = * * * * * ?)
     */
    @XxlJob(value = "deleteFileJobHandler")
    public ReturnT<String> deleteFileJobHandler() {
        return ReturnT.SUCCESS;
    }
}
