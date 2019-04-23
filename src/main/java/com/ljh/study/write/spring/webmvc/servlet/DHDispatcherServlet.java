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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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

    //保存参数适配器，一个handler对应一个adapter
    private Map<DHHandlerMapping,DHHandlerAdapter> handlerAdapters = new HashMap<DHHandlerMapping,DHHandlerAdapter>();

    private List<DHViewResolver> viewResolvers = new ArrayList<DHViewResolver>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doDispatch(req, resp);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp){
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        DHHandlerMapping handler = getHandler(req);
        //2、准备调用前的参数
        DHHandlerAdapter ha = getHandlerAdapter(handler);
        //3、真正的调用方法，返回ModelAndView存储了要传页面上的值，和页面模版的名称
        DHModelAndView mv = ha.handler(req, resp, handler);

        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, DHModelAndView mv) {
    }


    /**
     * 获取一个请求相对应的参数适配器
     * @param handler
     * @return
     */
    private DHHandlerAdapter getHandlerAdapter(DHHandlerMapping handler) {
        if(null == handler) return null;
        DHHandlerAdapter dhHandlerAdapter = this.handlerAdapters.get(handler);
        if(dhHandlerAdapter.supports(handler)){
            return dhHandlerAdapter;
        }
        return null;
    }

    /**
     * 通过request中的url获取相对应的handler
     * @param request
     * @return
     */
    private DHHandlerMapping getHandler(HttpServletRequest request){
        //获取请求的url
        String requestURI = request.getRequestURI();
        //获取上下文路径
        String contextPath = request.getContextPath();
        //把重复输入的////替换为一个/
        requestURI = requestURI.replace(contextPath, "").replaceAll("/+", "/");
        //遍历handlerMappings匹配跟请求中相同的URL正则表达式
        for(DHHandlerMapping handlerMapping : this.handlerMappings){
            try{
                Matcher matcher = handlerMapping.getPattern().matcher(requestURI);
                if(!matcher.matches()) continue;
                return handlerMapping;
            }catch (Exception e){
                throw e;
            }
        }
        return null;
    }

    /**
     * /servlet初始化
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化application
        context = new DHApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        initStrategies(context);
    }

    /**
     * 初始化spring九大组件
     * @param context
     */
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

        for(DHHandlerMapping handlerMapping: this.handlerMappings){
            this.handlerAdapters.put(handlerMapping,new DHHandlerAdapter());
        }
    }

    /**
     * 初始化handlerMapping
     * @param context
     */
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
