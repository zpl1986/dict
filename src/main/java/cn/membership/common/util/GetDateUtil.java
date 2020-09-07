package cn.membership.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
@SuppressWarnings("all")
public class GetDateUtil {

	 public static List<Map<String, String>> getBetweenDateYearsBySdateEdate(String sdate,String edate) {
	        List<Map<String, String>> listDateMap = new ArrayList<Map<String, String>>();
	        Map<String, String> dateDataMap = null;
	        try {
	            List<String> list = getYearBetween(sdate, edate);
	            int i = 0;
	            for (String str : list) {
	      	      //每年12月的最大天数
	      	      String ym = str+"-12";
	      	      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
	              Date date1 = dateFormat.parse(ym);
	              Calendar calendar = new GregorianCalendar();
	              calendar.setTime(date1);
	              int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	                  
	                String sdate001 = str+"-01-01";
	                String edate001 = ym+"-"+days;
	                //第一次
	                if(i == 0){
	                    //查询条件开始日期大于月的第一天,用查询条件的开始日期
	                    if(GetDateUtil.getCompareTime(sdate, sdate001)>0){
	                        sdate001 = sdate;
	                    }
	                }
	                //最后一次
	                if(i == list.size() -1){
	                    //查询条件结束日期小于等于月的最后一条,用查询条件的结束日期
	                    if(GetDateUtil.getCompareTime(edate, edate001) <= 0){
	                        edate001 = edate;
	                    }
	                }
	                dateDataMap = new LinkedHashMap<String, String>();
	                dateDataMap.put("sdate", sdate001);
	                dateDataMap.put("edate", edate001);
	                
	      	        DateFormat dateFormat001 = new SimpleDateFormat("yyyy");
	                Date date001 = dateFormat001.parse(str);
	                Calendar calendar001 = new GregorianCalendar();
	                calendar001.setTime(date001);
	                int yeardays = calendar001.getActualMaximum(Calendar.DAY_OF_YEAR);
                    dateDataMap.put("maxday", String.valueOf(yeardays));
                    listDateMap.add(dateDataMap);
                    i++;
	            }
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        
	        
	        List<Map<String, String>> resListDateMap = new ArrayList<Map<String, String>>();
	        try {
	                //如果没有一个月的话,就过滤掉
	                for (Map<String, String> map : listDateMap) {
	                        String sdate002 = (String)map.get("sdate");
	                        String edate002 = (String)map.get("edate");
	                        String maxday002 = (String)map.get("maxday");
	                        Integer maxday = Integer.parseInt(maxday002);
	                        int days = GetDateUtil.daysBetween(java.sql.Date.valueOf(sdate002),java.sql.Date.valueOf(edate002))+1;
	                        if(null != maxday && maxday.intValue() == days){
	                            resListDateMap.add(map);
	                        }
	                }
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        return resListDateMap;
	    }
	 
	    private static List<String> getYearBetween(String minDate, String maxDate) {
	        ArrayList<String> result = new ArrayList<String>();
	    	if(null != minDate && !"".equals(minDate) && null != maxDate && !"".equals(maxDate)){
	    		String minYear = minDate.substring(0,4);
	    		String maxYear = maxDate.substring(0,4);
	    		int minYear001 = Integer.parseInt(minYear);
	    		int maxYear001 = Integer.parseInt(maxYear);
	    		if(minYear001 == maxYear001){
	    			result.add(String.valueOf(minYear001));
	    		}else{
	    			for (int i = minYear001; i <= maxYear001; i++) {
	    				result.add(String.valueOf(i));
					}
	    		}
	    	}
	        return result;
	    }
	    
    public static List<Map<String, String>> getBetweenDateMonthsBySdateEdate(String sdate,String edate) {
        List<Map<String, String>> listDateMap = new ArrayList<Map<String, String>>();
        Map<String, String> dateDataMap = null;
        try {
            List<String> list = getMonthBetween(sdate, edate);
            int i = 0;
            for (String str : list) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                Date date1 = dateFormat.parse(str);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date1);
                int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                
                String sdate001 = str+"-01";
                String edate001 = str+"-"+days;
                //第一次
                if(i == 0){
                    //查询条件开始日期大于月的第一天,用查询条件的开始日期
                    if(getCompareTime(sdate, sdate001)>0){
                        sdate001 = sdate;
                    }
                }
                //最后一次
                if(i == list.size() -1){
                    //查询条件结束日期小于等于月的最后一条,用查询条件的结束日期
                    if(getCompareTime(edate, edate001) <= 0){
                        edate001 = edate;
                    }
                }
                dateDataMap = new LinkedHashMap<String, String>();
                dateDataMap.put("sdate", sdate001);
                dateDataMap.put("edate", edate001);
                dateDataMap.put("maxday", String.valueOf(days));
                listDateMap.add(dateDataMap);
                i++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        
        List<Map<String, String>> resListDateMap = new ArrayList<Map<String, String>>();
        try {
                //如果没有一个月的话,就过滤掉
                for (Map<String, String> map : listDateMap) {
                        String sdate002 = (String)map.get("sdate");
                        String edate002 = (String)map.get("edate");
                        String maxday002 = (String)map.get("maxday");
                        Integer maxday = Integer.parseInt(maxday002);
                        int days = daysBetween(java.sql.Date.valueOf(sdate002),java.sql.Date.valueOf(edate002))+1;
                        if(null != maxday && maxday.intValue() == days){
                            resListDateMap.add(map);
                        }
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resListDateMap;
    }

    
    public static List<Map<String, String>> getBetweenDateWeeksBySdateEdate(String sdate,String edate){
        List<String> dateStrList = getBetweenDatesBySdateEdate(sdate, edate);
        List<Map<String, String>> listDateMap = new ArrayList<Map<String, String>>();
        Map<String, String> dateDataMap = null;
        String lastWeekSunday = "";
        for (String dateStr : dateStrList) {
            String week = getWeekOfDate(java.sql.Date.valueOf(dateStr));
            if("星期六".equals(week)){
                    //星期日
                    String weekMonday = getDateNextDay(java.sql.Date.valueOf(dateStr),-6);
                    String sdate001 = "";
                    if(getCompareTime(sdate, weekMonday)>=0){
                        sdate001 = sdate;
                    }else{
                        sdate001 = weekMonday;
                    }
                    dateDataMap = new LinkedHashMap<String, String>();
                    String sweek = getWeekOfDate(java.sql.Date.valueOf(sdate001));
                    dateDataMap.put("sweek", sweek);
                    dateDataMap.put("eweek", week);
                    dateDataMap.put("sdate", sdate001);
                    dateDataMap.put("edate", dateStr);
                    listDateMap.add(dateDataMap);
                    //星期日的时间
                    lastWeekSunday = dateStr;
            }
        }
        //最后的星期日小于查询条件的结束日期
        if(null != lastWeekSunday && !"".equals(lastWeekSunday) && getCompareTime(lastWeekSunday, edate)<0){
                  String weekMonday = getDateNextDay(java.sql.Date.valueOf(lastWeekSunday),1); //星期日的下一天,也就是下个星期一
                  String weekSunday = getDateNextDay(java.sql.Date.valueOf(weekMonday),6);//在加6天,下个星期日
                  String sdate001 = "";
                  String edate001 = "";
                  //如果查询条件的结束日期小于计算的下个星期日
                  if(getCompareTime(edate, weekSunday)<0){
                      sdate001 = weekMonday;
                      edate001 = edate;
                  }else{
                      sdate001 = weekMonday;
                      edate001 = weekSunday;
                  }
                  dateDataMap = new LinkedHashMap<String, String>();
                  String sweek = getWeekOfDate(java.sql.Date.valueOf(sdate001));
                  String eweek = getWeekOfDate(java.sql.Date.valueOf(edate001));
                  dateDataMap.put("sweek", sweek);
                  dateDataMap.put("eweek", eweek);
                  dateDataMap.put("sdate", sdate001);
                  dateDataMap.put("edate", edate001);
                  listDateMap.add(dateDataMap);
        }
        if(null != listDateMap && listDateMap.size() == 0){
                  dateDataMap = new LinkedHashMap<String, String>();
                  String sweek = getWeekOfDate(java.sql.Date.valueOf(sdate));
                  String eweek = getWeekOfDate(java.sql.Date.valueOf(edate));
                  dateDataMap.put("sweek", sweek);
                  dateDataMap.put("eweek", eweek);
                  dateDataMap.put("sdate", sdate);
                  dateDataMap.put("edate", edate);
                  listDateMap.add(dateDataMap);
        }
        List<Map<String, String>> resListDateMap = new ArrayList<Map<String, String>>();
        //如果没有七天,开始日期不是周日,结束日期不是周六,就过滤掉
        for (Map<String, String> map : listDateMap) {
            String sweek = (String)map.get("sweek");
            String eweek = (String)map.get("eweek");
            if("星期日".equals(sweek) && "星期六".equals(eweek)){
                resListDateMap.add(map);
            }
        }
        return resListDateMap;
    }

    
    public static String getDateNextDay(final Date date, final int num) {
        Calendar cla = Calendar.getInstance();
        cla.setTime(date);
        cla.add(Calendar.DAY_OF_YEAR, num);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(cla.getTime());
    }
    
    public static String getWeekOfDate(Date date) { 
        String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" }; 
        //String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" }; 
        Calendar calendar = Calendar.getInstance(); 
        calendar.setTime(date); 
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; 
        return weekDaysName[intWeek]; 
    } 
    
    public static int getCompareTime(final String sdate1, final String edate1) {
        Date sdate = getStrToDate(sdate1);
        Date edate = getStrToDate(edate1);
        int i = 0;
        if (sdate.getTime() > edate.getTime()) {
            i = 1;
        } else if (sdate.getTime() == edate.getTime()) {
            i = 0;
        } else if (sdate.getTime() < edate.getTime()) {
            i = -1;
        }
        return i;
    }
    
    public static Date getStrToDate(final String time) {
        java.sql.Date sd = java.sql.Date.valueOf(time);
        return new Date(sd.getTime());
    }
    
    public static List<String> getBetweenDatesBySdateEdate(final String sdate, final String edate) {
        List<String> list = new ArrayList<String>();
        try {
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date _sdate = df.parse(sdate);
            startCalendar.setTime(_sdate);
            Date _edate = df.parse(edate);
            endCalendar.setTime(_edate);
            list.add(df.format(_sdate.getTime()));
            while (true) {
                startCalendar.add(Calendar.DAY_OF_MONTH, 1);
                if (startCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()) {
                    list.add(df.format(startCalendar.getTime()));
                } else {
                    break;
                }
            }
            list.add(df.format(_edate.getTime()));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        List<String> reslist = new ArrayList<String>();
        for (String data : list) {
            if(!reslist.contains(data)){
                reslist.add(data);
            }
        }
        return reslist;
    }
    
    private static List<String> getMonthBetween(String minDate, String maxDate) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
            Calendar curr = min;
            while (curr.before(max)) {
                 result.add(sdf.format(curr.getTime()));
                 curr.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取格式化的系统日期
     * 
     * @return
     */
    public static Date getSysFormatDate() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String curtime = f.format(new Date());
        java.sql.Date sd = java.sql.Date.valueOf(curtime);
        return new Date(sd.getTime());
    }
    
    /**
     * 获取格式化的系统日期
     * 
     * @return
     */
    public static String getSysFormatDateStr() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String curtime = f.format(new Date());
        return curtime;
    }
    
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
        int days = Integer.parseInt(String.valueOf(between_days));
        return days;
    }
    
}
