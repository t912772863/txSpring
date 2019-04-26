package com.tian.txspring.webmvc.aop;

import com.tian.txspring.webmvc.util.StringUtils;
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
    /**
     * 是否前置拦截
     */
    private boolean doBefore = true;
    /**
     * 是否后置拦截
     */
    private boolean doAfter = true;

    public boolean isDoBefore() {
        return doBefore;
    }

    public void setDoBefore(boolean doBefore) {
        this.doBefore = doBefore;
    }

    public boolean isDoAfter() {
        return doAfter;
    }

    public void setDoAfter(boolean doAfter) {
        this.doAfter = doAfter;
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
        if(StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName()) && doBefore){
            doBefore();
        }
        // 执行拦截的方法
        result = proxy.invokeSuper(obj,args);
        if(StringUtils.isNotBlank(proxyMethod) && proxyMethod.equals(method.getName()) && doAfter){
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
