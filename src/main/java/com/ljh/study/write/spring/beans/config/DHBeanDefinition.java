package com.ljh.study.write.spring.beans.config;

import lombok.Data;

/**
 * @description: 自定义IOC容器的实例
 * @author: Jh Lee
 * @create: 2019-04-11 22:18
 **/
@Data
public class DHBeanDefinition {

    private String beanClassName;

    private boolean lazyInit = false;

    private String factoryBeanName;
}
