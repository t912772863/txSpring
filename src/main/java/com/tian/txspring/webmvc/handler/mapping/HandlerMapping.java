package com.tian.txspring.webmvc.handler.mapping;

import com.tian.txspring.webmvc.annotation.TXRequestMapping;
import com.tian.txspring.webmvc.aop.AopBeanContainer;
import com.tian.txspring.webmvc.ioc.BeanContainer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianxiong on 2019/3/24.
 */
public class HandlerMapping {
    private AopBeanContainer aopBeanContainer;
    private BeanContainer beanContainer;
    /**
     * 方法与url映射关系
     */
    public Map<String, Method> mappings = new HashMap<String, Method>();

    private HandlerMapping(){}
    private static HandlerMapping instance = new HandlerMapping();
    public static HandlerMapping getInstance(){
        return instance;
    }


    /**
     * 初始化请求路径映射.
     * 一般init开始的方法为初始化方法
     */
    public void initHandlerMapping() {
        this.beanContainer = BeanContainer.getInstance();
        for(String s: beanContainer.controllerClassNames){
            Class<?> clazz = null;
            try {
                clazz = Class.forName(s);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String baseUrl = "";
            // 获取controller的url配置
            if(clazz.isAnnotationPresent(TXRequestMapping.class)){
                baseUrl = clazz.getAnnotation(TXRequestMapping.class).value();
            }
            // 获取method的url配置
            Method[] methods = clazz.getMethods();
            for(Method method: methods){
                if(!method.isAnnotationPresent(TXRequestMapping.class)){
                    continue;
                }
                TXRequestMapping requestMapping = method.getAnnotation(TXRequestMapping.class);
                String url = ("/"+baseUrl+"/"+requestMapping.value()).replaceAll("[/]+","/");
                mappings.put(url, method);
                System.out.println("mapped:"+url+", "+method);
            }


        }


    }

}
