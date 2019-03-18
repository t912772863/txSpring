package com.tian.demo.service.impl;

import com.tian.demo.service.IDemoService;
import com.tian.txspring.webmvc.annotation.TXService;

/**
 * Created by tianxiong on 2019/3/16.
 */
@TXService
public class DemoServiceImpl implements IDemoService {
    public String get(String name) {
        return "demoServiceImpl: "+name;
    }
}
