package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by janseny on 2018/11/28.
 */

@Controller
public class LoginController {

    @RequestMapping(value = "/index",produces="application/json;charset=utf-8")
    public String index(){
        System.out.println("login/index");
        return "login/index";
    }

    @RequestMapping(value = "/welcome",produces="application/json;charset=utf-8")
    public String login(){
        System.out.println("/login/login");
        return "login/login";
    }

    @RequestMapping(value = "/home",produces="application/json;charset=utf-8")
    public String home(){
        System.out.println("/login/home");
        return "login/home";
    }

}
