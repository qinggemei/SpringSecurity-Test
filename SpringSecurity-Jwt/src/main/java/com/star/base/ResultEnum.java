package com.star.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResultEnum {



    SUCCESS(200,"成功"),

    ERROR(201,"失败"),

    NOT_AUTHORIZATION(401,"用户未登录"),

    AUTHORIZATION_FAILED(402,"认证失败"),

    NOT_PERMISSION(403,"权限不足")

    ;


    private final Integer code;

    private final String message;

}
