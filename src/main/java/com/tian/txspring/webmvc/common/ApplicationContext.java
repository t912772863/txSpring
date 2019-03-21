package com.tian.txspring.webmvc.common;

import com.tian.txspring.webmvc.annotation.TXAspect;
import com.tian.txspring.webmvc.annotation.TXPointCut;
import com.tian.txspring.webmvc.aop.AbsMethodAdvance;
import com.tian.txspring.webmvc.util.ClassUtils;
import com.tian.txspring.webmvc.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tianxiong on 2019/3/21.
 */
public class ApplicationContext {
    /**
     * 存放代理类的集合
     */
    public static ConcurrentHashMap<String,Object> proxyBeanMap = new ConcurrentHashMap<String, Object>();
    static {
        initAopBeanMap("com.tian.test.demo");
    }
    /**
     * 初始化容器
     * @param basePath
     */
    public static void initAopBeanMap(String basePath){
        try{
            Set<Class<?>> classSet = ClassUtils.getClassSet(basePath);
            for(Class clazz : classSet){
                if(clazz.isAnnotationPresent(TXAspect.class)){
                    Method[] methods = clazz.getMethods();
                    for(Method method : methods){
                        if(method.isAnnotationPresent(TXPointCut.class)){
                            //找到切点
                            TXPointCut pointCut = (TXPointCut)method.getAnnotations()[0];
                            String pointCutStr = pointCut.value();
                            //System.out.println("pointCutStr:" + pointCutStr);
                            String[] pointCutArr = pointCutStr.split("_");
                            //被代理的类名
                            String className = pointCutArr[0];
                            //System.out.println("className:" + className);
                            //被代理的方法名
                            String methodName = pointCutArr[1];
                            // System.out.println("methodName:" + methodName);

                            //根据切点 创建被代理对象
                            Object targeObj = ReflectionUtil.newInstance(className);
                            //根据切面类创建代理者
                            AbsMethodAdvance proxyer = (AbsMethodAdvance)ReflectionUtil.newInstance(clazz);
                            //设置代理的方法
                            proxyer.setProxyMethodName(methodName);

                            Object object = proxyer.createProxyObject(targeObj);

                            if(object != null){
                                proxyBeanMap.put(targeObj.getClass().getSimpleName().toLowerCase(),object);
                            }
                        }
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
