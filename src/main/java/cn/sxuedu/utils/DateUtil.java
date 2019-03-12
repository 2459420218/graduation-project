package cn.sxuedu.utils;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间处理的工具类
 * */
public class DateUtil {

    private static final String  STANEDFORMAT="yyyy-MM-dd HH:mm:ss";

    /**
     * 将字符换转Date
     * @param datestr 字符串类型的时间
     * */
    public static Date stringToDate(String datestr){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(STANEDFORMAT);
            DateTime dateTime=dateTimeFormatter.parseDateTime(datestr);
            return dateTime.toDate();
    }


    /**
     * 将Date转字符串
     * */
    public static String dateToStr(Date date){
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANEDFORMAT);
    }


}
