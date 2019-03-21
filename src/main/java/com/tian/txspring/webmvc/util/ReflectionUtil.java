package com.tian.txspring.webmvc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by tianxiong on 2019/3/21.
 */
public class ReflectionUtil {
    /**
     * 创建实例
     * @param clazz
     * @return
     */
    public static Object newInstance(Class<?> clazz){
        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    /**
     * 根据类名创建实例
     * @param className
     * @return
     */
    public static Object newInstance(String className){
        Class<?> clazz = ClassUtils.loadClass(className);
        return newInstance(clazz);

    }

    public static Object invokeMethod(Object obj , Method method, Object ... args){
        Object result;
        method.setAccessible(true);
        try {
            result = method.invoke(obj,args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 设置成员变量的值
     * @param obj
     * @param field
     * @param value
     */
    public static void setFiled(Object obj, Field field, Object value){
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
