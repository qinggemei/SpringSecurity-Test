package com.star.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.base.Constant;
import com.star.base.JwtProperties;
import com.star.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 21:30
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authToken = request.getHeader(jwtProperties.getHeader());
        if (authToken != null) {
            String username = jwtTokenUtil.getUsernameFromToken(authToken);

            //当token中的username不为空时进行验证token是否是有效的token
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("自定义过滤器获得用户名为   " + username);

                Claims claimsFromToken = jwtTokenUtil.getClaimsFromToken(authToken);
                String roles = (String) claimsFromToken.get(Constant.AUTHORITIES);
                List list = new ObjectMapper().readValue(roles, List.class);
                List<SimpleGrantedAuthority> authorities= (List<SimpleGrantedAuthority>) list.stream().map(o -> {
                    return new SimpleGrantedAuthority((String) o);
                }).collect(Collectors.toList());

                username = claimsFromToken.getSubject();


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //将authentication放入SecurityContextHolder中
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }
        filterChain.doFilter(request, response);
    }

}
