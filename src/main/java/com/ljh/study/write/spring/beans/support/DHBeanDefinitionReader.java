package com.ljh.study.write.spring.beans.support;

import com.ljh.study.write.spring.beans.config.DHBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description: 定位配置文件的核心类
 * @author: Jh Lee
 * @create: 2019-04-13 15:46
 **/
public class DHBeanDefinitionReader {

    //存放我们扫描到的所有类的类名
    private List<String> registerBeanClasses = new ArrayList<String>();

    //配置文件对象
    private Properties config = new Properties();

    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";

    //通过构造器初始化
    public DHBeanDefinitionReader(String ... locations){
        //通过URL定位找到其所对应的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            //使用properties文件进行加载配置
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //扫描配置文件中我们所配置的扫描的包地址
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    //扫描配置文件中我们所配置的扫描的包地址
    private void doScanner(String scanPackage) {
        //转换为文件路径，实际上就是把.替换为/就OK了
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        //递归遍历这个路径下的全部目录，是以.class文件结尾的就加入List集合中，如果是路径则继续扫描
        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else{
                if(!file.getName().endsWith(".class")){ continue;}
                String className = (scanPackage + "." + file.getName().replace(".class",""));
                registerBeanClasses.add(className);
            }
        }
    }

    //把配置文件中扫描到的所有配置信息转换为DHBeanDefinition包装类，方便IOC操作使用
    public List<DHBeanDefinition> loadBeanDefinitions(){
        List<DHBeanDefinition> result = new ArrayList<DHBeanDefinition>();
        try {
            for (String registerBeanClass : registerBeanClasses) {
                //通过我们扫描到的类名反射得到class对象然后通过doCreateBeanDefinition()方法包装成DHBeanDefinition加入一个包装类集合中
                Class<?> beanClass = Class.forName(registerBeanClass);
                //如果扫描到的是接口，我们暂时先不做处理（spring中如果是接口则把实现类包装）
                if(beanClass.isInterface())continue;
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //对我们需要注入的bean是需要的beanName以及这个bean的类名进行包装成DHBeanDefinition
    public DHBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        DHBeanDefinition dhBeanDefinition = new DHBeanDefinition();
        //设置IOC容器中的beanName
        dhBeanDefinition.setFactoryBeanName(factoryBeanName);
        //设置这个bean的类名，为了以后实例化使用
        dhBeanDefinition.setBeanClassName(beanClassName);
        return dhBeanDefinition;
    }

    //为了简化程序逻辑，就不做其他判断了，大家了解就OK
    //其实用写注释的时间都能够把逻辑写完了
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
