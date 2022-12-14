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

    // ????????????Jwt Token?????????

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
        // ????????????
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
                //???????????????????????????
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/doLogin").permitAll()
                .anyRequest().authenticated();
        ;
        // ????????????
        httpSecurity.formLogin()
                //??????????????????????????????
                .successHandler(jwtAuthenticationSuccessHandler)
                // ????????????????????????
                .failureHandler(jwtAuthenticationLoginFailureHandler)
                // ?????????????????????URI
                .loginProcessingUrl("/doLogin");

        // ????????????????????????
        httpSecurity.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // ??????????????????????????????
                .accessDeniedHandler(jwtAccessDeniedHandler);
        //????????????????????????,????????????session
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // ???????????????
        httpSecurity.rememberMe();

        // ??????????????????????????????  ???????????????????????????????????????????????????
        httpSecurity.cors().and().csrf().disable();

        //token??????????????????????????????csrf?????????
        httpSecurity.csrf().disable();

        //???????????????jwt???????????????
        httpSecurity
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);


        // ??????cacheControl???disable page caching???
        httpSecurity.headers().cacheControl();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        // ???????????????
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
//        // ?????????????????????????????? true
//        jdbcTokenRepository.setCreateTableOnStartup(false);
//        jdbcTokenRepository.setDataSource(dataSource);
//        return jdbcTokenRepository;
//    }


}

