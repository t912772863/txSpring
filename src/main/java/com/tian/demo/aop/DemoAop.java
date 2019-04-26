package com.tian.demo.aop;

import com.tian.txspring.webmvc.annotation.TXAspect;
import com.tian.txspring.webmvc.annotation.TXPointCut;
import com.tian.txspring.webmvc.aop.AbsMethodAdvance;

/**
 * 测试自己实现的切面功能.
 * Created by tianxiong on 2019/3/24.
 */
@TXAspect
@TXPointCut("com.tian.demo.mvc.action.DemoAction_removed")
public class DemoAop extends AbsMethodAdvance {
    /**
     * 方法调用前调用
     */

    public void doBefore() {
        System.out.println("====> this is before.");
    }

    /**
     * 方法成功返回后调用
     */
    public void doAfter() {
        System.out.println("====> this is after.");
    }
}
