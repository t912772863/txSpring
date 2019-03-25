package com.tian.txspring.webmvc.aop;

import com.tian.txspring.webmvc.util.StringUtils;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by tianxiong on 2019/3/21.
 */
public abstract class AbsMethodAdvance implements MethodInterceptor {
    /**
     * 要被代理的对象
     */
    private Object targetObject;
    /**
     * 被代理的方法名
     */
    private String proxyMethodName;

    private BeanMap beanMap;

    public void setValue(String property,Object value) {
        beanMap.put(property, value);
    }

    public Object getValue(String property) {
        return beanMap.get(property);
    }

    public Object createProxyObject(Object target){
        this.targetObject = target;
        // 该类用于生成代理对象
        Enhancer enhancer = new Enhancer();
        // 设置目标类为代理对象的父类
        enhancer.setSuperclass(target.getClass());
        // 设置回调用对象本身
        enhancer.setCallback(this);
        return enhancer.create();
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result;
        String proxyMethod = getProxyMethodName();
        if(StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())){
            doBefore();
        }
        // 执行拦截的方法
        result = proxy.invokeSuper(obj,args);
        if(StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName())){
            doAfter();
        }
        return result;
    }

    public abstract void doBefore();

    public abstract void doAfter();

    public String getProxyMethodName() {
        return proxyMethodName;
    }
    public void setProxyMethodName(String proxyMethodName){
        this.proxyMethodName = proxyMethodName;
    }

}
