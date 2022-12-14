package com.doudoudrive.common.util.lang;

import cn.hutool.core.util.IdUtil;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import com.doudoudrive.common.model.dto.model.SysLogMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * <p>集成压缩算法工具类单元测试</p>
 * <p>2022-11-18 14:31</p>
 *
 * @author Dan
 **/
@Slf4j
public class CompressionUtilTest {

    /**
     * 测试压缩算法
     */
    @Test
    public void compressTest() {
        // 字符串压缩
        String str = "Hello World";
        // 压缩
        byte[] bytes = CompressionUtil.compress(str.getBytes());
        // 解压后对比
        Assert.isTrue(str.equals(new String(CompressionUtil.decompressBytes(bytes))), "解压缩失败");

        // 大字符串压缩测试
        StringBuilder sb = new StringBuilder();
        sb.append("Hello World".repeat(1000));
        // 压缩
        byte[] bytes1 = CompressionUtil.compress(sb.toString().getBytes());
        // 解压后对比
        Assert.isTrue(sb.toString().equals(new String(CompressionUtil.decompressBytes(bytes1))), "解压缩失败");

        // 对象压缩测试
        ProtostuffUtil<SysLogMessage> protostuffUtil = new ProtostuffUtil<>();
        SysLogMessage sysLogMessage = SysLogMessage.builder()
                .businessId(SequenceUtil.nextId(SequenceModuleEnum.SYS_LOGBACK))
                .tracerId(IdUtil.fastSimpleUUID())
                .spanId(IdUtil.fastSimpleUUID())
                .content(IdUtil.fastSimpleUUID())
                .level(IdUtil.fastSimpleUUID())
                .appName(IdUtil.fastSimpleUUID())
                .currIp(IdUtil.fastSimpleUUID())
                .className(IdUtil.fastSimpleUUID())
                .methodName(IdUtil.fastSimpleUUID())
                .threadName(IdUtil.fastSimpleUUID())
                .timestamp(new Date())
                .build();
        // 压缩(先序列化再压缩)
        byte[] bytes2 = CompressionUtil.compress(protostuffUtil.serialize(sysLogMessage));
        // 解压后对比(先解压再序列化)
        Assert.isTrue(sysLogMessage.equals(protostuffUtil.deserialize(CompressionUtil.decompressBytes(bytes2), SysLogMessage.class)), "解压缩失败");
    }
}
