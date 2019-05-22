package com.tian.txspring.webmvc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 通过反射获取到对像所有属性的值
     *
     * @param object
     * @param filterNull 是否过滤空值
     * @return K为属性名, V为值
     */
    public static Map<String, Object> getFiledValues(Object object, boolean filterNull) {
        Field[] fields = object.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap();
        for(Field f: fields){
            f.setAccessible(true);
            Object value = null;
            try {
                value = f.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if(filterNull && value == null){
                continue;
            }
            map.put(f.getName(), value);
        }
        return map;
    }

    /**
     * 获取还有指定注解的
     * @param object
     * @param filterNull
     * @param annotationClass
     * @param <T>
     * @return
     */
    public static <T extends Annotation>  Map<String, Object> getFiledValues(Object object, boolean filterNull, Class<T> annotationClass) {
        Field[] fields = object.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap();
        for(Field f: fields){
            T t = f.getAnnotation(annotationClass);
            if(t == null){
                continue;
            }
            f.setAccessible(true);
            Object value = null;
            try {
                value = f.get(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if(filterNull && value == null){
                continue;
            }
            map.put(f.getName(), value);
        }
        return map;
    }

}
