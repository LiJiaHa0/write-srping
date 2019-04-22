package com.ljh.study.write.spring.webmvc.servlet;

import java.util.Map;

/**
 * @description: 视图
 * @author: Jh Lee
 * @create: 2019-04-22 09:42
 **/
public class DHModelAndView {

    private String viewName;

    private Map<String,?> model;

    public DHModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public DHModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getMap() {
        return model;
    }
}
