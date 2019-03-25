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

    /**
     * 把首字母转小写工具类
     * @param simpleName
     * @return
     */
    public static String lowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        // 英文的字母大小写,相差32
        chars[0] += 32;
        return new String(chars);
    }
}
