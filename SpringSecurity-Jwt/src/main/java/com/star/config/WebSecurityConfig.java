package com.star.config;

import com.star.filter.JwtAuthenticationTokenFilter;
import com.star.filter.LoginFilter;
import com.star.handler.StarAccessDeniedHandler;
import com.star.handler.StarAuthenticationEntryPoint;
import com.star.handler.StarAuthenticationFailureHandler;
import com.star.handler.StarAuthenticationSuccessHandler;
import com.star.service.security.StarUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.UrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author liuxing
 * @description
 * @create: 2022-10-12 21:36
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private StarAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Resource
    private StarAccessDeniedHandler jwtAccessDeniedHandler;

    @Resource
    private StarAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    @Resource
    private StarAuthenticationFailureHandler jwtAuthenticationLoginFailureHandler;

    @Resource
    private StarUserDetailsService userDetailsService;

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Resource
    private DataSource dataSource;

    @Resource
    private CustomSecurityMetadataSource metadataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return passwordEncoder;
    }

    // 自定义的Jwt Token过滤器

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ApplicationContext applicationContext = httpSecurity.getSharedObject(ApplicationContext.class);
        // 配置授权
        httpSecurity.apply(new UrlAuthorizationConfigurer<>(applicationContext))
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setSecurityMetadataSource(metadataSource);
                        object.setRejectPublicInvocations(false);
                        return object;
                    }
                });
        httpSecurity
                .authorizeRequests()
                //配置允许访问的路径
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/doLogin").permitAll()
                .anyRequest().authenticated();
        ;
        // 表单登录
        httpSecurity.formLogin()
                //自定义认证成功处理器
                .successHandler(jwtAuthenticationSuccessHandler)
                // 自定义失败拦截器
                .failureHandler(jwtAuthenticationLoginFailureHandler)
                // 自定义登录拦截URI
                .loginProcessingUrl("/doLogin");

        // 自定义认证失败类
        httpSecurity.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // 自定义权限不足处理类
                .accessDeniedHandler(jwtAccessDeniedHandler);
        //设置无状态的连接,即不创建session
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 开启记住我
        httpSecurity.rememberMe();

        // 解决跨域问题（重要）  只有在前端请求接口时才发现需要这个
        httpSecurity.cors().and().csrf().disable();

        //token的验证方式不需要开启csrf的防护
        httpSecurity.csrf().disable();

        //配置自己的jwt验证过滤器
        httpSecurity
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);


        // 禁用cacheControl（disable page caching）
        httpSecurity.headers().cacheControl();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        // 配置记住我
//        loginFilter.setRememberMeServices(rememberMeServices());
        loginFilter.setAuthenticationFailureHandler(jwtAuthenticationLoginFailureHandler);
        loginFilter.setAuthenticationSuccessHandler(jwtAuthenticationSuccessHandler);
        loginFilter.setFilterProcessesUrl("/doLogin");
        return loginFilter;
    }

//    @Bean
//    public RememberMeServices rememberMeServices() {
//        StarPersistentTokenBasedRememberMeServices rememberMeService = new StarPersistentTokenBasedRememberMeServices(
//                UUID.randomUUID().toString()
//                , userDetailsService
//                ,persistentTokenRepository()
//                );
//        return rememberMeService;
//    }

//    @Bean
//    public PersistentTokenRepository persistentTokenRepository() {
//        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
//        // 只需要没有表时设置为 true
//        jdbcTokenRepository.setCreateTableOnStartup(false);
//        jdbcTokenRepository.setDataSource(dataSource);
//        return jdbcTokenRepository;
//    }


}

