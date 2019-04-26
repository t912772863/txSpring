package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 类比spring的PostConstruct注解, 在所有的类型初始化完成后调用该注解标记的方法
 * Created by tianxiong on 2019/4/26.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TXPostConstruct {
}
