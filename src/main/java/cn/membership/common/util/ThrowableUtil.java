/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

public class ThrowableUtil {

    public static boolean instanceofClass(Throwable throwable , Class clazz){
        while(null != throwable){
            if(throwable.getClass() == clazz){
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }
    
}
