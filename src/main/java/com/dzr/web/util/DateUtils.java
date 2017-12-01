package com.dzr.web.util;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public final class DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyyMMdd");

    public static String getDateNowTime() {
        return dateFormat.format(new Date());
    }

    public static String getDateNowDay() {
        return dateFormatDay.format(new Date());
    }

    /**
     * 返回int类型的当前时间
     */
    public static int getNowTime() {
        Long current = System.currentTimeMillis();
        String currentTime = String.valueOf(current / 1000);
        return Integer.parseInt(currentTime);
    }

    /**
     * 将int类型装换为指定格式的日期
     */
    public static String convertToFormatDate(int time, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(new Date(time * 1000L));
    }

    /**
     * 将int类型装换为日期
     */
    public static String convertToDate(int time) {
        return convertToFormatDate(time, "yyyyMMdd");
    }

    /**
     * 日期转化为指定格式的Integer型日期
     */
    public static Integer convertToInteger(int time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return Integer.valueOf(sdf.format(new Date(time * 1000L)));
    }

    /**
     * 时间转化为int
     */
    public static Integer dateToInt(String time) throws ParseException {
        return dateToInt(time, "yyyyMMdd");
    }

    /**
     * 时间转化为int
     */
    public static Integer dateToInt(String time, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(time));
        long time1 = cal.getTimeInMillis();
        return Integer.parseInt(String.valueOf(time1 / 1000L));
    }


    /**
     * 两个日期相差天数
     */
    public static int daysBetween(String smDate, String bDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smDate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bDate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 两时期时间差天数
     */
    public static int differDays(int startTime, int endTime) {
        String smDate = DateUtils.convertToDate(startTime);
        String bDate = DateUtils.convertToDate(endTime);
        try {
            return DateUtils.daysBetween(smDate, bDate);
        } catch (ParseException e) {
            logger.error("两时期时间差天数出错" + e.getMessage());
        }
        return -1;
    }

    /**
     * 加几个月后的时间
     */
    public static String monthAdd(String now, Integer len) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(now));
            cal.add(Calendar.MONTH, +len);
        } catch (ParseException e) {
            logger.error("加几个月后的时间出错" + e.getMessage());
        }
        return format.format(cal.getTime());
    }

    /**
     * 加几个月后的时间
     */
    public static Integer monthAdd(Integer now, Integer len) {
        String nowStr = convertToDate(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(nowStr));
            cal.add(Calendar.MONTH, +len);
            return dateToInt(format.format(cal.getTime()));
        } catch (ParseException e) {
            logger.error("加几个月后的时间出错" + e.getMessage());
        }
        return 0;
    }

    /**
     * 日期的月份计算
     */
    public static String addMonth(String time1, int months) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c1 = getCalendar(time1);
        c1.add(Calendar.MONTH, months);
        return sdf.format(c1.getTime());
    }

    /**
     * 日期的月份减
     * @param date    日期
     * @param months  月份
     * @param pattern 格式
     */
    public static String minusMonths(String date, int months, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        YearMonth ym = YearMonth.parse(date, dateTimeFormatter);
        YearMonth resultYm = ym.minusMonths(months);
        return resultYm.format(dateTimeFormatter);
    }

    /**
     * 加几天后的时间
     */
    public static Integer dateAdd(Integer now, Integer len) {
        String nowStr = convertToDate(now);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(format.parse(nowStr));
            cal.add(Calendar.DATE, +len);
            return dateToInt(format.format(cal.getTime()));
        } catch (ParseException e) {
            logger.error("天数相加出错" + e.getMessage());
        }
        return 0;
    }

    /**
     * 两个日期比较月份大小（后者大 返回正数；前者大负数）
     */
    public static int monthCompareWithDay(String time1, String time2) {
        Calendar c1 = getCalendar(time1);
        Calendar c2 = getCalendar(time2);
        int day = c2.get(Calendar.DATE) - c1.get(Calendar.DATE);
        int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        int years = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        if (day < 0) {
            month = month - 1;
        }
        return month + years * 12;
    }

    /**
     * 两个日期比较月份大小（后者大 返回正数；前者大负数）
     */
    public static int monthCompare(String time1, String time2) {
        Calendar c1 = getCalendar(time1);
        Calendar c2 = getCalendar(time2);
        int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        int years = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        return month + years * 12;
    }

    private static Calendar getCalendar(@NotBlank String time) {
        String[] times = time.split("-");
        Calendar calendar = Calendar.getInstance();
        if (times.length > 2) {
            calendar.set(Integer.parseInt(times[0]), Integer.parseInt(times[1]) - 1, Integer.parseInt(times[2]));
        } else {
            calendar.set(Integer.parseInt(times[0]), Integer.parseInt(times[1]) - 1, 10);
        }
        return calendar;
    }

    /**
     * 计算相差月数
     */
    public static int differMonth(Long beginTime, Long endTime) {

        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        begin.setTimeInMillis(beginTime);
        end.setTimeInMillis(endTime);

        return (end.get(Calendar.YEAR) - begin.get(Calendar.YEAR)) * 12 +
                (end.get(Calendar.MONTH) - begin.get(Calendar.MONTH));
    }


    /***
     * 日期月份加减处理
     */
    public static String monthAdd(String datetime, Integer diff, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cl = Calendar.getInstance();
        try {
            cl.setTime(sdf.parse(datetime));
            cl.add(Calendar.MONTH, diff);
            return sdf.format(cl.getTime());
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return "";
    }

    /**
     * 计算两个日期与当前年是否相同，不相同返回差值（不关心天数）
     * @param year1 减数
     * @param year2 被减数
     * year2 - year1
     */
    public static int yearDiff(int year1, int year2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String date2 = sdf.format(new Date(year2 * 1000L));
        String date1 = sdf.format(new Date(year1 * 1000L));

        return Integer.parseInt(date2) - Integer.parseInt(date1);
    }

    /**
     * 判断是否过调剂生效月（月份与月份的比较,不计算天数）
     */
    public static int getMonthCompare(int startTime, int endTime){
        return monthCompare(convertToFormatDate(startTime, "yyyy-MM-dd"), convertToFormatDate(endTime, "yyyy-MM-dd"));
    }
/***
 * 获取具体月份
 */
    public static int getMonth(int t){
        Calendar c2 = getCalendar(convertToFormatDate(t,"yyyy-MM-dd"));
        return  c2.get(Calendar.MONTH)+1;
    }
    /***
     * 获取具体日期
     */
    public static int getDay(int t){
        Calendar c2 = getCalendar(convertToFormatDate(t,"yyyy-MM-dd"));
        return  c2.get(Calendar.DATE);
    }
    /***
     * 取年月拼接具体日期
     */
    public static int monthJoinDay(int time,int day){
        return  dateAdd(time,(day-getDay(time)));
    }
}
