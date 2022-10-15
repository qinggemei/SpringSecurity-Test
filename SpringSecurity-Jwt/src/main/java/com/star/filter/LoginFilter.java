package com.star.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 自定义前后端分离认证 Filter
 *
 * @author liuxing
 */

public class LoginFilter extends UsernamePasswordAuthenticationFilter {


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        //1.判断是否是 post 方式请求
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        //2.判断是否是 json 格式请求类型
        if (request.getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            //3.从 json 数据中获取用户输入用户名和密码进行认证 {"uname":"xxx","password":"xxx","remember-me":true}
            Map<String, String> userInfo = null;
            try {
                userInfo = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                logger.info("-------------------请求数据解析失败-------------------");
                e.printStackTrace();
            }
            String username = userInfo.get(getUsernameParameter());
            String password = userInfo.get(getPasswordParameter());
            // 记住我
            String rememberMe = userInfo.get(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY);
            if(StringUtils.hasText(rememberMe)){
                request.setAttribute(AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY,rememberMe);
            }
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        throw new AuthenticationServiceException("登录信息错误");
    }
}
