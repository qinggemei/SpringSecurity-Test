package com.star.base;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 20:40
 */
@Data
@ToString
@Configuration
// 与配置文件中的数据关联起来(这个注解会自动匹配jwt开头的配置)
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 请求头名称 */
    private String header;

    /** Base64对该令牌进行编码 */
    private String secret;

    /** 令牌过期时间 此处单位/毫秒 */
    private Long tokenValidityInSeconds;

}
