package com.tian.test.demo;

import java.lang.annotation.*;

/**
 * Created by tianxiong on 2019/5/22.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsParam {
}
