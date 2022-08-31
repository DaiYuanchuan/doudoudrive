package com.doudoudrive.task.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>删除云端文件的定时任务（Bean模式）</p>
 * <p>2022-08-30 19:30</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class DeleteFileJob {

    /**
     * 查找 oss_file 表中存在，但是 disk_file 表中不存在的文件信息
     * 配置定时任务 ，每月1号凌晨1点执行(cron = 0 0 1 1 * ?)删除云端文件
     */
    @XxlJob(value = "deleteFileJobHandler")
    public void deleteFileJobHandler() {
        log.info("删除云端文件的定时任务（Bean模式）");
        XxlJobHelper.log("java, Hello World~~~ 删除云端文件的定时任务（Bean模式）--");
    }
}
