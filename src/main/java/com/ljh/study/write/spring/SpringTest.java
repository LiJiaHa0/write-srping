package com.ljh.study.write.spring;

import com.ljh.study.write.spring.context.DHApplicationContext;

/**
 * @description: 测试类
 * @author: Jh Lee
 * @create: 2019-04-14 14:49
 **/
public class SpringTest {

    public static void main(String[] args) {
        DHApplicationContext dhApplicationContext = new DHApplicationContext("classpath:application.properties");
        dhApplicationContext.getBean("demoController");
    }
}
