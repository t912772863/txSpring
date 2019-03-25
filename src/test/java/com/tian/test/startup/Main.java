package com.tian.test.startup;

import com.tian.test.demo.Demo001;
import com.tian.txspring.webmvc.common.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试自己实现的aop功能,初始化,并动态代理了一个类的指定方法
 * Created by tianxiong on 2019/3/21.
 */
public class Main {
    public static void main(String[] args){
        //模拟容器初始化
        ApplicationContext applicationContext = new ApplicationContext();
        ConcurrentHashMap<String,Object> proxyBeanMap = ApplicationContext.proxyBeanMap;
        //生成代理对象，默认为该类名的小写
        Demo001 test =(Demo001)proxyBeanMap.get("demo001");
        test.hello();

    }
}
