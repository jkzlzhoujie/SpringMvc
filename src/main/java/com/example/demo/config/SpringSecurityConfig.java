package com.example.demo.config;

import com.example.demo.filter.AccessDecisionManagerImpl;
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

    /**
     * 设置  Http 请求的 拦截规则
     **/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 自定义登录页面  -- 未自定义登录界面 ，spring sercurity 自带有登录界面 localhost:8081/login
        http.csrf().disable().formLogin().loginPage("/welcome").permitAll();
        // 自定义注销
        http.logout().logoutUrl("/logout").logoutSuccessUrl("/welcome")
                .invalidateHttpSession(true);
        //定义哪些URL需要被保护、哪些不需要被保护  拦截规则
        http.authorizeRequests()  //
                .antMatchers("/jsp/**").permitAll()
                .antMatchers("/bo/**").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers("/user").hasRole("admin")//需要角色
                .anyRequest().authenticated();    // 任何请求,登录后可以访问

        // session管理
//        http.sessionManagement().sessionFixation().changeSessionId()
//                .maximumSessions(1).expiredUrl("/");

        // RemeberMe  和UserDetailsService合作 用来保存用户信息， 一段时间内可以不用在输入用户名和密码登录，暂不使用该功能
//        http.rememberMe().key("webmvc#FD637E6D9C0F1A5A67082AF56CE32485");
    }

    /**
     * 错误信息拦截器
     **/
    @Bean(name = "accessDeniedHandler")
    public AccessDeniedHandlerImpl accessDeniedHandler() {
        System.out.println("spring security 错误拦截");
        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage("/error/403.jsp");
        return accessDeniedHandler;
    }

//<!------------------- SpringSecurity 正常配置启动，正常跳转到登录页 及 http 权限控制-------------------------------------->

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //下面这两行配置表示在内存中配置了两个用户 密码明文：123
        auth.inMemoryAuthentication()
                .withUser("admin").roles("administrator").password("$2a$10$OR3VSksVAmCzc.7WeaRPR.t0wyCsIj24k0Bne8iKWV1o.V9wsP8Xe")
                .and()
                .withUser("janseny").roles("user").password("$2a$10$p1H8iWa8I4.CA.7Z8bwLjes91ZpY.rYREGHQEInNtAp4NzL6PLKxi");
    }

    //设置密码的加密方式
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    /*
//     * 表达式控制器  -- 扩展内容
//     */
//    @Bean(name = "expressionHandler")
//    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
//        DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
//        return webSecurityExpressionHandler;
//    }


}
