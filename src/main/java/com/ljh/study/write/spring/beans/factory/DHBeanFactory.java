package com.ljh.study.write.spring.beans.factory;

/**
 * @description: 自定义BeanFactory
 * 单例工厂的顶层设计
 * @author: Jh Lee
 * @create: 2019-04-11 21:51
 **/
public interface DHBeanFactory {

    //通过beanName从IOC容器中获取bean实例
    Object getBean(String beanName);
}
