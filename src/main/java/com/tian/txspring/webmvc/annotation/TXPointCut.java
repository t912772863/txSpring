package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by tianxiong on 2019/3/21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface TXPointCut {
    /**
     * 全类名_方法名
     * @return
     */
    String value();
}
