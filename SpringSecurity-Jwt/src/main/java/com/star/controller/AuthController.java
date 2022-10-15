package com.star.controller;

import com.star.base.ResultEnum;
import com.star.base.ResultVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 21:49
 */
@RestController
public class AuthController {

    @RequestMapping("/get")
    public ResultVo get(){
        HashMap map = new HashMap();
        map.put("username","admin");
        map.put("password","123456");
        ResultVo r = new ResultVo(ResultEnum.SUCCESS,map);
        return r;
    }

    @PreAuthorize("hasAuthority('admin')")
    @RequestMapping("/del")
    public String del(){
        return "删除成功";
    }

    @RequestMapping("/admin/1")
    public String admin1(){
        return "test成功";
    }

}
