package com.ljh.study.write.spring.context;

import com.ljh.study.write.spring.beans.factory.DHBeanFactory;
import com.ljh.study.write.spring.beans.support.DHDefaultListableBeanFactory;

/**
 * @description: 自定义applicationContext
 * @author: Jh Lee
 * @create: 2019-04-11 22:07
 **/
public class DHApplicationContext extends DHDefaultListableBeanFactory implements DHBeanFactory {


    public Object getBean(String beanName) {
        return null;
    }

    @Override
    protected void refresh() {
        super.refresh();
    }
}
