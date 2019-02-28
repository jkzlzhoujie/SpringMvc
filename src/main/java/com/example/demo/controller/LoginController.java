package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by janseny on 2018/11/28.
 */

@Controller
@RequestMapping("login")
public class LoginController {

    @RequestMapping(value = "index",method = RequestMethod.GET)
    public String index(){
        System.out.println("/login/index");
        return "login/index";
    }

    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String login(){
        System.out.println("login--");
        return "login/login";
    }

    @RequestMapping(value = "loginPost",method = RequestMethod.POST)
    public String login2(){
        System.out.println("login");
        return "login/login";
    }

}
