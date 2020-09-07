/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 * 
 * @author N. SUN
 * @version 0.0.1
 * @since 	
 */
public class TimeUtil {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected final static Logger log = Logger.getLogger(TimeUtil.class);

    public static final String getCurrentDatetimeString() {

        String currentDatetimeString = null;

        if (null != simpleDateFormat) {
            currentDatetimeString = simpleDateFormat.format(new Date());
        } // if (null != simpleDateFormat)

        return currentDatetimeString;
    }

    public static final String getDatetimeString(Date date) {
        String datetimeString = null;

        if (null != simpleDateFormat) {
            datetimeString = simpleDateFormat.format(date);
        } // if (null != simpleDateFormat)       

        return datetimeString;
    }

    public static final String getDatetimeString(Long date) {
        String datetimeString = null;

        if (null != simpleDateFormat) {
            datetimeString = simpleDateFormat.format(new Date(date));
        } // if (null != simpleDateFormat)       

        return datetimeString;
    }

    /*
     * 今天是星期几
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK); //weekday=1，当天是周日；weekday=2，当天是周一；...;weekday=7，当天是周六
    }

    public static Date getRepaymentDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**  
     * 计算当前处于的计划中期数  
     * @param load_date 放款的时间 
     * @return 所处期数
     * @throws ParseException  
     */
    public static int getCurrenPriod(Date loadDate, int periodTotal) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        log.debug("getCurrenPriod");
        log.debug("loadDate:" + sdf.format(loadDate));
        log.debug("periodTotal:" + periodTotal);
        Date firstShouldRepayDay = sdf.parse(sdf.format(getRepaymentDay(loadDate, 1)));//第一期应还日期
        Date now = sdf.parse(sdf.format(new Date()));
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(firstShouldRepayDay);
        c2.setTime(now);
        int years = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        int result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH) + years * 12;
        log.debug("result:" + result);

        if (result < 0) { //当前月份少于第一期应还月份     
            result = 1; //第一期              
        } else if (result == 0) { //当前月份等于第一期应还月份
            long between_days = (c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000 * 3600 * 24);
            if (between_days <= 0) //当前天数少于等于第一期应还天数
                result = 1; //第一期          
            else //当前天数大于第一期应还天数
                result = 2; //第二期          
        } else { //当前月份大于第一期应还月份 
            Date shouldRepayDay = getRepaymentDay(loadDate, result + 1); //当前月份所处应还期数的应还日期
            c1.setTime(sdf.parse(sdf.format(shouldRepayDay)));
            long between_days = (c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000 * 3600 * 24);
            log.debug("shouldRepayDay:" + sdf.format(shouldRepayDay));
            log.debug("between_days:" + between_days);
            if (between_days <= 0) //当前天数少于等于当期应还天数
                result += 1; //当期       
            else //当前天数大于当期应还天数
                result += 2; //下期
        }

        if (result > periodTotal)
            result = periodTotal;

        return result;

    }

    /**
     * @param month 为正数，方法不判断
     * @return 一个月前
     */
    public static Date SomeMonthAgo(int month) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, -1 * month);
        return instance.getTime();
    }

}
