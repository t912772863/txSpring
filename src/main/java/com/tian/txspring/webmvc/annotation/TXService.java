package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 类比@Service注解
 * Created by tianxiong on 2019/3/16.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TXService {
    String value() default "";
}
