package com.star.handler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.base.ResultEnum;
import com.star.base.ResultVo;
import com.star.util.JwtTokenUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 21:22
 */
@Component
public class StarAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //生成token
        List<SimpleGrantedAuthority> authorities = (List<SimpleGrantedAuthority>) authentication.getAuthorities();
        List<Object> roleList = authorities.stream().map(simpleGrantedAuthority -> {
            return simpleGrantedAuthority.getAuthority();
        }).collect(Collectors.toList());
        String roles = new ObjectMapper().writeValueAsString(roleList);
        final String realToken = jwtTokenUtil.generateToken(authentication.getName(),roles);
        HashMap<String,Object> map = new HashMap<>();
        map.put("token", realToken);
        ResultVo resultVo = new ResultVo(ResultEnum.SUCCESS.getCode(), "登录成功",map);

        //将生成的authentication放入容器中，生成安全的上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String json =  JSON.toJSONString(resultVo);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(json);
    }
}
