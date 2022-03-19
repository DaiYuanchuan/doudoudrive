package com.doudoudrive.common.util.lang;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.doudoudrive.common.constant.SequenceModuleEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>序列工具、用于获取表中唯一主键id</p>
 *
 * <p>2022-01-13 16:57</p>
 *
 * @author Dan
 **/
public class SequenceUtil {

    /**
     * 生成的序列id最大长度
     */
    private static final int SEQUENCE_ID_MAX_LENGTH = 35;

    /**
     * 默认需要生成的随机数位数
     */
    private static final int DEFAULT_RANDOM_NUMBER_LENGTH = 6;

    /**
     * 生成序列化时使用的时间戳格式
     */
    private static final String PURE_DATETIME_PATTERN = "yyMMddHHmmss";

    /**
     * 时间戳格式化
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(PURE_DATETIME_PATTERN);

    /**
     * 每个实例都随机生成两位数的终端ID
     */
    private static final String WORKER_ID = RandomUtil.randomNumbers(2);

    /**
     * 上一次的时间戳
     */
    private static final AtomicLong LAST_TIMESTAMP = new AtomicLong(Instant.now().toEpochMilli());

    private SequenceUtil() {
    }

    /**
     * 获取下一个序列id
     *
     * @param businessCode 代表业务模块的标识，用来区分不同的业务模块(两位数字)
     * @return 生成一个不重复的序列id
     */
    private String getNextSequenceId(SequenceModuleEnum businessCode) {
        // 获取到当前时间戳
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        // 当前时间毫秒数
        long epochMilli = Instant.now().toEpochMilli();
        if (epochMilli > LAST_TIMESTAMP.get()) {
            LAST_TIMESTAMP.set(epochMilli);
        }

        // 生成序列id
        String sequenceId = timestamp
                + LAST_TIMESTAMP.incrementAndGet()
                + businessCode.code
                + WORKER_ID
                + RandomUtil.randomNumbers(DEFAULT_RANDOM_NUMBER_LENGTH);
        if (sequenceId.length() > SEQUENCE_ID_MAX_LENGTH) {
            // 字符串之间的差值(求出超过指定数值的多少)
            int differenceValue = sequenceId.length() - SEQUENCE_ID_MAX_LENGTH;
            // 将原字符串从末尾开始 共截取 $differenceValue 位
            return StrUtil.reverse(StrUtil.reverse(sequenceId).substring(differenceValue));
        }
        return sequenceId;
    }

    private static class InstanceHolder {
        private static final SequenceUtil INSTANCE = new SequenceUtil();
    }

    /**
     * 获取下一个序列id
     *
     * @param businessCode 代表业务模块的标识，用来区分不同的业务模块(两位数字)
     * @return 一个序列id
     */
    public static String nextId(SequenceModuleEnum businessCode) {
        return InstanceHolder.INSTANCE.getNextSequenceId(businessCode);
    }

    /**
     * 对指定的序列id进行取余运算
     *
     * @param sequenceId 序列id
     * @param remainder  余数
     * @return 取余字符串形式的运算结果
     */
    public static BigDecimal complement(String sequenceId, Integer remainder) {
        return new BigDecimal(sequenceId).divideAndRemainder(BigDecimal.valueOf(remainder))[1];
    }

    /**
     * 获取表名后缀
     * (序列id % 余数) + 1，对结果补零
     *
     * @param sequenceId 序列id
     * @param remainder  余数
     * @return 表名称后缀
     */
    public static String tableSuffix(String sequenceId, Integer remainder) {
        // 先进行取余运算
        BigDecimal complement = complement(sequenceId, remainder);

        // 对取余结果 +1 后补零
        return String.format("%02d", complement.add(BigDecimal.ONE).longValue());
    }
}