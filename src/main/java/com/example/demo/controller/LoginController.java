package com.example.demo.controller;

import com.example.demo.service.CmUserService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by janseny on 2018/11/28.
 */

@Controller
public class LoginController {

    @Autowired
    private CmUserService cmUserService;

    @RequestMapping(value = "/index",produces="application/json;charset=utf-8")
    public String index(){
        System.out.println("login/index");
        return "login/index";
    }

    @RequestMapping(value = "/welcome",produces="application/json;charset=utf-8")
    public String welcome(){
        System.out.println("/login/welcome");
        return "login/login";
    }

    @RequestMapping(value = "/login/fail",produces="application/json;charset=utf-8")
    public String loginFail(){
        System.out.println("/login/fail");
        return "login/fail";
    }

    @RequestMapping(value = "/login/success",produces="application/json;charset=utf-8")
    public String loginSuccess(){
        System.out.println("/login/index");
        return "login/index";
    }

    @RequestMapping(value = "/home",produces="application/json;charset=utf-8")
    public String home(){
        System.out.println("/login/home");
        return "login/home";
    }

}
