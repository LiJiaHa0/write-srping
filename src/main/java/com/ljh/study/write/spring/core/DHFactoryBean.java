package com.ljh.study.write.spring.core;

/**
 * @description: 自定义FactoryBean
 * @author: Jh Lee
 * @create: 2019-04-11 21:51
 **/
public interface DHFactoryBean {

    /**
     * 通过beanName获取Bean
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    /**
     * 通过className获取Bean
     * @param className
     * @return
     * @throws Exception
     */
    Object getBean(Class<?> className) throws Exception;
}
