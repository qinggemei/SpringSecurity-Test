package com.star.service.security;

import com.star.dao.UserMapper;
import com.star.entity.Role;
import com.star.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 21:28
 */
@Service
public class StarUserDetailsService implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userMapper.loadUserByUsername(username);
        if(user!=null){
            List<Role> rolesByUserId = userMapper.getRolesByUserId(user.getId());
            user.setRoles(rolesByUserId);
        }
        return user;
    }
}
