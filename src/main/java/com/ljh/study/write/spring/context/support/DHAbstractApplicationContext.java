package com.ljh.study.write.spring.context.support;

/**
 * @description: 自定义AbstractApplicationContext
 * 主要为refresh方法刷新IOC容器
 * @author: Jh Lee
 * @create: 2019-04-11 22:09
 **/
public abstract class DHAbstractApplicationContext {

    //加入protected为受保护的，子类才能重写
    protected void refresh(){

    }
}
