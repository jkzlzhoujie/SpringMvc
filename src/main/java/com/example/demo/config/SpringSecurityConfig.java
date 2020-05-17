package com.example.demo.config;

import com.example.demo.filter.AccessDecisionManagerImpl;
import com.example.demo.service.CmUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * @author janseny
 * @version 1.0
 * @created 2018/11/21
 */

//@EnableWebSecurity
// 该注解@EnableWebSecurity需要开发人员自己引入以启用Web安全
// 在非Springboot的Spring Web MVC应用中，
// 而在基于Springboot的Spring Web MVC应用中,开发人员没有必要再次引用该注解，Springboot的自动配置机制

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {


    /**
     * 静态资源被拦截的问题  设置拦截规则
     **/
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(  //ignoring 不拦截
                "/bo/**",
                "/login/**",
                "/swagger/**"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //以下五步是表单登录进行身份认证最简单的登陆环境
        http.formLogin() //表单登陆 1
                .loginPage("/welcome")//自定义登录页
                .loginProcessingUrl("/authentication/form")//登陆页面提交的页面 开始使用UsernamePasswordAuthenticationFilter过滤器处理请求
                .successForwardUrl("/login/success")//自定义登录成功跳转接口
                .and() //2
                .authorizeRequests() //下面的都是授权的配置 3
                .antMatchers("/welcome").permitAll()//访问此地址就不需要进行身份认证了，防止重定向死循环
                .antMatchers("/authentication/form").permitAll()
                .anyRequest() //任何请求 4
                .authenticated() //访问任何资源都需要身份认证 5
                .and()
                .csrf().disable();//关闭跨站请求伪造攻击拦截

        // session管理
//        http.sessionManagement().sessionFixation().changeSessionId()
//                .maximumSessions(1).expiredUrl("/");

        // RemeberMe  和UserDetailsService合作 用来保存用户信息， 一段时间内可以不用在输入用户名和密码登录，暂不使用该功能
//        http.rememberMe().key("webmvc#FD637E6D9C0F1A5A67082AF56CE32485");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String password ="123";
//        方法1 ：下面这两行配置表示在内存中配置了两个用户 密码明文：123
        auth.inMemoryAuthentication()
                .withUser("admin").roles("administrator").password(password)
                .and()
                .withUser("janseny").roles("user").password(password);
    }

}
