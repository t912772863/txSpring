package com.tian.demo.mvc.action;

import com.tian.demo.service.IDemoService;
import com.tian.txspring.webmvc.annotation.TXAutowired;
import com.tian.txspring.webmvc.annotation.TXController;
import com.tian.txspring.webmvc.annotation.TXRequestMapping;
import com.tian.txspring.webmvc.annotation.TXRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by tianxiong on 2019/3/16.
 */
@TXController
@TXRequestMapping
public class DemoAction {
    @TXAutowired
    private IDemoService demoService;

    @TXRequestMapping("/remove.json")
    public void removed(HttpServletRequest request, HttpServletResponse response, @TXRequestParam("name") String name){
        String result = demoService.get(name);
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}