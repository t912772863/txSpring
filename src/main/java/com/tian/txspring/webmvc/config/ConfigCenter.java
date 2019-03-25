package com.tian.txspring.webmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by tianxiong on 2019/3/24.
 */
public class ConfigCenter {
    /**
     * 全局的配置
     */
    private Properties contextConfig = new Properties();
    private static ConfigCenter instance = new ConfigCenter();
    private ConfigCenter(){}

    public static ConfigCenter getInstance(){
        return instance;
    }

    public void doLoadContextConfig(String configPath){
        // 拿到spring配置文件路径, 读取文件中的所有内容
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configPath);
        //
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Properties getContextConfig(){
        return this.contextConfig;
    }

    public String getValue(String key){
        return this.contextConfig.getProperty(key);
    }
}
