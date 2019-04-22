package com.ljh.study.write.spring.webmvc.servlet;

import com.ljh.study.write.spring.context.DHApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 自定义的servlet
 * @author: Jh Lee
 * @create: 2019-04-22 09:38
 **/
public class DHDispatcherServlet extends HttpServlet {

    private DHApplicationContext context;

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

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
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for(String beanName : beanDefinitionNames){

        }
    }

    private void initThemeResolver(DHApplicationContext context) {
    }

    private void initLocaleResolver(DHApplicationContext context) {
    }

    private void initMultipartResolver(DHApplicationContext context) {
    }


}
