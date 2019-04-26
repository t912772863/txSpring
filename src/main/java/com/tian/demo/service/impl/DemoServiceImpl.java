package com.tian.demo.service.impl;

import com.tian.demo.service.IDemoService;
import com.tian.txspring.webmvc.annotation.TXPostConstruct;
import com.tian.txspring.webmvc.annotation.TXService;

/**
 * Created by tianxiong on 2019/3/16.
 */
@TXService
public class DemoServiceImpl implements IDemoService {
    /**
     * 测试所有对象初始化完成后, 调用该注解标记的方法功能.
     */
    @TXPostConstruct
    private void init(){
        System.out.println("all bean had init. so methods that signed by \"TXPostConstruct\" is running");
    }

    public String get(String name) {
        System.out.println("method get dome.");
        return "demoServiceImpl: "+name;
    }
}
