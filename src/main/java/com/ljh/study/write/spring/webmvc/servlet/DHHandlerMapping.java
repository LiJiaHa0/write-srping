package com.ljh.study.write.spring.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @description: mapping映射
 * @author: Jh Lee
 * @create: 2019-04-22 20:52
 **/
public class DHHandlerMapping {

    //保存方法对应的实例
    private Object controller;

    //保存实例对应的方法
    private Method method;

    //保存URL的正则表达式匹配
    private Pattern pattern;

    public DHHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
