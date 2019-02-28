package com.example.demo.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by janseny on 2018/11/30
 *
 *      待重新测试 备注
 */
@Aspect
@Component
public class ObserverRequiredAOP {

    //Controller层切点路径


    @Pointcut("execution(public * com.example.demo.controller.*.*(..))")
    public void recordLog(){}

//    @Pointcut("execution(* com.example.demo..*.*(..))")
//    public void controllerAspect() {}

    public ObserverRequiredAOP() {
//        System.out.println("Observer-----执行----------------------------------");
    }


//    @Around("controllerAspect()")
//    public Object checkToken(ProceedingJoinPoint point) throws Throwable {
//        Object object = null;
//        String error = "";
////        try {
////            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
////            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
////            response.setCharacterEncoding("UTF-8");
////            JSONObject json = getAgent(request);
////            String observer = json.has("observer") ? json.getString("observer") : "";
////            if(StringUtils.isNotBlank(observer)&&"1".equals(observer)){
////                PrintWriter writer=response.getWriter();
////                writer.write(error(403, "该操作没有权限"));
////                writer.flush();
////                return object;
////            }
////        }catch (Exception e){
////            //return object;
////        }
//        System.out.println("controllerAspect已经记录下操作日志@Around 方法执行前");
//        object = point.proceed();
//        System.out.println("controllerAspect 已经记录下操作日志@Around 方法执行后");
//        return object;
//    }

    @Before("recordLog()")
    public void before() {
        System.out.println("已经记录下操作日志@Before 方法执行前");
    }

//    @Around("recordLog()")
//    public void around(ProceedingJoinPoint pjp) throws Throwable{
//        System.out.println("已经记录下操作日志@Around 方法执行前");
//        pjp.proceed();
//        System.out.println("已经记录下操作日志@Around 方法执行后");
//    }

    @After("recordLog()")
    public void after() {
        System.out.println("已经记录下操作日志@After 方法执行后");
    }


    public String write(int code, String msg) {
        try {
            JSONObject json = new JSONObject();
            json.put("status", code);
            json.put("msg", msg);
            return json.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String error(int code, String msg) {
        try {
            JSONObject json = new JSONObject();
            json.put("status", code);
            json.put("msg", msg);
            return json.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject getAgent(HttpServletRequest request) {
        try {
            String userAgent = request.getHeader("userAgent");
            if (StringUtils.isEmpty(userAgent)) {
                userAgent = request.getHeader("User-Agent");
            }
            System.out.println("userAgent:" + userAgent);
            return new JSONObject(userAgent);
        } catch (Exception e) {
            return null;
        }
    }

}
