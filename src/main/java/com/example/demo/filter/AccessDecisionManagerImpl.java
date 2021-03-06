package com.example.demo.filter;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.AccessDecisionManager;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.access.ConfigAttribute;
//import org.springframework.security.authentication.InsufficientAuthenticationException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author janseny
 * @version 1.0
 * @created 2018/11/22
 */

/**
 * Spring Security提供了一些拦截器，来控制对安全对象的访问权限，例如方法调用或web请求。
 * 一个是否允许执行调用的预调用决定，是由AccessDecisionManager实现的。
 * 这个 AccessDecisionManager 被AbstractSecurityInterceptor调用，
 * 它用来作最终访问控制的决定。
 *
 * implements AccessDecisionManager
 */
public class AccessDecisionManagerImpl {
//    @Autowired
//    RoleCache roleCache;
//
//    @Autowired
//    private HttpServletRequest request;
//
//    /**
//     * 验证用户是否拥有权限
//     *
//     * @param authentication   用户权限列表
//     * @param object           FilterInvocation 请求信息
//     * @param configAttributes 当前访问路径所需权限
//     * @throws AccessDeniedException               如无权限 抛出该异常
//     * @throws InsufficientAuthenticationException
//     */
//    @Override
//    public void decide(Authentication authentication, Object object,
//                       Collection<ConfigAttribute> configAttributes)
//            throws AccessDeniedException, InsufficientAuthenticationException {
//        if (configAttributes == null) {
//            return;
//        }
//        FilterInvocation fi = (FilterInvocation) object;
//        String url = fi.getRequestUrl();
//        Iterator<ConfigAttribute> ite = configAttributes.iterator();
//        ConfigAttribute ca;
//        int index;
//        while (ite.hasNext()) {
//            ca = ite.next();
//            if ("anonymous".equals(ca.toString()) || "permitAll".equals(ca.toString())) {
//                return;
//            }
//        }
//
//        index = url.indexOf("?");
//        if (index != -1){
//            url = url.substring(0, index);
//        }
//        url.replaceAll("/+", "/");
//        if (!roleCache.contains(url)){
//            return;
//        }
//        Object res = null;
//        try {
//            res = request.getSession().getAttribute("token");
//        } catch (Exception ex) {
//            res = fi.getRequest().getSession().getAttribute("token");
//        }
//        //ga 为用户所被赋予的权限
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        if (res != null) {
//            ArrayList<String> token = (ArrayList<String>) res;
//            if (token.contains(url)){
//                return;
//            }
//        }
//        throw new AccessDeniedException("没有权限！");
//
//    }
//
//
//    /**
//     * 在启动的时候被AbstractSecurityInterceptor调用，)
//     * 来决定AccessDecisionManager是否可以执行传递ConfigAttribute。
//     * @param attribute
//     * @return
//     */
//    @Override
//    public boolean supports(ConfigAttribute attribute) {
//
//        return true;
//
//    }
//
//    /**
//     * 被安全拦截器实现调用，
//     * 包含安全拦截器将显示的AccessDecisionManager支持安全对象的类型。
//     * @param clazz
//     * @return
//     */
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return true;
//    }

}