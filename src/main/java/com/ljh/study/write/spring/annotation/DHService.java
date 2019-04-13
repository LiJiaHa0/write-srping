package com.ljh.study.write.spring.annotation;

import java.lang.annotation.*;

/**
 * @description: 自定义Service注解
 * @author: Jh Lee
 * @create: 2019-03-26 15:10
 **/

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DHService {

    String value() default "";
}
