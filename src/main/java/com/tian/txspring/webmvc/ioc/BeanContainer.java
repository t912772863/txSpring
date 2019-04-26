package com.tian.txspring.webmvc.ioc;

import com.tian.txspring.webmvc.annotation.TXAutowired;
import com.tian.txspring.webmvc.annotation.TXController;
import com.tian.txspring.webmvc.annotation.TXService;
import com.tian.txspring.webmvc.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tian.txspring.webmvc.util.StringUtils.lowerFirstCase;

/**
 * Created by tianxiong on 2019/3/24.
 */
public class BeanContainer {
    /**
     * 配置的扫描包路径下, 需要由框架初始化的类全名.
     */
    public List<String> classNames = new ArrayList<String>();
    /**
     * controller类的类名集合
     */
    public List<String> controllerClassNames = new ArrayList<String>();
    /**
     * bean容器, key是类全名, value是类的实例化对象
     */
    public Map<String, Object> typeBean = new HashMap();
    /**
     * bean容器, key是注解的类名, 如果没有, 默认用的类名首字母小写
     */
    public Map<String, Object> nameBean = new HashMap();


    private BeanContainer (){}
    private static BeanContainer instance = new BeanContainer();
    public static BeanContainer getInstance(){
        return instance;
    }

    /**
     * 扫描指定路径包下的带有注解的class
     * @param scanPackage
     */
    public void doScanner(String scanPackage) {
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

    /**
     * 通过反射, 实例化类
     */
    public void doInstance() {
        for(String className: classNames){
            try {
                // 第一步拿到class对象, 接下来就可以反射
                Class<?> clazz = Class.forName(className);
                // 通过反射机制,拿到类的实例对象. 只加载带有定义注解的类
                if(clazz.isAnnotationPresent(TXController.class)){
                    instanceController(clazz);
                }else if(clazz.isAnnotationPresent(TXService.class)){
                    instanceService(clazz);
                }else {
                    // 不用加载
                    continue;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void instanceService(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        // 如果没有配置,用类名小写
        // 如果自己指定了, 则用指定的名字
        // 把子类实现赋值给父类接口
        TXService service = clazz.getAnnotation(TXService.class);
        Object o = clazz.newInstance();
        String nameKey = StringUtils.isNotBlank(service.value())?service.value():lowerFirstCase(clazz.getSimpleName());
        nameBean.put(nameKey, o);
        String typeKey = clazz.getName();
        typeBean.put(typeKey, o);

        Class<?>[] interfaces = clazz.getInterfaces();
        for(Class<?> c: interfaces){
            typeBean.put(c.getName(), o);
        }

    }

    private void instanceController(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        controllerClassNames.add(clazz.getName());

        Object instance = clazz.newInstance();
        // 默认为类名首字母小写
        TXController txController = clazz.getAnnotation(TXController.class);
        String nameKey = StringUtils.isNotBlank(txController.value())?txController.value():lowerFirstCase(clazz.getSimpleName());
        nameBean.put(nameKey, instance);
        String typeKey = clazz.getName();
        typeBean.put(typeKey, instance);
    }

    public <T> T getBean(Class clazz){
        return (T)typeBean.get(clazz.getName());
    }

    public <T> T getBean(String beanName){
        return (T)nameBean.get(beanName);
    }

    public void doAutowired() {
        for(Map.Entry<String,Object> entry: nameBean.entrySet()){
            // 拿到实例对象中的所有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
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
