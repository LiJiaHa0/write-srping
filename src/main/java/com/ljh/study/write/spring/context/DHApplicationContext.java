package com.ljh.study.write.spring.context;

import com.ljh.study.write.spring.beans.config.DHBeanDefinition;
import com.ljh.study.write.spring.beans.factory.DHBeanFactory;
import com.ljh.study.write.spring.beans.support.DHBeanDefinitionReader;
import com.ljh.study.write.spring.beans.support.DHDefaultListableBeanFactory;

import java.util.List;
import java.util.Map;

/**
 * @description: 自定义applicationContext
 * @author: Jh Lee
 * @create: 2019-04-11 22:07
 **/
public class DHApplicationContext extends DHDefaultListableBeanFactory implements DHBeanFactory {


    //配置文件路径
    private String [] configLoactions;

    //定位配置文件
    private DHBeanDefinitionReader reader;


    @Override
    protected void refresh() throws Exception {
        //1.定位，定位配置文件
        reader = new DHBeanDefinitionReader(this.configLoactions);
        //2.加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<DHBeanDefinition> dhBeanDefinitions = reader.loadBeanDefinitions();

        //3.注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(dhBeanDefinitions);

        //4.把未设置延时加载的bean提前初始化
        doAutowired();
    }

    //把未设置延时加载的bean提前初始化
    private void doAutowired() {
        for (Map.Entry<String, DHBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }

    }

    //通过遍历通过第二部扫描到的类并且包装成的BeanDefinition的List集合，然后注册到IOC容器中
    private void doRegisterBeanDefinition(List<DHBeanDefinition> dhBeanDefinitions) throws Exception {
        for (DHBeanDefinition dhBeanDefinition : dhBeanDefinitions) {
            if(super.beanDefinitionMap.containsKey(dhBeanDefinition.getFactoryBeanName())){
                throw new Exception("The bean:" + dhBeanDefinition.getFactoryBeanName() + " already exist");
            }
            super.beanDefinitionMap.put(dhBeanDefinition.getFactoryBeanName(),dhBeanDefinition);
        }
    }


    public Object getBean(String beanName) {
        return null;
    }
}
