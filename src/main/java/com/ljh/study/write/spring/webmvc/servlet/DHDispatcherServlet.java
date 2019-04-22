package com.ljh.study.write.spring.webmvc.servlet;

import com.ljh.study.write.spring.annotation.DHController;
import com.ljh.study.write.spring.annotation.DHRequestMapping;
import com.ljh.study.write.spring.context.DHApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @description: 自定义的servlet
 * @author: Jh Lee
 * @create: 2019-04-22 09:38
 **/
@Slf4j
public class DHDispatcherServlet extends HttpServlet {

    private DHApplicationContext context;

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    //保存Controller与url的匹配
    private List<DHHandlerMapping> handlerMappings = new ArrayList<DHHandlerMapping>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp){

    }

    //servlet初始化
    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化application
        context = new DHApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        initStrategies(context);
    }

    //初始化spring九大组件
    private void initStrategies(DHApplicationContext context) {
        //多文件上传组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模版处理器
        initThemeResolver(context);

        //初始化handlerMappings(这里必须实现)
        initHandlerMappings(context);
        //初始化参数适配器（这里必须实现）
        initHandlerAdapters(context);
        
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化驶入转换器，必须事先
        initRequestToViewNameTranslator(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(DHApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(DHApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(DHApplicationContext context) {
    }

    private void initHandlerAdapters(DHApplicationContext context) {
    }

    private void initHandlerMappings(DHApplicationContext context) {
        //从application上下文中获取扫描到的全部Bean集合
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        //遍历获取到的全部bean集合，判断是否有@DHController和@DHRequestMapping注解
        for(String beanName : beanDefinitionNames){
            //通过beanName来获取bean的实例
            Object bean = context.getBean(beanName);
            //通过实例对象获取Class对象
            Class<?> beanClass = bean.getClass();
            //判断是否有@DHController注解
            if(!beanClass.isAnnotationPresent(DHController.class)){
                continue;
            }
            String baseUrl = "";
            //判断是否有@DHRequestMapping注解，获取url配置
            if(beanClass.isAnnotationPresent(DHRequestMapping.class)){
                DHRequestMapping requestMapping = beanClass.getAnnotation(DHRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            for(Method method : beanClass.getMethods()){
                if(!method.isAnnotationPresent(DHRequestMapping.class)){
                    continue;
                }
                DHRequestMapping annotation = method.getAnnotation(DHRequestMapping.class);
                //第一个替换时替换/demo/*替换为.*，第二个替换为把多写的//替换成一个/（demo//add）
                String url = ("/" + baseUrl + "/" + annotation.value().replaceAll("\\*", ".*")).replaceAll("/+","/");
                //把URL转换为正则
                Pattern compile = Pattern.compile(url);
                //保存到集合中
                this.handlerMappings.add(new DHHandlerMapping(bean,method,compile));
                log.info("Mapped: " + url + "->" + method);
            }
        }
    }

    private void initThemeResolver(DHApplicationContext context) {
    }

    private void initLocaleResolver(DHApplicationContext context) {
    }

    private void initMultipartResolver(DHApplicationContext context) {
    }


}
