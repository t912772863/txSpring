package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by tianxiong on 2019/3/18.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TXBefore {
    /**
     * 全类名_方法名
     * @return
     */
    String value();
}
