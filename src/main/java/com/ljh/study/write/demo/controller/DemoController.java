package com.ljh.study.write.demo.controller;

import com.ljh.study.write.spring.annotation.DHController;
import com.ljh.study.write.spring.annotation.DHRequestMapping;
import com.ljh.study.write.spring.annotation.DHRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 使用自定义注解的controller
 * @author: Jh Lee
 * @create: 2019-03-26 16:52
 **/
@DHController
@DHRequestMapping
public class DemoController {


    @DHRequestMapping("/query")
    public void queryName(HttpServletRequest req, HttpServletResponse resp,
                          @DHRequestParam("name") String name){
        String result = "My name is " + name;
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
