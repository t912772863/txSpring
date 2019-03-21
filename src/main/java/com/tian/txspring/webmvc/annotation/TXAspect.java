package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 类比@Aspect注解,实现aop功能
 * Created by tianxiong on 2019/3/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TXAspect {
    String value() default "";
}
