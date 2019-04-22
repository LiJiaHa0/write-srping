package com.ljh.study.write.spring.beans.support;

import com.ljh.study.write.spring.beans.config.DHBeanDefinition;
import com.ljh.study.write.spring.context.support.DHAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 自定义DefaultListableBeanFactory、
 * 默认的IOC初始化的实现
 * @author: Jh Lee
 * @create: 2019-04-11 22:14
 **/
public class DHDefaultListableBeanFactory extends DHAbstractApplicationContext {

    //存储注册信息的IOC容易这是最主要的IOC容器，Map为ConcurrentHashMap类型主要为了线程安全
    protected final Map<String, DHBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
}
