package com.doudoudrive.common.util.date;

import cn.hutool.core.date.*;
import com.doudoudrive.common.constant.NumberConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>时间工具类</p>
 * <p>2022-03-08 10:05</p>
 *
 * @author Dan
 **/
@Slf4j
public class DateUtils {

    /**
     * 字符串常量
     */
    private static final String JUST = "刚刚";
    private static final String BEFORE = "前";
    private static final String MINUTE_AGO = "钟前";

    /**
     * 传入任意 顺序 的日期 , 对日期进行 开始日期 < 结束日期 的排序
     * 遵循 小的日期在前面[开始日期] 大的日期在后面[结束日期]
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @param format    日期格式化[如:yyyy-MM-dd HH:mm:ss.SSS]
     * @return 返回一个String数组, 数组的第一位为 最终的开始日期,数组的第二位为 最终的结束日期
     */
    public static String[] sortByDate(String startTime, String endTime, String format) {
        // 如果 结束日期 为空 则默认 结束日期 为现在时间
        if (StringUtils.isBlank(endTime)) {
            endTime = DateUtil.today();
        }

        // 如果 起始日期 为空 则默认 起始日期 为现在时间
        if (StringUtils.isBlank(startTime)) {
            startTime = DateUtil.today();
        }

        // 校验:日期格式
        boolean isStartTime = isTime(endTime, format);
        if (!isStartTime) {
            return null;
        }
        boolean isEndTime = isTime(endTime, format);
        if (!isEndTime) {
            return null;
        }

        // 开始日期的毫秒数
        long startTimeMillisecond = DateUtil.parse(startTime, format).getTime();

        // 结束日期的毫秒数
        long endTimeMillisecond = DateUtil.parse(endTime, format).getTime();

        // 调整时间日期 判断 开始日期的毫秒数 是否小于 结束日期的毫秒数
        if (startTimeMillisecond < endTimeMillisecond) {
            return new String[]{startTime, endTime};
        }
        // 开始日期大于结束日期 两个 日期调换下位置 保证 开始日期 小于 结束日期
        return new String[]{endTime, startTime};
    }

    /**
     * 传入任意 顺序 的日期 , 对日期进行 开始日期 < 结束日期 的排序
     * 遵循 小的日期在前面[开始日期] 大的日期在后面[结束日期]
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return 返回一个Date数组, 数组的第一位为 最终的开始日期,数组的第二位为 最终的结束日期
     */
    public static Date[] sortByDate(Date startTime, Date endTime) {
        // 如果 结束日期 为空 则默认 结束日期 为现在时间
        if (endTime == null) {
            endTime = DateUtil.date();
        }

        // 如果 起始日期 为空 则默认 起始日期 为现在时间
        if (startTime == null) {
            startTime = DateUtil.date();
        }

        // 调整时间日期 判断 开始日期的毫秒数 是否小于 结束日期的毫秒数
        if (startTime.getTime() < endTime.getTime()) {
            return new Date[]{startTime, endTime};
        }
        // 开始日期大于结束日期 两个 日期调换下位置 保证 开始日期 小于 结束日期
        return new Date[]{endTime, startTime};
    }

    /**
     * 验证字符串是否可以转成时间格式
     *
     * @param time       需要转换的时间字符串
     * @param dateFormat yyyy-MM-dd hh:mm:ss
     * @return 返回true:可以转换成时间格式 ，返回false:不可以
     */
    public static boolean isTime(String time, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，
            // 比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(time);
            return true;
        } catch (Exception e) {
            // 如果throw ParseException或者NullPointerException
            // 就说明格式不对
            return false;
        }
    }

    /**
     * 根据传入的时间与现在的时间差
     * 将时间差转成可读字符串 如:刚刚、3秒前、一小时前等等
     *
     * @param time 需要进行比较的时间对象
     * @return 返回可读字符串, 大于20天的 将会直接显示日期
     */
    public static String timeDifference(Date time) {
        if (time == null) {
            return StringUtils.EMPTY;
        }

        // 获取两个日期之间相差的毫秒数
        long timeDifference = DateUtil.between(DateUtil.date(), time, DateUnit.MS);

        // 1分钟之内
        if (timeDifference < DateUnit.MINUTE.getMillis()) {
            // 如果是5秒内
            int just = NumberConstant.INTEGER_FIVE;
            if (timeDifference < DateUnit.SECOND.getMillis() * just) {
                return JUST;
            } else {
                return DateUtil.formatBetween(timeDifference, BetweenFormatter.Level.SECOND) + BEFORE;
            }
        }

        // 一小时内
        if (timeDifference > DateUnit.MINUTE.getMillis() && timeDifference < DateUnit.HOUR.getMillis()) {
            return DateUtil.formatBetween(timeDifference, BetweenFormatter.Level.MINUTE) + MINUTE_AGO;
        }

        // 一天内
        if (timeDifference >= DateUnit.HOUR.getMillis() && timeDifference < DateUnit.DAY.getMillis()) {
            return DateUtil.formatBetween(timeDifference, BetweenFormatter.Level.HOUR) + BEFORE;
        }

        // 二十天内
        long twentyDays = DateUnit.DAY.getMillis() * NumberConstant.INTEGER_TWENTY;
        if (timeDifference >= DateUnit.DAY.getMillis() && timeDifference < twentyDays) {
            return DateUtil.formatBetween(timeDifference, BetweenFormatter.Level.DAY) + BEFORE;
        }
        return DateUtil.format(time, DatePattern.NORM_DATE_PATTERN);
    }

    /**
     * 根据 时间戳 格式化 日期
     * 默认的日期格式为:yyyy-MM-dd HH:mm:ss
     *
     * @param date 需要格式化的时间戳
     * @return 格式化后的字符串
     */
    public static String format(long date) {
        return DateUtil.format(DateUtil.date(date), DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 根据 时间戳 格式化 日期
     *
     * @param date   需要格式化的时间戳
     * @param format 日期格式[如:yyyy-MM-dd]
     * @return 格式化后的字符串
     */
    public static String format(long date, String format) {
        return DateUtil.format(DateUtil.date(date), format);
    }

    /**
     * 格式化日期
     * 默认的日期格式为:yyyy-MM-dd HH:mm:ss
     *
     * @param date 被格式化的日期
     * @return 格式化后的字符串
     */
    public static String format(Date date) {
        return DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 格式化日期
     *
     * @param date   被格式化的日期
     * @param format 日期格式[如:yyyy-MM-dd]
     * @return 格式化后的字符串
     */
    public static String format(Date date, String format) {
        return DateUtil.format(date, format);
    }

    /**
     * 将时间戳转成Date
     *
     * @param date 时间戳
     * @return Date日期
     */
    public static Date parse(long date) {
        return DateUtil.date(date);
    }

    /**
     * 将字符串形式的日期转为Date
     *
     * @param date 字符串格式的日期时间
     * @return Date日期
     */
    public static Date parse(String date) {
        return DateUtil.parse(date);
    }

    /**
     * 将字符串形式的日期转为Date
     * 指定转换的格式[yyyy-MM-dd HH:mm:ss]
     *
     * @param date   字符串格式的日期时间
     * @param format 需要转换的格式
     * @return Date日期
     */
    public static Date parse(String date, String format) {
        return DateUtil.parse(date, format);
    }

    /**
     * 获取当前月份，格式 yyyyMM
     *
     * @return 当前月份日期的字符串
     */
    public static String toMonth() {
        return DatePattern.SIMPLE_MONTH_FORMAT.format(new DateTime());
    }

    /**
     * 获取指定时间的月份，格式 yyyyMM
     *
     * @param date 指定时间
     * @return 当前月份日期的字符串
     */
    public static String toMonth(Date date) {
        return DatePattern.SIMPLE_MONTH_FORMAT.format(date);
    }

    /**
     * 获取一段时间内的天数
     *
     * @param start   开始时间
     * @param end     结束时间
     * @param pattern 时间格式
     * @return 时间差
     */
    public static List<String> getDaysByPeriodTime(LocalDateTime start, LocalDateTime end, String pattern) {
        return Stream.iterate(start, localDate -> localDate.plusDays(NumberConstant.INTEGER_ONE))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.DAYS.between(start, end) + NumberConstant.INTEGER_ONE)
                // 转换成字符串
                .map(time -> time.format(DateTimeFormatter.ofPattern(pattern)))
                // 把流收集为List
                .collect(Collectors.toList());
    }

    /**
     * 获取一段时间内的小时
     *
     * @param startDateTime 开始时间
     * @param endDateTime   结束时间
     * @param pattern       时间格式
     * @return 时间差
     */
    public static List<String> getHoursByPeriodTime(LocalDateTime startDateTime, LocalDateTime endDateTime, String pattern) {
        return Stream.iterate(startDateTime, localDate -> localDate.plusHours(NumberConstant.INTEGER_ONE))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.HOURS.between(startDateTime, endDateTime) + NumberConstant.INTEGER_ONE)
                // 转换成字符串
                .map(time -> time.format(DateTimeFormatter.ofPattern(pattern)))
                // 把流收集为List
                .collect(Collectors.toList());
    }

    /**
     * Date类型转为LocalDateTime类型，时区为系统默认时区
     *
     * @param date Date类型日期
     * @return LocalDateTime类型日期
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime类型转为Date类型，时区为系统默认时区
     *
     * @param localDateTime LocalDateTime类型日期
     * @return Date类型日期
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
