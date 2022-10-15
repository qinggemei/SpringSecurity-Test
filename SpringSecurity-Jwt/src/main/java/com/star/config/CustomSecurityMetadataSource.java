package com.star.config;

import com.star.dao.MenuMapper;
import com.star.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 22:46
 */
@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private final MenuMapper menuMapper;

    @Autowired
    public CustomSecurityMetadataSource(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String requestURI = ((FilterInvocation) object).getRequest().getRequestURI();
        // 查询后形成缓存，减少数据库压力
        List<Menu> allMenu = menuMapper.getAllMenu();
        for (Menu menu : allMenu) {
            if (antPathMatcher.match(menu.getPattern(), requestURI)) {
                String[] roles = menu.getRoles().stream().map(r -> r.getName()).toArray(String[]::new);
                return SecurityConfig.createList(roles);
            }
        }
        // 每次请求都需要去查询数据库，太消耗数据库资源
//        if(StringUtils.hasText(requestURI)){
//            List<String> roleList = menuMapper.getRolesByPattern(requestURI);
//            String[] roles = roleList.stream().toArray(String[]::new);
//                return SecurityConfig.createList(roles);
//        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}