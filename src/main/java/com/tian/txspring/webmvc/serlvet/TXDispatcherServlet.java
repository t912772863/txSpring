package com.tian.txspring.webmvc.serlvet;

import com.alibaba.fastjson.JSONObject;
import com.tian.txspring.webmvc.annotation.TXResponseBody;
import com.tian.txspring.webmvc.aop.AopBeanContainer;
import com.tian.txspring.webmvc.config.ConfigCenter;
import com.tian.txspring.webmvc.handler.mapping.HandlerMapping;
import com.tian.txspring.webmvc.handler.param.ParseFormParam;
import com.tian.txspring.webmvc.ioc.BeanContainer;
import com.tian.txspring.webmvc.util.JarLoaderUtil;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static com.tian.txspring.webmvc.util.StringUtils.lowerFirstCase;

/**
 * 模拟spring的DispatcherServlet写一个自己的实现
 * Created by tianxiong on 2019/3/16.
 */
public class TXDispatcherServlet extends HttpServlet {
    private Logger logger = Logger.getLogger(TXDispatcherServlet.class.getName());
    /**
     * 全局的配置
     */
    private ConfigCenter configCenter = ConfigCenter.getInstance();
    /**
     * bean容器
     */
    private BeanContainer beanContainer = BeanContainer.getInstance();
    /**
     * aop bean容器
     */
    private AopBeanContainer aopBeanContainer = AopBeanContainer.getInstance();
    /**
     * 保存请求url映射关系
     */
    private HandlerMapping handlerMapping = HandlerMapping.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        try{
            doDispatch(req, resp);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(this.handlerMapping.mappings.isEmpty()){
            return;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+", "/");
        if(!this.handlerMapping.mappings.containsKey(url)){
            resp.getWriter().write("404 Not Found.");
        }
        Method method = handlerMapping.mappings.get(url);

        // 保存参数值
        Object[] parameterValues = new ParseFormParam().parse(req,resp,method);

        String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
        // 利用反射机制调用
        try {
            Object result = method.invoke(this.aopBeanContainer.nameBean.get(beanName), parameterValues);
            logger.info("method "+method.getName()+" return "+result);
            afterProcessResult(method, result, req, resp);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * controller层方法调用完成后对方法的返回结果后置处理.
     * @param method 方法对象
     * @param result 方法返回结果
     * @param req
     * @param resp
     */
    private void afterProcessResult(Method method, Object result, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 如果有@TXResponseBody注解, 则转成json,用resp回写.
        if(method.getAnnotation(TXResponseBody.class) != null){
            String jsonStr = JSONObject.toJSONString(result);
            resp.getWriter().write(jsonStr);
        }
    }

    /**
     * 加载框架的初始化方法
     */
    public void init(ServletConfig config){
        // 加载配置
        configCenter.doLoadContextConfig(config.getInitParameter("contextConfigLocation"));
        // 加载依赖jar文件
        doLoadJar();
        // 扫描所有相关的类
        beanContainer.doScanner(configCenter.getValue("scan_package"));
        // 初始化所有相关的类,并且将所有扫到的类放到IOC容器中.
        beanContainer.doInstance();
        // 自动注入
        beanContainer.doAutowired();
        // 切面功能初始化, 也就是针对切入点生成增强类
        aopBeanContainer.doAopInstance();
        // 增强类的注入
        aopBeanContainer.doAutowired();
        // 调用txPostConstruct注解的方法
        beanContainer.doPostConstruct();
        // 初始化HandlerMapping
        handlerMapping.initHandlerMapping();

    }

    private void doLoadJar() {
        // 先加载要依赖的jar文件
        JarLoaderUtil.loadJarPath(this.getClass().getResource("/").getPath());
    }

}
