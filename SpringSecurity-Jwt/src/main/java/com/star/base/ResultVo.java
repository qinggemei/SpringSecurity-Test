package com.star.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 20:49
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo {

    private Integer code;

    private String message;

    private Object data;

    public ResultVo(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
    }

    public ResultVo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultVo(ResultEnum resultEnum,Object data) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.data=data;
    }


}
