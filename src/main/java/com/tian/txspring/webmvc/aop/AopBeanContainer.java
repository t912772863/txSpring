package com.tian.txspring.webmvc.aop;

import com.tian.txspring.webmvc.annotation.TXAspect;
import com.tian.txspring.webmvc.annotation.TXAutowired;
import com.tian.txspring.webmvc.annotation.TXPointCut;
import com.tian.txspring.webmvc.ioc.BeanContainer;
import com.tian.txspring.webmvc.util.ReflectionUtil;
import com.tian.txspring.webmvc.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tianxiong on 2019/3/24.
 */
public class AopBeanContainer {
    private BeanContainer beanContainer = BeanContainer.getInstance();
    /**
     * 配置的扫描包路径下, 需要由框架初始化的类全名.
     */
    private List<String> classNames = new ArrayList<String>();
    /**
     * bean容器, key是类全名, 发是类的实例化对象
     */
    public Map<String, Object> typeBean = new HashMap();
    /**
     * bean容器, key是注解的类名, 如果没有, 默认用的类名首字母小写
     */
    public Map<String, Object> nameBean = new HashMap();
    /**
     * 配置的切面类都有哪些
     */
    private List<String> aopClassNames = new ArrayList<String>();

    private AopBeanContainer (){}

    private static AopBeanContainer instance = new AopBeanContainer();

    public static AopBeanContainer getInstance(){
        return instance;
    }

    /**
     * 切面功能初始化
     */
    public void doAopInstance(){
        // 先把前面生成的对象复制一份,查看有哪些方法是要增加的,生成增强类替换原对象
        syncBeans();
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
                        Object targetObj = beanContainer.typeBean.get(pointCutArr[0]);
                        //根据切面类创建代理者
                        AbsMethodAdvance proxy = (AbsMethodAdvance) ReflectionUtil.newInstance(clazz);
                        //设置代理的方法
                        proxy.setProxyMethodName(methodName);

                        Object object = proxy.createProxyObject(targetObj);


                        if(object != null){
                            // 生成的代理类
                            String nameKey = StringUtils.lowerFirstCase(pointCutArr[0].substring(pointCutArr[0].lastIndexOf(".")+1));
                            nameBean.put(nameKey, object);
                            typeBean.put(pointCutArr[0], object);

                            Class<?>[] interfaces = clazz.getInterfaces();
                            for(Class<?> c: interfaces){
                                typeBean.put(c.getName(), object);
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 把ioc初始的bean复制一分给aop容器
     */
    private void syncBeans() {
        this.classNames.addAll(BeanContainer.getInstance().classNames);
        this.nameBean.putAll(BeanContainer.getInstance().nameBean);
        this.typeBean.putAll(BeanContainer.getInstance().typeBean);
    }

    /**
     * 处理依赖注入逻辑.
     * 分两步, 第一步先注入对象本身的依赖
     * 第二步再注入增的的aop依赖
     */
    public void doAutowired() {
        for(Map.Entry<String,Object> entry: nameBean.entrySet()){
            // 拿到实例对象中的所有属性
            Field[] fields = entry.getValue().getClass().getSuperclass().getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(TXAutowired.class)){
                    continue;
                }
                TXAutowired autowired = field.getAnnotation(TXAutowired.class);
                field.setAccessible(true);
                String beanName = autowired.value();
                try{
                    if(StringUtils.isNotBlank(beanName)){
                        // 按名字注入
                        field.set(entry.getValue(), nameBean.get(beanName));
                    }else {
                        // 按类型注入
                        field.set(entry.getValue(), typeBean.get(field.getType().getName()));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

        }

    }

}
