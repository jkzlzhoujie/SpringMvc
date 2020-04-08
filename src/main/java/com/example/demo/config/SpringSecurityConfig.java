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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题  设置不拦截规则
        web.ignoring().antMatchers(
                "/bo/**",
                "/login/**",
                "/swagger**"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        // 设置拦截规则
            http
                .formLogin()       //  定义当需要用户登录时候，转到的登录页面。loginPage("/welcome")
                .and()
                .authorizeRequests()  // 定义哪些URL需要被保护、哪些不需要被保护
                .antMatchers("/jsp/**").permitAll()
                .antMatchers("/bo/**").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers("/user").hasRole("admin")//需要角色
                .anyRequest().authenticated();    // 任何请求,登录后可以访问


//        // 自定义登录页面
//        http.csrf().disable().formLogin().loginPage("/login").permitAll();
//        // 自定义注销
//        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login")
//                .invalidateHttpSession(true);

        // session管理
//        http.sessionManagement().sessionFixation().changeSessionId()
//                .maximumSessions(1).expiredUrl("/");

        // RemeberMe  和UserDetailsService合作 用来保存用户信息， 一段时间内可以不用在输入用户名和密码登录，暂不使用该功能
//        http.rememberMe().key("webmvc#FD637E6D9C0F1A5A67082AF56CE32485");

    }

//    /*
//     * 错误信息拦截器
//     */
//    @Bean(name = "accessDeniedHandler")
//    public AccessDeniedHandlerImpl accessDeniedHandler() {
//        System.out.println("spring security 错误拦截");
//        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
//        accessDeniedHandler.setErrorPage("/error/403.jsp");
//        return accessDeniedHandler;
//    }

//    /*
//     * 表达式控制器
//     */
//    @Bean(name = "expressionHandler")
//    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
//        DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
//        return webSecurityExpressionHandler;
//    }










}
