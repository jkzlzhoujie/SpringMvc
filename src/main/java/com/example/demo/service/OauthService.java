package com.example.demo.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhoujie
 * @Date: 2020/5/19
 */
@Service
public class OauthService {

    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;
    @Autowired
    private AuthenticationManager authenticationManager;

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
    public Object getToken(String username, String password, HttpServletRequest request) throws IOException {
        Map<String,Object> map = new HashMap<>(8);
        //进行验证
        String header = request.getHeader("Authorization");
        if (header == null && !header.startsWith("Basic")) {
            map.put("code",500);
            map.put("message","请求投中无client信息");
            return map;
        }
        String[] tokens = this.extractAndDecodeHeader(header, request);
        assert tokens.length == 2;
        //获取clientId 和 clientSecret
        String clientId = tokens[0];
        String clientSecret = tokens[1];
        //获取 ClientDetails
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null){
            map.put("code",500);
            map.put("message","clientId 不存在"+clientId);
            return map;
            //判断 方言 是否一致
        }else if (!StringUtils.equals(clientDetails.getClientSecret(),clientSecret)){
            map.put("code",500);
            map.put("message","clientSecret 不匹配"+clientId);
            return map;
        }
        //使用username、密码进行登录
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        //调用指定的UserDetailsService，进行用户名密码验证
        Authentication authenticate = authenticationManager.authenticate(authentication);
//        HrUtils.setCurrentUser(authenticate);
        //放到session中
        //密码授权 模式, 组建 authentication
        TokenRequest tokenRequest = new TokenRequest(new HashMap<>(),clientId,clientDetails.getScope(),"password");

        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request,authentication);

        OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
        map.put("code",200);
        map.put("token",token.getValue());
        map.put("refreshToken",token.getRefreshToken());
        return map;
    }

    /**
     * 解码请求头
     */
    private String[] extractAndDecodeHeader(String header, javax.servlet.http.HttpServletRequest request) throws IOException {
        byte[] base64Token = header.substring(5).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }
        String token = new String(decoded, "UTF-8");
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        }
    }


}
