package com.tian.test.demo;

import com.tian.txspring.webmvc.annotation.TXAspect;
import com.tian.txspring.webmvc.annotation.TXPointCut;
import com.tian.txspring.webmvc.aop.AbsMethodAdvance;

/**
 * Created by tianxiong on 2019/3/21.
 */
@TXAspect
public class TestAspect extends AbsMethodAdvance {
    @TXPointCut("com.tian.test.demo.Demo001_hello")
    public void testAspect(){

    }

    public void doBefore() {
        System.out.println("this is before.");

    }

    public void doAfter() {
        System.out.println("this is after.");
    }
}
