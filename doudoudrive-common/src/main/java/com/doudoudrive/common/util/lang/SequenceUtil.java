package com.doudoudrive.common.util.lang;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.doudoudrive.common.constant.NumberConstant;
import com.doudoudrive.common.constant.SequenceModuleEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

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

    /**
     * 两位数补零填充
     */
    private static final String DOUBLE_DIGIT_ZERO_FILLING = "%02d";

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
                + businessCode.getCode()
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
        return complement(new BigDecimal(sequenceId), remainder);
    }

    /**
     * 对指定的序列id进行取余运算
     *
     * @param sequenceId 序列id
     * @param remainder  余数
     * @return 取余字符串形式的运算结果
     */
    public static BigDecimal complement(BigDecimal sequenceId, Integer remainder) {
        return sequenceId.divideAndRemainder(BigDecimal.valueOf(remainder))[1];
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
        return String.format(DOUBLE_DIGIT_ZERO_FILLING, complement.add(BigDecimal.ONE).longValue());
    }

    /**
     * 获取序列生成时间月份后缀
     *
     * @param sequenceId 序列id
     * @return 时间月份后缀
     */
    public static String generateTimeSuffix(String sequenceId) {
        // 获取序列生成时间
        LocalDateTime generateTime = SequenceUtil.generateTime(sequenceId);
        if (generateTime == null) {
            return StringUtils.EMPTY;
        }

        // 获取表后缀
        return generateTime.format(DatePattern.SIMPLE_MONTH_FORMATTER);
    }

    /**
     * 依照序列id的组成规则，获取序列中的时间信息，序列id的组成规则如下：
     * <pre>
     *     230327182923   1679912979502   11               95               040071
     *     yyMMddHHmmss + 13位自增时间戳 + 两位业务模块标识 + 两位随机终端ID + 6位随机数
     * </pre>
     *
     * @param sequenceId 序列id
     * @return 序列生成时间，失败返回 null
     */
    public static LocalDateTime generateTime(String sequenceId) {
        if (StringUtils.isBlank(sequenceId)) {
            return null;
        }
        // 需要截取的开始位置和结束位置
        final int begin = NumberConstant.INTEGER_ZERO;
        final int end = NumberConstant.INTEGER_TEN + NumberConstant.INTEGER_TWO;

        // 截取序列id中的时间信息
        String time = sequenceId.substring(begin, end);
        if (StringUtils.isBlank(time)) {
            return null;
        }

        try {
            // 将时间信息转换为 LocalDateTime
            return LocalDateTime.parse(time, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 指定分表数量，获取所有表后缀
     *
     * @param remainder 分表余数
     * @return 所有的表后缀
     */
    public static List<String> tableSuffixList(Integer remainder) {
        return IntStream.rangeClosed(NumberConstant.INTEGER_ONE, remainder).mapToObj(i -> String.format(DOUBLE_DIGIT_ZERO_FILLING, i)).toList();
    }

    /**
     * 获取表名后缀(支持字符串序列)
     * (对字符串序列id中每一个字符的 ASCII 码求和 % 余数) + 1，对结果补零
     *
     * @param sequenceId 字符串序列id
     * @param remainder  余数
     * @return 表名称后缀
     */
    public static String asciiSuffix(String sequenceId, Integer remainder) {
        // 对取余结果 +1 后补零
        return String.format(DOUBLE_DIGIT_ZERO_FILLING, PartitionByJumpConsistentHash.calculate(sequenceId, remainder) + NumberConstant.INTEGER_ONE);
    }
}
