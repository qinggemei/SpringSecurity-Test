package com.star.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.entity.Role;
import com.star.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liuxing
 * @since 2022-09-07
 */
public interface UserMapper extends BaseMapper<User> {

    List<User> findAllUser();

    User loadUserByUsername(String username);

    List<Role> getRolesByUserId(Integer userId);

    Integer updatePassword(@Param("username")String username,@Param("password") String password);



}
