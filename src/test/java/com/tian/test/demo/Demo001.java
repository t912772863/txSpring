package com.tian.test.demo;

import com.tian.txspring.webmvc.util.ReflectionUtil;

import java.util.Map;

/**
 * Created by tianxiong on 2019/3/21.
 */
public class Demo001 {
    private String temp;
    private String name;

    public String hello(){
        System.out.println("i am class Demo001");
        return "i am class Demo001";
    }

    public void doWithNotProxy(){
        System.out.println("do some thing with not proxy");
    }

    public static void main(String[] args) {
        Demo001 demo001 = new Demo001();
        demo001.temp = "test";
        Map<String, Object> map = ReflectionUtil.getFiledValues(demo001,false);
        System.out.println(map);
    }
}
