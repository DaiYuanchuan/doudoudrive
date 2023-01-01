package com.doudoudrive.common.util.data;

import com.doudoudrive.common.util.date.DateUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>时间工具类单元测试</p>
 * <p>2023-01-01 16:40</p>
 *
 * @author Dan
 **/
@Slf4j
public class DateUtilsTest {

    /**
     * 传入任意 顺序 的日期 , 对日期进行 开始日期 < 结束日期 的排序
     * 遵循 小的日期在前面[开始日期] 大的日期在后面[结束日期]
     */
    @Test
    @SneakyThrows
    public void sortByDateTest() {
        String startTime = "2021-01-01 23:59:59";
        String endTime = "2021-01-01 00:00:00";
        String format = "yyyy-MM-dd HH:mm:ss";

        String[] time = DateUtils.sortByDate(startTime, endTime, format);
        Assert.isTrue(time != null, "time is null !");
        Assert.isTrue(time.length == 2, "time length is not 2 !");
        Assert.isTrue(time[0].equals("2021-01-01 00:00:00"), "time[0] is not 2021-01-01 00:00:00 !");
        Assert.isTrue(time[1].equals("2021-01-01 23:59:59"), "time[1] is not 2021-01-01 23:59:59 !");

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        Date[] date = DateUtils.sortByDate(startDate, endDate);

        Assert.isTrue(date.length == 2, "date length is not 2 !");
        Assert.isTrue(date[0].equals(endDate), "date[0] is not endDate !");
        Assert.isTrue(date[1].equals(startDate), "date[1] is not startDate !");
    }

    /**
     * 验证字符串是否可以转成时间格式
     */
    @Test
    public void isTimeTest() {
        String time1 = "2021-01-01 23:59";
        String time2 = "2021-01-01 00:00:00";

        String format = "yyyy-MM-dd HH:mm:ss";
        Assert.isTrue(!DateUtils.isTime(time1, format), "time1 is time !");
        Assert.isTrue(DateUtils.isTime(time2, format), "time2 is not time !");
    }

    /**
     * 根据传入的时间与现在的时间差
     * 将时间差转成可读字符串 如:刚刚、3秒前、一小时前等等
     */
    @Test
    public void timeDifferenceTest() {
        Date date = new Date();
        String just = DateUtils.timeDifference(date);

        // 减去10分钟
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -10);

        Date newDate = calendar.getTime();
        String tenMinute = DateUtils.timeDifference(newDate);

        Assert.isTrue(just.equals("刚刚"), "just is not 刚刚 !");
        Assert.isTrue(tenMinute.equals("10分钟前"), "tenMinute is not 10分钟前 !");
    }

    /**
     * 格式化 日期
     */
    @Test
    @SneakyThrows
    public void formatTest() {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date startDate = sdf.parse("2021-01-01 00:00:00");

        Assert.isTrue(DateUtils.format(startDate.getTime()).equals("2021-01-01 00:00:00"), "current is not 2021-01-01 00:00:00 !");
        Assert.isTrue(DateUtils.format(startDate.getTime(), format).equals("2021-01-01 00:00:00"), "current is not 2021-01-01 00:00:00 !");
        Assert.isTrue(DateUtils.format(startDate).equals("2021-01-01 00:00:00"), "current is not 2021-01-01 00:00:00 !");
        Assert.isTrue(DateUtils.format(startDate, format).equals("2021-01-01 00:00:00"), "current is not 2021-01-01 00:00:00 !");
    }

    /**
     * toDate
     */
    @Test
    @SneakyThrows
    public void parseTest() {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date startDate = sdf.parse("2021-01-01 00:00:00");

        Assert.isTrue(DateUtils.parse(startDate.getTime()).equals(startDate), "date is not 2021-01-01 00:00:00 !");
        Assert.isTrue(DateUtils.parse("2021-01-01 00:00:00").equals(startDate), "date is not 2021-01-01 00:00:00 !");
        Assert.isTrue(DateUtils.parse("2021-01-01 00:00:00", format).equals(startDate), "date is not 2021-01-01 00:00:00 !");
    }

    /**
     * 获取指定时间的月份
     */
    @Test
    @SneakyThrows
    public void toMonthTest() {
        String format = "yyyyMM";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date now = new Date();
        String month = sdf.format(now);

        Assert.isTrue(DateUtils.toMonth().equals(month), "month is not " + month + " !");
        Assert.isTrue(DateUtils.toMonth(now).equals(month), "month is not " + month + " !");
    }

    /**
     * 获取一段时间内的天数
     */
    @Test
    public void getDaysByPeriodTimeTest() {
        LocalDateTime minusDays = LocalDateTime.now();
        minusDays = minusDays.minusDays(10);

        List<String> stringList = DateUtils.getDaysByPeriodTime(minusDays, LocalDateTime.now(), "yyyy-MM-dd");
        Assert.isTrue(stringList.size() == 11, "stringList size is not 11 !");
    }

    /**
     * 获取一段时间内的小时
     */
    @Test
    public void getHoursByPeriodTimeTest() {
        LocalDateTime minusDays = LocalDateTime.now();
        minusDays = minusDays.minusDays(10);

        List<String> stringList = DateUtils.getHoursByPeriodTime(minusDays, LocalDateTime.now(), "yyyy-MM-dd HH");
        Assert.isTrue(stringList.size() == 241, "stringList size is not 241 !");
    }

    /**
     * Date类型转为LocalDateTime类型，时区为系统默认时区
     */
    @Test
    public void toLocalDateTimeTest() {
        Date date = new Date();
        LocalDateTime localDateTime = DateUtils.toLocalDateTime(date);

        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Assert.isTrue(sdf.format(date).equals(localDateTime.format(DateTimeFormatter.ofPattern(format))), "localDateTime is not " + sdf.format(date) + " !");
    }

    /**
     * LocalDateTime类型转为Date类型，时区为系统默认时区
     */
    @Test
    public void toDateTest() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = DateUtils.toDate(localDateTime);

        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);

        Assert.isTrue(sdf.format(date).equals(localDateTime.format(DateTimeFormatter.ofPattern(format))), "date is not " + sdf.format(date) + " !");
    }
}
