package com.star.generator;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;

/**
 * @author 刘星
 * Description : 自定义代码生成器的类型转换，解决tinyint转换为Boolen的问题
 */
public class TypeConvertCustom extends MySqlTypeConvert implements ITypeConvert {

    public static final String TINT_1 = "tinyint(1)";

    @Override
    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains(TINT_1)) {
            return DbColumnType.INTEGER;
        }
        return super.processTypeConvert(globalConfig, fieldType);
    }
}
