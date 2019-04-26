package com.tian.txspring.webmvc.handler.param;

import com.tian.txspring.webmvc.util.ReflectionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        // 把map中的值, 转到一个有序集合中
        ArrayList<String[]> arrayList = new ArrayList<String[]>();
        arrayList.addAll(paramValues.values());
        for (int i = 0; i < paramTypes.length; i++) {
            if(paramTypes[i] == HttpServletRequest.class){
                result[i] = request;
            }else if(paramTypes[i] == HttpServletResponse.class){
                result[i] = response;
            }else if(paramTypes[i] == String.class){
                String value = arrayList.get(i)[0];
                result[i] = value;
            }else if(paramTypes[i] == Byte.class){
                Byte value = Byte.parseByte(arrayList.get(i)[0]);
                result[i] = value;
            }else if(paramTypes[i] == Integer.class){
                Integer value = Integer.parseInt(arrayList.get(i)[0]);
                result[i] = value;
            }else if(paramTypes[i] == Long.class){
                Long value = Long.parseLong(arrayList.get(i)[0]);
                result[i] = value;
            }else if(paramTypes[i] == Double.class){
                Double value = Double.parseDouble(arrayList.get(i)[0]);
                result[i] = value;
            }else if(paramTypes[i] == Date.class){
                Date value = null;
                // 常用的日志解析, 页面有两种形式, 一个是传时间戳, 一个是传格式化的时候
                try{
                    Long time = Long.parseLong(arrayList.get(i)[0]);
                    value = new Date(time);
                }catch (Exception e){
                    // 再尝试用格式化时间解析一次
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        value = sdf.parse(arrayList.get(i)[0]);
                    } catch (ParseException e1) {
                        // 解析还是失败,格式有误
                        e1.printStackTrace();
                        throw new RuntimeException(e1);
                    }
                }
                result[i] = value;
            }else {
                // 不是基本类型的参数, 可能是自定义的对象, 则要对对象内部的属性进行参数绑定
                Object value = parseObject(paramTypes[i], paramValues);
                result[i] = value;
            }
        }

        return result;
    }

    /**
     * 自定义对象的参数绑定
     * @param paramType 自定义对象的类型
     * @param paramValues 接收到的参数集合
     * @return
     */
    private Object parseObject(Class paramType, Map<String, String[]> paramValues) {
        // 先通过反射创建一个对象
        Object object = ReflectionUtil.newInstance(paramType);
        // 拿到对象中的属性, 然后在值中找到该属性的值, 并设置
        Field[] fields = paramType.getDeclaredFields();
        for(Field f: fields){
            String filedName = f.getName();
            String[] filedValue = paramValues.get(filedName);

            // 属性的类型
            Class clazz = f.getType();

            f.setAccessible(true);
            try {
                f.set(object, convertTypeValue(clazz, filedValue[0]));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        // 返回对象
        return object;
    }

    /**
     * 把一个String类型的值转换成指定类型的值
     * @param clazz
     * @param strValue
     * @return
     */
    private static Object convertTypeValue(Class clazz, String strValue) throws ParseException {
        if(clazz == Byte.class){
            return Byte.parseByte(strValue);
        }else if(clazz == Integer.class){
            return Integer.parseInt(strValue);
        }else if(clazz == Long.class){
            return Long.parseLong(strValue);
        }else if(clazz == Double.class){
            return Double.parseDouble(strValue);
        }else if(Float.class == clazz){
            return Float.parseFloat(strValue);
        }else if(String.class == clazz){
            return strValue;
        }else if(Date.class == clazz){
            Date value = null;
            try{
                Long time = Long.parseLong(strValue);
                value = new Date(time);
            }catch (Exception e){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                value = sdf.parse(strValue);
            }
            return value;
        }else {
            return strValue;
        }
    }
}
