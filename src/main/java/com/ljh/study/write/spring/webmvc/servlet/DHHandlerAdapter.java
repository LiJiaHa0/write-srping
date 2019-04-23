package com.ljh.study.write.spring.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 参数适配器
 * @author: Jh Lee
 * @create: 2019-04-23 10:56
 **/
public class DHHandlerAdapter {

    public boolean supports(Object handler){
        return handler instanceof DHHandlerMapping;
    }

    public DHModelAndView handler(HttpServletRequest request, HttpServletResponse response,Object handler){

        return null;
    }
}
