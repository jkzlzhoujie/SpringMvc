package com.example.demo.config;

import com.example.demo.filter.AccessDecisionManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * @author janseny
 * @version 1.0
 * @created 2018/11/21
 */
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置不拦截规则
        web.ignoring().antMatchers(
                "/static/**",
                "/common/**",
                "/login/**",
                "/out"
        );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 设置拦截规则
        http
                .authorizeRequests()
                .accessDecisionManager(accessDecisionManager())
//                .expressionHandler(webSecurityExpressionHandler())
                .antMatchers("/yueren/**").permitAll()
                .antMatchers("/svr-iot/device/**").permitAll()//物联网平台没有做登录（这里添加免登录验证）
                .antMatchers("/admin/**").hasRole("USER")
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
//                .headers().frameOptions().disable();//取消jfame的安全验证
//                .and()
                .headers().disable();

        // 自定义登录页面
//        http.csrf().disable().formLogin().loginPage("/login").permitAll();

        // 自定义注销
//        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login")
//                .invalidateHttpSession(true);

        // session管理
//        http.sessionManagement().sessionFixation().changeSessionId()
//                .maximumSessions(1).expiredUrl("/");

        // RemeberMe  和UserDetailsService合作 用来保存用户信息， 一段时间内可以不用在输入用户名和密码登录，暂不使用该功能
//        http.rememberMe().key("webmvc#FD637E6D9C0F1A5A67082AF56CE32485");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    }

    /*
     * 最终访问控制器
     */
    @Bean(name = "accessDecisionManager")
    public AccessDecisionManager accessDecisionManager() {
        return new AccessDecisionManagerImpl();
    }

    /*
     * 错误信息拦截器
     */
    @Bean(name = "accessDeniedHandler")
    public AccessDeniedHandlerImpl accessDeniedHandler() {
        AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
        accessDeniedHandler.setErrorPage("/error/403.jsp");
        return accessDeniedHandler;
    }

//    /*
//     * 表达式控制器
//     */
//    @Bean(name = "expressionHandler")
//    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
//        DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
//        return webSecurityExpressionHandler;
//    }
}
