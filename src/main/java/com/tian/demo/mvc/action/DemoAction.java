package com.tian.demo.mvc.action;

import com.tian.demo.mvc.dto.DemoParam1;
import com.tian.demo.service.IDemoService;
import com.tian.demo.service.impl.DemoServiceImpl;
import com.tian.txspring.webmvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by tianxiong on 2019/3/16.
 */
@TXController
@TXRequestMapping
public class DemoAction {
    /** 通过接口方式注入, 按类型 */
    @TXAutowired
    private IDemoService demoService;
    /** 通过实现类注入, 按名字*/
    @TXAutowired("demoServiceImpl")
    private DemoServiceImpl demoServiceImpl;

    /**
     * 测试自动注入, 切面功能
     * @param request
     * @param response
     * @param name
     */
    @TXRequestMapping("/remove.json")
    public void removed(HttpServletRequest request, HttpServletResponse response, @TXRequestParam("name") String name){

        String result = demoService.get(name);
        String result2 = demoServiceImpl.get(name);
        try {
            response.getWriter().write(result+" : "+result2);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试无切面功能
     * @param request
     * @param response
     * @throws IOException
     */
    @TXRequestMapping("/getName.json")
    public void getName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("this getName method is running.");
        response.getWriter().write("this getName method is running.");
    }

    /**
     * 对象类型的参数绑定
     * @param demoParam1
     * @return
     */
    @TXRequestMapping("/print.json")
    @TXResponseBody
    public DemoParam1 print(DemoParam1 demoParam1){
        System.out.println(demoParam1);
        return demoParam1;
    }

    /**
     * 普通类型参数绑定
     * @param age
     * @param name
     * @return
     */
    @TXRequestMapping("/print2.json")
    @TXResponseBody
    public String print2(Integer age, String name, Date birthday){
        System.out.println("age: "+age+", name: "+name);
        return "age: "+age+", name: "+name;
    }

}
