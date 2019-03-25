package com.tian.txspring.webmvc.serlvet;

import com.tian.txspring.webmvc.annotation.*;
import com.tian.txspring.webmvc.aop.AbsMethodAdvance;
import com.tian.txspring.webmvc.aop.AopBeanContainer;
import com.tian.txspring.webmvc.config.ConfigCenter;
import com.tian.txspring.webmvc.handler.mapping.HandlerMapping;
import com.tian.txspring.webmvc.ioc.BeanContainer;
import com.tian.txspring.webmvc.util.JarLoaderUtil;
import com.tian.txspring.webmvc.util.ReflectionUtil;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.tian.txspring.webmvc.util.StringUtils.lowerFirstCase;

/**
 * 模拟spring的DispatcherServlet写一个自己的实现
 * Created by tianxiong on 2019/3/16.
 */
public class TXDispatcherServlet extends HttpServlet {
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
    /**
     * 所有要自动加载的类的全路径
     */
    private List<String> classNames = new ArrayList<String>();
    /**
     * ioc容器
     */
    private Map<String, Object> ioc = new HashMap<String, Object>();
    /**
     * 把ioc复制一份, 在解析切面的时候,用切面对象替换原有对象,以实现切面功能.
     */
    private Map<String, Object> iocProxy = new HashMap();
    /**
     * 存放代理类的集合
     */
    public ConcurrentHashMap<String,Object> proxyBeanMap = new ConcurrentHashMap<String, Object>();


    public void doGet(HttpServletRequest req, HttpServletResponse resp){
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        try{
            doDispatch(req, resp);
        }catch (Exception e){

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
        Map<String,String[]> params = req.getParameterMap();
        Method method = handlerMapping.mappings.get(url);
        // 获取方法的参数列表
        Class[] parameterTypes = method.getParameterTypes();
        // 获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        // 保存参数值
        Object[] parameterValues = new Object[parameterTypes.length];
        // 方法的参数列表
        for (int i = 0; i < parameterTypes.length; i++) {
            // 根据参数名称做某些处理
            Class paramTerType = parameterTypes[i];
            if(paramTerType == HttpServletRequest.class){
                // 参数类型已经明确,进行强转
                parameterValues[i] = req;
                continue;
            }else if(paramTerType == HttpServletResponse.class){
                parameterValues[i] = resp;
                continue;
            }else if(paramTerType == String.class){
                for(Map.Entry<String, String[]> param: parameterMap.entrySet()){
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s",",");
                    parameterValues[i] = value;
                }
            }
        }

        String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
        // 利用反射机制调用
        try {
            method.invoke(this.aopBeanContainer.nameBean.get(beanName), parameterValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
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
        // 增加类的注入
        aopBeanContainer.doAutowired();
        // 初始化HandlerMapping
        handlerMapping.initHandlerMapping();

    }

    private void doLoadJar() {
        // 先加载要依赖的jar文件
        JarLoaderUtil.loadJarPath(this.getClass().getResource("/").getPath());
    }

    /**
     * 切面功能初始化
     */
    private void doAopInstance() {
        // 先把前面生成的对象复制一份,查看有哪些方法是要增加的,生成增强类替换原对象
        iocProxy.putAll(ioc);
        // 获取到要代理的切面
        for(String s: classNames){
            Class<?> clazz = null;
            try {
                clazz = Class.forName(s);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(clazz.isAnnotationPresent(TXAspect.class)){
                Method[] methods = clazz.getMethods();
                for(Method method : methods){
                    if(method.isAnnotationPresent(TXPointCut.class)){
                        //找到切点
                        TXPointCut pointCut = (TXPointCut)method.getAnnotations()[0];
                        String pointCutStr = pointCut.value();
                        String[] pointCutArr = pointCutStr.split("_");
                        //被代理的方法名
                        String methodName = pointCutArr[1];
                        //根据切点 创建被代理对象
                        String beanName = pointCutArr[0].substring(pointCutArr[0].lastIndexOf(".")+1);
                        Object targeObj = ioc.get(lowerFirstCase(beanName));
                        //根据切面类创建代理者
                        AbsMethodAdvance proxyer = (AbsMethodAdvance)ReflectionUtil.newInstance(clazz);
                        //设置代理的方法
                        proxyer.setProxyMethodName(methodName);

                        Object object = proxyer.createProxyObject(targeObj);

                        if(object != null){
                            // 生成的代理类
                            proxyBeanMap.put(lowerFirstCase(targeObj.getClass().getSimpleName()),object);
                        }
                    }
                }
            }
        }


        for(String s: proxyBeanMap.keySet()){
            try {
               // 把那些代理的类, 用其代理实现替换掉原来的实现
                iocProxy.put(s,proxyBeanMap.get(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化请求路径映射.
     * 一般init开始的方法为初始化方法
     */
//    private void initHandlerMapping() {
//        if(ioc.isEmpty()){
//            return;
//        }
//        for(Map.Entry<String,Object> entry: ioc.entrySet()){
//            Class<?> clazz = entry.getValue().getClass();
//            if(!clazz.isAnnotationPresent(TXController.class)){
//                continue;
//            }
//            String baseUrl = "";
//            // 获取controller的url配置
//            if(clazz.isAnnotationPresent(TXRequestMapping.class)){
//               baseUrl = clazz.getAnnotation(TXRequestMapping.class).value();
//            }
//            // 获取method的url配置
//            Method[] methods = clazz.getMethods();
//            for(Method method: methods){
//                if(!method.isAnnotationPresent(TXRequestMapping.class)){
//                    continue;
//                }
//                TXRequestMapping requestMapping = method.getAnnotation(TXRequestMapping.class);
//                String url = ("/"+baseUrl+"/"+requestMapping.value()).replaceAll("[/]+","/");
//                handlerMapping.put(url, method);
//                System.out.println("mapped:"+url+", "+method);
//            }
//
//
//        }
//
//
//    }

    /**
     * 处理依赖注入逻辑
     */
    private void doAutowired() {
        if(iocProxy.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry: iocProxy.entrySet()){
            // 拿到实例对象中的所有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(TXAutowired.class)){
                    continue;
                }
                TXAutowired autowired = field.getAnnotation(TXAutowired.class);
                String beanName = autowired.value();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), iocProxy.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    /**
     * 通过反射, 实例化类
     */
    private void doInstance() {
        // 判断,有没有找到类
        if(classNames.isEmpty()){
            return;
        }
        for(String className: classNames){
            // 第一步拿到class对象, 接下来就可以反射
            try {
                Class<?> clazz = Class.forName(className);
                // 通过反射机制,拿到类的实例对象. 只加载带有定义注解的类
                if(clazz.isAnnotationPresent(TXController.class)){
                    Object instance = clazz.newInstance();
                    // 默认为类名首字母小写
                    String key = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(key, instance);

                }else if(clazz.isAnnotationPresent(TXService.class)){
                    // 如果没有配置,用类名小写
                    // 如果自己指定了, 则用指定的名字
                    // 把子类实现赋值给父类接口
                    TXService service = clazz.getAnnotation(TXService.class);
                    String beanName = service.value();
                    Object o = clazz.newInstance();
                    if(!"".equals(beanName)){
                        ioc.put(beanName, o);
                        continue;
                    }else {
                        ioc.put(lowerFirstCase(clazz.getSimpleName()), o);
                    }

                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> c: interfaces){
                        ioc.put(c.getName(), o);
                    }

                }else {
                    // 不用加载
                    continue;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 扫描指定路径包下的带有注解的class
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        // 拿到包外下所有的类
        URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classDir = new File(url.getFile());
        for(File file : classDir.listFiles()){
            // 递归遍历获取到class文件的名字
            if(file.isDirectory()){
                // 是文件夹则递归
                doScanner(scanPackage+"."+file.getName());
            }else {
                // 文件则转换一个名字保存下来
                String className = (scanPackage+"."+file.getName()).replace(".class", "");
                classNames.add(className);
            }

        }

    }

//    private void doLoadConfig(String contextConfigLocation) {
//        // 拿到spring配置文件路径, 读取文件中的所有内容
//        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
//        //
//        try {
//            contextConfig.load(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if(is != null){
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


}
