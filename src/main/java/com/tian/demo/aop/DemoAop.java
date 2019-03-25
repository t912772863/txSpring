package com.tian.demo.aop;

import com.tian.txspring.webmvc.annotation.TXAspect;
import com.tian.txspring.webmvc.annotation.TXPointCut;
import com.tian.txspring.webmvc.aop.AbsMethodAdvance;

/**
 * Created by tianxiong on 2019/3/24.
 */
@TXAspect
public class DemoAop extends AbsMethodAdvance {
    @TXPointCut("com.tian.demo.mvc.action.DemoAction_removed")
    public void temp(){

    }

    public void doBefore() {
        System.out.println("====> this is before.");
    }

    public void doAfter() {
        System.out.println("====> this is after.");
    }
}
