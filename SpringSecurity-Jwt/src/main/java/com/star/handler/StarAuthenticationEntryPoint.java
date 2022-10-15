package com.star.handler;

import com.alibaba.fastjson.JSON;
import com.star.base.ResultEnum;
import com.star.base.ResultVo;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 20:48
 */
@Component
public class StarAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("无凭证");
        ResultVo resultVo = new ResultVo(ResultEnum.NOT_AUTHORIZATION);
        // 使用fastjson
        String json =  JSON.toJSONString(resultVo);
        // 指定响应格式是json
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(json);
    }
}
