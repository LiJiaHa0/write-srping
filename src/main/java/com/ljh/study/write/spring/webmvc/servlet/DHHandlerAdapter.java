package com.ljh.study.write.spring.webmvc.servlet;

import com.ljh.study.write.spring.annotation.DHRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 参数适配器
 * @author: Jh Lee
 * @create: 2019-04-23 10:56
 **/
public class DHHandlerAdapter {

    public boolean supports(Object handler){
        return handler instanceof DHHandlerMapping;
    }

    public DHModelAndView handler(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {
        DHHandlerMapping dhHandlerMapping = (DHHandlerMapping)handler;

        //把方法形参列表和request的参数列表所在的顺序一一对应
        Map<String,Integer> paramIndexMapping = new HashMap<>();

        //提取方法中加了注解的参数
        //把方法上的注解拿到，得到的是一个二维数组
        //因为一个参数可以有多个注解，而一个方法又有多个参数
        Annotation[][] parameterAnnotations = dhHandlerMapping.getMethod().getParameterAnnotations();
        for(int i = 0; i < parameterAnnotations.length; i++){
            for(Annotation a : parameterAnnotations[i]){
                //判断是否加了DHRequestParam的注解
                if(a instanceof DHRequestParam){
                    //获取注解中配置的参数名
                    String paramName = ((DHRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //获取参数列表中的request和response参数并存入paramIndexMapping
        Class<?>[] parameterTypes = dhHandlerMapping.getMethod().getParameterTypes();
        for(int i = 0; i < parameterTypes.length; i++){
            Class<?> parameterType = parameterTypes[i];
            if(parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class){
                paramIndexMapping.put(parameterType.getName(),i);
            }
        }
        //获得方法的形参列表
        Map<String,String[]> params = request.getParameterMap();
        //实参列表
        Object [] paramValues = new Object[parameterTypes.length];

        for(Map.Entry<String,String[]> entry : params.entrySet()){
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");
            if(paramIndexMapping.containsKey(entry.getKey()))continue;
            int index = paramIndexMapping.get(entry.getKey());
            paramValues[index] = caseStringValue(value,parameterTypes[index]);
        }
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int resIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[resIndex] = response;
        }
        Object result = dhHandlerMapping.getMethod().invoke(dhHandlerMapping.getController(), paramValues);
        if(null == result || result instanceof Void) return null;
        boolean isModelAndView = dhHandlerMapping.getMethod().getReturnType() == DHModelAndView.class;
        if(isModelAndView){
            return (DHModelAndView)result;
        }
        return null;
    }

    /**
     * 参数类型转换
     * @param value
     * @param parameterType
     * @return
     */
    private Object caseStringValue(String value, Class<?> parameterType) {

        if(String.class == parameterType){
            return value;
        }else if(Integer.class == parameterType){
            return Integer.valueOf(value);
        }else if(Double.class == parameterType){
            return Double.valueOf(value);
        }else if(Float.class == parameterType){
            return Float.valueOf(value);
        }else{
            if(value != null){
                return value;
            }
            return null;
        }
    }
}
