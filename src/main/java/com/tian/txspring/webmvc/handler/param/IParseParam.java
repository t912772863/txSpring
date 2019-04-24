package com.tian.txspring.webmvc.handler.param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created by tianxiong on 2019/3/25.
 */
public interface IParseParam {
    /**
     * 解析http请求参数
     * @param request
     * @param method
     * @return
     */
    Object[] parse(HttpServletRequest request, HttpServletResponse response, Method method );
}
