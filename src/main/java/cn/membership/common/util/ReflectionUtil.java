/**
 *
 * Copyright (c) 2015 Chutong Technologies All rights reserved.
 *
 */
package cn.membership.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * 
 * @author N. SUN
 * @version 0.0.1
 * @since 	
 */
public class ReflectionUtil {
    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     * 
     * @param object 子类对象
     * @param methodName 父类中的方法名
     * @param parameterTypes 父类中的方法参数类型
     * @return 父类中的方法对象
     */
    public static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;

        if (null != object) {
            for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    method = clazz.getDeclaredMethod(methodName, parameterTypes);
                } catch (Exception e) {
                    //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。   
                    //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了   
                }
            } // for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass())

        } // if (null != object)

        return method;
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     * 
     * @param object 子类对象
     * @param methodName 父类中的方法名
     * @param parameterTypes 父类中的方法参数类型
     * @param parameters 父类中的方法参数
     * @return 父类中方法的执行结果
     */
    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) {

        Object result = null;

        //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象   
        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        if (null != method) {
            //抑制Java对方法进行检查,主要是针对私有方法而言   
            method.setAccessible(true);

            try {
                //调用object 的 method 所代表的方法，其方法的参数是 parameters   
                result = method.invoke(object, parameters);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } // if (null != method)

        return result;
    }

    public static Object invokeMethod(final Object object, final String methodName) {
        return invokeMethod(object, methodName, new Class[] {}, new Object[] {});
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     * 
     * @param object 子类对象
     * @param fieldName 父类中的属性名
     * @return 父类中的属性对象
     */
    public static Field getDeclaredField(Object object, String fieldName) {
        Field field = null;

        if (null != object && null != fieldName) {
            for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (Exception e) {
                    //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。   
                    //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了   

                }
            } // for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass())            
        } // if (null != object && null != fieldName)

        return field;
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     * 
     * @param object 子类对象
     * @param fieldName 父类中的属性名
     * @param value 将要设置的值
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象   
        Field field = getDeclaredField(object, fieldName);

        if (null != field) {
            //抑制Java对其的检查   
            field.setAccessible(true);

            try {
                //将 object 中 field 所代表的值 设置为 value   
                field.set(object, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } // if (null != field)
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     * 
     * @param object 子类对象
     * @param fieldName 父类中的属性名
     * @return 父类中的属性值
     */
    public static Object getFieldValue(Object object, String fieldName) {

        Object fieldValue = null;

        //根据 对象和属性名通过反射 调用上面的方法获取 Field对象   
        Field field = getDeclaredField(object, fieldName);
        if (null != field) {
            //抑制Java对其的检查   
            field.setAccessible(true);

            try {
                //获取 object 中 field 所代表的属性值   
                fieldValue = field.get(object);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } // if (null != field)

        return fieldValue;
    }
}
