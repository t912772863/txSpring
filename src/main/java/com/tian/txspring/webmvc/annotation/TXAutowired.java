package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 类比@Autowired注解
 * Created by tianxiong on 2019/3/16.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TXAutowired {
    String value() default "";
}
