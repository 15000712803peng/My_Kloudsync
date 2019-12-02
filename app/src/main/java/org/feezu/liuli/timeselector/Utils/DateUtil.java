package org.feezu.liuli.timeselector.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by liuli on 2015/11/27.
 */
public class DateUtil {


    /**
     * 使用用户格式提取字符串日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期格式
     * @return
     */

    public static Date parse(String strDate, String pattern) {

        if (TextUtil.isEmpty(strDate)) {
            return null;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用用户格式格式化日期
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return
     */

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    public static String getMeetingTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss",
                Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = new Date(timestamp+8*60*60*1000);
        sdf.format(date);
        return sdf.format(date);
    }




}
