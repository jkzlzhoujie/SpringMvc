package com.example.demo.controller.base;

import com.example.demo.service.OauthService;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoujie
 * @Date: 2020/5/17
 * 认证接口类
 */
@RestController
public class OauthController {


    @Autowired
    private OauthService oauthService;

    /**
     * 获取 token
     *
     * 请求头：Authorization:Basic+clientid:secret 的base64加密字符串 (认证服务器中设置的client信息)
     * 请求参数：username password (用户登陆账号密码)
     * @param username
     * @param password
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/oauth/getToken")
    public Object getToken(
            @ApiParam(name = "username", value = "登录名", required = true)
            @RequestParam(value = "username")
            String username,
            @ApiParam(name = "password", value = "密码", required = true)
            @RequestParam(value = "password")
            String password,
            HttpServletRequest request
    ) throws IOException {
        System.out.println("获取 token ");
        return oauthService.getToken(username,password,request);
    }






}
