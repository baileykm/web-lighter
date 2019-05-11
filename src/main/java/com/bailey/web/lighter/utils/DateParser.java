package com.bailey.web.lighter.utils;

import com.bailey.web.lighter.WebLighterConfig;
import com.bailey.web.lighter.vo.SearchCriteria;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期型数据解析工具类
 */
public class DateParser {

    private static final Logger logger = LoggerFactory.getLogger(WebLighterConfig.LIB_NAME + ".utils.DateParser");

    public static final String UTC_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final DateFormat UTC_FORMAT;
    public static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    public static final DateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());


    static {
        UTC_FORMAT = new SimpleDateFormat(UTC_FORMAT_STRING, Locale.getDefault());
        UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * 将字符串表示的 ISO 日期时间转为 Date 类型
     *
     * @param dateStr 时间字符串
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            return UTC_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            logger.warn("解析时间日期出错", e);
            return null;
        }
    }

    /**
     * 将字符串表示的 ISO 日期时间转为 Date 类型
     *
     * @param dateStrArr 时间字符串数组
     */
    public static Date[] parseDate(String... dateStrArr) {
        if (dateStrArr == null) return null;
        Date[] dates = new Date[dateStrArr.length];
        for (int i = 0; i < dateStrArr.length; i++) {
            dates[i] = parseDate(dateStrArr[i]);
        }
        return dates;
    }


    /**
     * 将字符串时段转为日期数组
     *
     * @param dateRange 形如"2018/11/17 - 2018/12/31"
     */
    public static Date[] parseDateRange(String dateRange) throws ParseException {
        if (StringUtils.isBlank(dateRange)) return null;
        String[] dateStrArr = dateRange.split("-");
        if (dateStrArr.length < 2) throw new ParseException("日期范围格式错误, 应为 yyyy/MM/dd - yyyy/MM/dd", 0);
        return new Date[]{dateStrArr[0] == null ? null : SIMPLE_DATE_FORMAT.parse(dateStrArr[0]), dateStrArr[1] == null ? null : SIMPLE_DATE_FORMAT.parse(dateStrArr[1])};
    }

    /**
     * 将检索条件中的 ISO 日期时间转为 Date 类型数组.
     * <p>
     * 默认字段名分别为 dateMin, dateMax
     *
     * @param criteria 时间字符串数组
     */
    public static Date[] parseDate(SearchCriteria criteria) {
        return parseDate(criteria, "dateMin", "dateMax");
    }

    /**
     * 将检索条件中的 ISO 日期时间转为 Date 类型数组
     *
     * @param criteria 时间字符串数组
     * @param key      字段名
     */
    public static Date[] parseDate(SearchCriteria criteria, String... key) {
        String[] dateStrArr = new String[key.length];
        for (int i = 0; i < key.length; i++) {
            dateStrArr[i] = criteria.get(key[i]);
        }
        return parseDate(dateStrArr);
    }
}
