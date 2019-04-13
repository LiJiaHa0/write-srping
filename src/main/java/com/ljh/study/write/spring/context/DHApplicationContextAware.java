package com.ljh.study.write.spring.context;

/**
 * @description: 自定义ApplicationContextAware
 *
 * 通过解耦的方式获得IOC容器的顶层设计
 * 后面将通过一个监听器去扫描所有的类，只要实现了此接口将自动调用setApplicationContext方法，从而将IOC容器注入到目标类中
 * @author: Jh Lee
 * @create: 2019-04-11 22:32
 **/
public interface DHApplicationContextAware {

    void setApplicationContext(DHApplicationContext applicationContext);
}
