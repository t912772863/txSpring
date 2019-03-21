package com.tian.txspring.webmvc.util;

/**
 * Created by tianxiong on 2019/3/21.
 */
public class StringUtils {
    public static boolean isNotBlank(String str){
        if(str == null||str.trim().length()==0){
            return false;
        }
        return true;
    }

    public static boolean isBlank(String str){
        return !isNotBlank(str);
    }
}
