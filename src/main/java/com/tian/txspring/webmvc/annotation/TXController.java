package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 实现一个自定义的注解,类比@Controller注解
 * Created by tianxiong on 2019/3/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TXController {
    String value() default "";
}
