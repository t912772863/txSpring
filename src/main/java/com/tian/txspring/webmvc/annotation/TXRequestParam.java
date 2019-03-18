package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * Created by tianxiong on 2019/3/16.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TXRequestParam {
    String value() default "";
}
