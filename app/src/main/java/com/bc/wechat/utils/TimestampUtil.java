package com.bc.wechat.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimestampUtil {
    private static int hour;
    private static int day;
    private static Date date;
    private static DateFormat df;
    private static String pointText;
    private static Long now;

    public TimestampUtil() {
        hour = 60 * 60 * 1000;
        day = (60 * 60 * 24) * 1000;
        now = new Date().getTime();
        pointText = "1970-01-01";
    }

    // 获得当天0点时间
    public static Long getTimesMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // 获取星期几
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    // 获取时间点
    public static String getTimePoint(Long time) {
        // 现在时间
        now = new Date().getTime();

        // 时间点比当天零点早
        if (time <= now && time != null) {
            date = new Date(time);
            if (time < getTimesMorning()) {
                if (time >= getTimesMorning() - day) {
                    // 比昨天零点晚
                    pointText = "昨天";
                    return pointText;
                } else {
                    // 比昨天零点早
                    if (time >= getTimesMorning() - 6 * day) {
                        // 比七天前的零点晚，显示星期几
                        return getWeekOfDate(date);
                    } else {
                        // 显示具体日期
                        df = new SimpleDateFormat("yyyy-MM-dd");
                        pointText = df.format(date);
                        return pointText;
                    }
                }
            } else {
                // 无日期时间,当天内具体时间
                df = new SimpleDateFormat("HH:mm");
                pointText = df.format(date);
                int hour = Integer.parseInt(pointText.split(":")[0]);
                String period = "";
                if (hour >= 0 && hour <= 6) {
                    period = "凌晨";
                }
                if (hour > 6 && hour <= 12) {
                    period = "上午";
                }
                if (hour == 13) {
                    period = "中午";
                }
                if (hour > 13 && hour <= 18) {
                    period = "下午";
                }
                if (hour > 18 && hour <= 24) {
                    period = "晚上";
                }

                return period + pointText;
            }
        }
        return pointText;
    }

    public static String[] getYearMonthAndDay(long timeStamp) {
        try {
            Date date = new Date(timeStamp);
            String[] time = new SimpleDateFormat("yyyy-MM-dd").format(date).split("-");
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getYearMonthAndDay(1574151206223L)[0]);
        System.out.println(getYearMonthAndDay(1574151206223L)[1]);
        System.out.println(getYearMonthAndDay(1574151206223L)[2]);
    }
}
