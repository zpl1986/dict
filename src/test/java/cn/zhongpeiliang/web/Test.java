package cn.zhongpeiliang.web;

import java.math.BigDecimal;
import java.util.Date;

public class Test {

    static void append(StringBuilder builder) {
        builder.append(new Date());
        cn.membership.web.interceptor.DefaultRequestInterceptor defaultRequestInterceptor = null;
    }

    static void printTime(String number) {
        long now = System.currentTimeMillis();
        int count = 100000;
        for (int i = 0; i < count; i++) {
            isNumber2(number);
        }
        long now2 = System.currentTimeMillis();
        System.out.println(now2 - now);
        for (int i = 0; i < count; i++) {
            isNumber(number);
        }
        System.out.println(System.currentTimeMillis() - now2);
    }

    static boolean isNumber2(String number) {
        String regex = "\\d+(\\.\\d+)?";
        return number.matches(regex);
    }

    static boolean isNumber(String number) {
        try {
            new BigDecimal(number);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String string = new String("str");
        String string2 = new String("str");
        String string21 = "str";
        System.out.println(string.intern() == string2.intern());
    }
    
}
