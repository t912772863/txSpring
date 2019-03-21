package com.tian.txspring.webmvc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by tianxiong on 2019/3/21.
 */
public class ClassUtils {
    /**
     * 获取类加载器
     * @return
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类,选择是否默认初始化
     * @param className
     * @param initialized
     * @return
     */
    public static Class<?> loadClass(String className, boolean initialized){
        Class<?> clazz;
        try {
            clazz = Class.forName(className, initialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return clazz;
    }

    /**
     * 加载类, 并默认初始化
     * @param name
     * @return
     */
    public static Class<?> loadClass(String name){
        return loadClass(name, true);
    }

    public static Set<Class<?>> getClassSet(String packageName) throws IOException {
        Set<Class<?>> classSet = new HashSet();
        Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
        // Enumeration类似于一个迭代器
        while (urls.hasMoreElements()){
            URL url = urls.nextElement();
            if(url != null){
                // 获得URL的协议
                String protocol = url.getProtocol();
                if(protocol.equals("file")){
                    // 转码
                    String packagePath = URLDecoder.decode(url.getFile(),"UTF-8");
                    addClass(classSet, packagePath, packageName);
                }else if(protocol.equals("jar")){
                    // 解析jar文件
                    JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                    if(jarURLConnection != null){
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if(jarFile != null){
                            Enumeration<JarEntry> jarEntries = jarFile.entries();
                            while (jarEntries.hasMoreElements()){
                                JarEntry jarEntry = jarEntries.nextElement();
                                String jarEntryName = jarEntry.getName();
                                if(jarEntryName.endsWith(".class")){
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                                    doAddClass(classSet, className);
                                }

                            }
                        }

                    }

                }
            }

        }
        return classSet;

    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> clazz = loadClass(className, false);
        classSet.add(clazz);
    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        final File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isFile() && file.getName().endsWith(".class") || file.isDirectory();
            }
        });
        for(File file: files){
            String fileName = file.getName();
            if(file.isFile()){
                String className = fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtils.isNotBlank(packageName)){
                    className = packageName + "." + className;
                }
                // 添加
                doAddClass(classSet,className);
            }else {
                // 子目录
                String subPackagePath = fileName;
                if(StringUtils.isNotBlank(packagePath)){
                    subPackagePath = subPackagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if(StringUtils.isNotBlank(packageName)){
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet,subPackagePath,subPackageName);
            }

        }

    }

}
