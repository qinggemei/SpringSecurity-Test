package com.star.handler;

import com.alibaba.fastjson.JSON;
import com.star.base.ResultEnum;
import com.star.base.ResultVo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 21:06
 */
@Component
public class StarAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResultVo resultVo = new ResultVo(ResultEnum.NOT_PERMISSION);
        String json =  JSON.toJSONString(resultVo);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(json);
    }
}
