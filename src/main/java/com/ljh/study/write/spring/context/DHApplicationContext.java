package com.ljh.study.write.spring.context;

import com.ljh.study.write.spring.annotation.DHAutowired;
import com.ljh.study.write.spring.annotation.DHController;
import com.ljh.study.write.spring.annotation.DHService;
import com.ljh.study.write.spring.beans.DHBeanWrapper;
import com.ljh.study.write.spring.beans.config.DHBeanDefinition;
import com.ljh.study.write.spring.beans.factory.DHBeanFactory;
import com.ljh.study.write.spring.beans.support.DHBeanDefinitionReader;
import com.ljh.study.write.spring.beans.support.DHDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    //通过构造方法把我们配置文件的路径赋值
    public DHApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;
        try {
            //调用激活刷新IOC容器以及DI的操作
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //单例的IOC容器缓存
    private Map<String,Object> singletonObjects = new ConcurrentHashMap<String, Object>();

    //通用的IOC容器
    private Map<String,DHBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, DHBeanWrapper>();


    @Override
    protected void refresh() throws Exception {
        //1.定位，定位配置文件中的指定包下面的全部的类
        reader = new DHBeanDefinitionReader(this.configLoactions);
        //2.加载指定包下面的全部的类，把他们封装成BeanDefinition
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

    //通过遍历通过第二部扫描到的类并且包装成的BeanDefinition的List集合，然后注册到伪IOC容器中
    private void doRegisterBeanDefinition(List<DHBeanDefinition> dhBeanDefinitions) throws Exception {
        //遍历扫描到的类名把这些类存放到伪IOC容器中
        for (DHBeanDefinition dhBeanDefinition : dhBeanDefinitions) {
            if(super.beanDefinitionMap.containsKey(dhBeanDefinition.getFactoryBeanName())){
                throw new Exception("The bean:" + dhBeanDefinition.getFactoryBeanName() + " already exist");
            }
            super.beanDefinitionMap.put(dhBeanDefinition.getFactoryBeanName(),dhBeanDefinition);
        }
    }


    public Object getBean(String beanName) {
        //通过beanName当成key在我们伪IOC容器中查找相对应的DHBeanDefinition
        DHBeanDefinition dhBeanDefinition = this.beanDefinitionMap.get(beanName);
        //然后通过DHBeanDefinition里面的bean的权限定名进行反射实例化
        Object instance =  instantiateBean(beanName, dhBeanDefinition);
        //把我们通过instantiateBean实例化后的对象进行包装成DHBeanWrapper
        DHBeanWrapper dhBeanWrapper = new DHBeanWrapper(instance);

        //2、拿到BeanWrapper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,dhBeanWrapper);
        //注入
        populateBean(beanName,new DHBeanDefinition(),dhBeanWrapper);

        return instance;
    }

    private void populateBean(String beanName, DHBeanDefinition dhBeanDefinition, DHBeanWrapper dhBeanWrapper) {
        //从DHBeanWrapper获取实例
        Object wrappedInstance = dhBeanWrapper.getWrappedInstance();
        //从DHBeanWrapper获取实例的Class对象
        Class<?> wrappedClass = dhBeanWrapper.getWrappedClass();
        //判断这个Class对象是否加了DHController、DHService注解，加了我们才进行依赖注入
        if(!(wrappedClass.isAnnotationPresent(DHController.class) || wrappedClass.isAnnotationPresent(DHService.class))){
            return;
        }else{
            //遍历这个实例所有的属性
            for (Field field : wrappedClass.getFields()) {
                //判断属性是否加了DHAutowired注解
                if(!field.isAnnotationPresent(DHAutowired.class)){
                    continue;
                }
                //获取注解信息
                DHAutowired annotation = field.getAnnotation(DHAutowired.class);
                //拿到DHAutowired的value值（我们指定的beanName）
                String annotationValue = annotation.value().trim();
                //如果没加就是默认的属性类名
                if("".equals(annotationValue)){
                    annotationValue = field.getType().getName();
                }
                //因为我们的属性一般都是设置为private，所以我们这里把自己设置为强制访问
                field.setAccessible(true);
                try {
                    field.set(wrappedInstance,this.factoryBeanInstanceCache.get(annotationValue).getWrappedInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //把我们包装类DHBeanDefinition中存放的类信息进行实例化
    private Object instantiateBean(String beanName, DHBeanDefinition dhBeanDefinition) {
        //从DHBeanDefinition获取我们伪IOC容器中的Bean的类名
        String beanClassName = dhBeanDefinition.getBeanClassName();
        Object instance = null;
        try {
            //判断从单例IOC容器缓存中是否存在该bean
            if(this.singletonObjects.containsKey(beanClassName)){
                instance = this.singletonObjects.get(beanClassName);
            }else{
                //不存在则用反射获取实例并存在单例IOC容器（默认所有的实例都是单例）
                Class<?> aClass = Class.forName(beanClassName);
                instance = aClass.newInstance();
                //一个是包名+类名为key
                this.singletonObjects.put(beanClassName,instance);
                //一个是类名首字母小写为key
                this.singletonObjects.put(dhBeanDefinition.getFactoryBeanName(),instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }
}
