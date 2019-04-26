package com.tian.txspring.webmvc.annotation;

import java.lang.annotation.*;

/**
 * 模拟ResponseBody注解,对返回的结果转成json
 * Created by tianxiong on 2019/4/26.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TXResponseBody {
}
