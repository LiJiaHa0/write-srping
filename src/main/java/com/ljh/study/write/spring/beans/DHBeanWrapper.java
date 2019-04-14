package com.ljh.study.write.spring.beans;

/**
 * @description: 自定义IOC容器中的实例
 * @author: Jh Lee
 * @create: 2019-04-14 13:25
 **/
public class DHBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public DHBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }


    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
