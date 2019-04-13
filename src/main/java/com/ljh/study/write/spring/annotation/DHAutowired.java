package com.ljh.study.write.spring.annotation;

import java.lang.annotation.*;

/**
 * @description: 自定义Autowired注解
 * @author: Jh Lee
 * @create: 2019-03-26 16:15
 **/

@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DHAutowired {

    String value() default "";
}
