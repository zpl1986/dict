package cn.membership.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

public class Validator {

    protected final static Logger log = Logger.getLogger(Validator.class);
    
    public static void main(String[] args) {
//        System.out.println(is18BitIdcardOk("45072219851122226X"));
//        System.out.println(isValidDate("2015-01-02"+" "));
    	System.out.println(isMobileOK("19912345678")); 
    }

    public static boolean is18BitIdcardOk(String idcard) {
        boolean b = true;
        if(null == idcard){
            b = false;
        }else if (idcard.length() != 18) {
            b = false;
        } else if (!isDateString(idcard.substring(6, 14), "yyyyMMdd")) {
            b = false;
        } else {
            String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4", "3", "2" };
            String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
            String Ai = idcard.substring(0, 17);

            int TotalmulAiWi = 0;
            for (int i = 0; i < 17; i++) {
                TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
            }
            int modValue = TotalmulAiWi % 11;
            String strVerifyCode = ValCodeArr[modValue];
            Ai = Ai + strVerifyCode;

            if (!Ai.equalsIgnoreCase(idcard)) {
                b = false;
            }
        }
        return b;
    }

    public static boolean isTelephoneOk(String telephone) {
        boolean valid = isMobileOK(telephone);
        if (!valid) {
            telephone = telephone.replaceAll("[-+]", "");
            log.debug("telephone = "+telephone);
            valid = telephone.matches("[0-9]{7,14}");
        }
        return valid;
    }

    public static boolean isMobileOK(String mobile) {
        if (!StringUtils.hasLength(mobile)) {
            return false;
        }
        // String regex = "[1-9][0-9]{10}";
//        String regex = "((13|15|17|18)[0-9]{9})|((147)[0-9]{8})";
        String regex = "1[0-9]{10}";
        return mobile.trim().matches(regex);
    }
    
    
    public static boolean isDateString(String date, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            String date2 = sdf.format(sdf.parse(date));
            if (!date.equals(date2)) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isEmailOK(String email) {
        if (email == null) {
            return false;
        }
        String regex = "\\w+@\\w+(\\.\\w+)+";
        return email.matches(regex);
    }

    public static boolean isLong(Object number) {
        boolean value = false;
        if (number instanceof Long) {
            value = true;
        } else if (number instanceof String) {
            try {
                value = new Long((String) number).toString().equals(number);
            } catch (Exception e) {
                value = false;
            }
        }
        return value;
    }

    public static boolean isInteger(Object number) {
        boolean value = false;
        if (number instanceof Integer) {
            value = true;
        } else if (number instanceof String) {
            try {
                value = new Integer((String) number).toString().equals(number);
            } catch (Exception e) {
                value = false;
            }
        }
        return value;
    }

    public static boolean isNumber(Object number) {
        boolean value = false;
        if (number instanceof Number) {
            value = true;
        } else if (number instanceof String) {
            try {
                new Double((String) number);
                value = true;
            } catch (Exception e) {
                value = false;
            }
        }
        return value;
    }
    /**
     * 验证字符串是否能转换成Date
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
     // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
         try {
     // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
         } catch (ParseException e) {
            // e.printStackTrace();
     // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
             convertSuccess=false;
         } 
         return convertSuccess;
  }
    /**
     * 验证字符串是否能转换成DateTime
     * @param str
     * @return
     */
    public static boolean isValidDateTime(String str) {
        boolean convertSuccess=true;
     // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         try {
     // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
         } catch (ParseException e) {
            // e.printStackTrace();
     // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
             convertSuccess=false;
         } 
         return convertSuccess;
  }
    //    public final static int required = 1;
    //    public final static int type_int = 1;
    //    public final static int type_long = 1;
    //    public final static int type_double = 1;
    //    public final static int type_email = 1;
    //    public final static int type_mobile = 1;
    //    
    //    
    //    public static void main(String[] args) {
    //        Map<String, Object> map = new HashMap<>();
    //        new Validator().add(required+type_int,map);
    //    }

}

