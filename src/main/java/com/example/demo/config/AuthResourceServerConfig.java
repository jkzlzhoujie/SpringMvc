package com.example.demo.config;

import com.example.demo.controller.base.AuthSecurityFailureHandler;
import com.example.demo.controller.base.AuthSecuritySuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @Author: zhoujie
 * @Date: 2020/5/19
 * 资源服务器
 * @EnableResourceServer这个注解就决定了这是个资源服务器。它决定了哪些资源需要什么样的权限
 */
@Configuration
@EnableResourceServer
public class AuthResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private AuthSecuritySuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthSecurityFailureHandler authenticationFailHandler;

    /**
     * 我这里只需要认证/cmUser/*开头的url。
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //表单登录 方式
        http.formLogin()
                .loginPage("/authentication/require")
                //登录需要经过的url请求
                .loginProcessingUrl("/authentication/form")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailHandler);

        http
                .authorizeRequests()
                .antMatchers("/cmUser/*")
                .authenticated()
                .antMatchers("/oauth/token").permitAll()
                .anyRequest()
                .permitAll()
                .and()
                //关闭跨站请求防护
                .csrf().disable();
    }



}
