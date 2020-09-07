/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;




/**
 * @author lichunlin
 * @version 0.0.1
 * @since 	
 */
public class GetAppUtil {
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getParameterValueMap(HttpServletRequest request) {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            queryMap.put(key, request.getParameter(key));
        }
        return queryMap;
    }
    
    public static String getCientIpAddress(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
    }
    
    /** 
     * 方法: getMaxDayByYearMonth 
     * 描述: 返还指定的年月最大的天数 
     * 作者: lichunlin 
     * 时间: 2015-12-17 
     * @param year
     * @param month
     * @return int
     */
    public static int getMaxDayByYearMonth(int year,int month) {
          int day = 0;
          if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
                         day = 31;
          }else if(month == 4 || month == 6 || month == 9 || month == 11){
                         day = 30;
          }else if(month == 2){
                                  if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                                                 day = 29;
                                  }else{
                                             day = 28;
                                  }
          }
          return day;
    }
    
    public static BigDecimal getObject2BigDecimal(Object obj){
        if(isNull(obj)){
             return BigDecimal.ZERO;
        }else{
             return new BigDecimal(obj.toString());
        }
    }
    
    public static boolean isNull(Object obj){
        return (null == obj) ? true : false;
    }
    
    public static BigDecimal getBigDecimalEmptyDefaultZero(BigDecimal bigdecimal){
        if(isNull(bigdecimal)){
             return BigDecimal.ZERO;
        }else{
             return bigdecimal;
        }
    }
    
}
