package com.doudoudrive.task.job;

import com.doudoudrive.commonservice.service.FileShareService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>文件分享操作相关定时任务处理程序（Bean模式）</p>
 * <p>2023-01-07 22:08</p>
 *
 * @author Dan
 **/
@Slf4j
@Component
public class FileShareJobHandler {

    private FileShareService fileShareService;

    @Autowired
    public void setFileShareService(FileShareService fileShareService) {
        this.fileShareService = fileShareService;
    }

    /**
     * 每天更新所有过期的分享链接，将分享链接状态改为已过期
     * 配置定时任务 ，每秒执行(cron = 0 0 0 * * ?)
     *
     * @return 返回公共处理状态
     */
    @XxlJob(value = "updateExpiredShareJobHandler")
    public ReturnT<String> updateExpiredShareJobHandler() {
        // 打印执行日志
        XxlJobHelper.log("updateExpiredShareJobHandler start...");
        fileShareService.updateExpiredShare();
        XxlJobHelper.log("updateExpiredShareJobHandler end...");
        return ReturnT.SUCCESS;
    }
}
