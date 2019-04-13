package com.ljh.study.write.spring.annotation;

import java.lang.annotation.*;

/**
 * @description: 自定义@RequestParam
 * @author: Jh Lee
 * @create: 2019-03-26 21:12
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DHRequestParam {

    String value() default "";
}
