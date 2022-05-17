package com.doudoudrive.common.util.lang;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ConcurrencyTester;
import cn.hutool.core.thread.ThreadUtil;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>序列工具单元测试用例</p>
 * <p>2022-03-06 22:09</p>
 *
 * @author Dan
 **/
@Slf4j
public class SequenceUtilTest {

    /**
     * 获取下一个序列id测试
     */
    @Test
    public void nextIdTest() {
        // 获取一个序列id
        log.info(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER));
    }

    /**
     * 序列id取余运算
     */
    @Test
    public void complementTest() {
        String sequenceId = SequenceUtil.nextId(SequenceModuleEnum.DISK_USER);
        Integer remainder = 10;
        log.info("sequenceId:{} % {} = {}", sequenceId, remainder, SequenceUtil.complement(sequenceId, remainder).toPlainString());
    }

    @Test
    public void tableSuffixTest() {
        String sequenceId = SequenceUtil.nextId(SequenceModuleEnum.DISK_USER);
        Integer remainder = 10;
        log.info("sequenceId:{} % {} = {} suffix: {}", sequenceId, remainder, SequenceUtil.complement(sequenceId, remainder).toPlainString(), SequenceUtil.tableSuffix(sequenceId, remainder));
    }

    @Test
    public void asciiSuffixTest(){
        String sequenceId = "lq8AjBQSRIqkU_VGaQD2WQbtbvUla";
        Integer remainder = 10;
        log.info("sequenceId:{} % {} suffix: {}", sequenceId, remainder, SequenceUtil.asciiSuffix(sequenceId, remainder));
    }

    /**
     * 序列id重复性测试
     */
    @Test
    public void distinctTest() {
        // 生成10000个id测试是否重复
        Set<String> set = new HashSet<>();
        TimeInterval timer = DateUtil.timer();
        for (int i = 0; i < 20000; i++) {
            set.add(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER));
        }
        log.info("{}ms", timer.interval());
        if (!String.valueOf(20000).equals(String.valueOf(set.size()))) {
            throw new AssertionError("序列id重复");
        }
    }

    /**
     * 序列id重复性测试(并发场景下)
     */
    @Test
    public void distinctThreadTest() {
        Set<String> ids = new ConcurrentHashSet<>();
        ConcurrencyTester tester = ThreadUtil.concurrencyTest(500, () -> {
            for (int i = 0; i < 5000; i++) {
                if (!ids.add(SequenceUtil.nextId(SequenceModuleEnum.DISK_USER))) {
                    log.info("存在重复ID！");
                }
            }
        });
        log.info("{}ms", tester.getInterval());
    }
}
