package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 类比@RequestMapping注解
 * Created by tianxiong on 2019/3/16.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TXRequestMapping {
    String value() default "";
}
