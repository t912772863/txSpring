package com.tian.txspring.webmvc.handler.param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * 解析form表单形式提交的请求的参数
 * Created by tianxiong on 2019/3/25.
 */
public class ParseFormParam implements IParseParam {
    public Object[] parse(HttpServletRequest request, HttpServletResponse response,Method method) {

        // 先拿到该方法的参数类型
        Class[] paramTypes = method.getParameterTypes();
        Object[] result = new Object[paramTypes.length];
        // 获取到所有参数
        Map<String, String[]> paramValues = request.getParameterMap();
        for (int i = 0; i < paramTypes.length; i++) {
            if(paramTypes[i] == HttpServletRequest.class){
                result[i] = request;
            }else if(paramTypes[i] == HttpServletResponse.class){
                result[i] = response;
            }else if(paramTypes[i] == String.class){
                for(Map.Entry<String, String[]> param: paramValues.entrySet()){
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s",",");
                    result[i] = value;
                }
            }
        }


        return result;
    }
}
