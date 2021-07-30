package com.bc.wechat.moments.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author way
 * <p>
 * 今天格式为12小时，上，下午判定
 * 昨天前天为12小时制
 * 更早直接日期
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    public static String converTime1(Context context, long timestamp) {
        long nowTimestamp = System.currentTimeMillis() / 1000;
        long timeDifference = nowTimestamp - timestamp / 1000;// 与现在时间相差秒数

        long secondTime = timeDifference;
        long minuteTime = secondTime / 60;
        long hoursTime = minuteTime / 60;

        long dayTime = hoursTime / 24;
        long monthTime = dayTime / 30;
        long yearTime = monthTime / 12;

        if (1 <= yearTime) {
            return yearTime + "年前";
        } else if (1 <= monthTime) {
            return monthTime + "月前";
        } else if (1 <= dayTime) {
            return dayTime + "天前";
        } else if (1 <= hoursTime) {
            return hoursTime + "小时前";
        } else if (1 <= minuteTime) {
            return minuteTime + "分钟前";
        } else if (1 <= secondTime) {
            return secondTime + "秒前";
        } else {
            return "刚刚";
        }
    }

    //转换时间
    public static String converTime(Context context, long time) {
        long currentSeconds = System.currentTimeMillis() / 1000;
        long timeGap = currentSeconds - time / 1000;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > 3 * 24 * 60 * 60) {
            timeStr = getDayTime(time) + " " + getMinTime24(time);
        } else if (timeGap > 24 * 2 * 60 * 60) {// 2天以上就返回标准时间
            String temp_before = "前天";//context.getResources().getString(R.string.time_yesterday_before);
            timeStr = temp_before + getMinTime24(time);
        } else if (timeGap > 24 * 60 * 60) {// 1天-2天
            String temp_yesterday = "昨天";//context.getResources().getString(R.string.time_yesterday);
            timeStr = temp_yesterday + getMinTime24(time);
        }

		/*else if (timeGap > 60 * 60) {// 1小时-24小时
//			timeStr = timeGap / (60 * 60) + "今天 " + getMinTime(time);
//		} else if (timeGap > 60) {// 1分钟-59分钟
//			timeStr = timeGap / 60 + "今天 " + getMinTime(time);
//
		}**/
        else {// 1秒钟-59秒钟
//			timeStr = "今天 " + getMinTime(time);

            //如果是今天，再细分，分到上午下午
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(time);
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);

            String am_pm = "";
            if (hour > 0 && hour <= 6) {
                am_pm = "凌晨";
            } else if (hour > 6 && hour <= 8) {
                am_pm = "早晨";
            } else if (hour > 8 && hour <= 12) {
                am_pm = "上午";
            } else if (hour > 12 && hour <= 18) {
                am_pm = "下午";
            } else if (hour > 18 && hour <= 24) {
                am_pm = "晚上";
            }

            timeStr = am_pm + getMinTime12(time);
        }
        return timeStr;
    }


    public static String getDayTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        return format.format(new Date(time));
    }

    public static String getMinTime24(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getMinTime12(long time) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        return format.format(new Date(time));
    }
}
