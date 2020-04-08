package com.example.demo.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by janseny on 2018/11/30
 *
 * AOP
 * Before 执行执行操作
 * Around 执行
 * After 执行后操作
 *
 * AOP 会影响web 界面跳转，影响视图解析器
 *
 *
 */
@Aspect
@Component
public class ObserverRequiredAOP {
    private static final Logger logger = LoggerFactory.getLogger(ObserverRequiredAOP.class);

    /**
     * 初始化构造函数加载初始化信息
     * 可以选择使用
     */
    public ObserverRequiredAOP() {
        System.out.println("AOP 构造函数，初始化构造函数加载初始化信息，可以不创建，主要用于分析类加载顺序");
    }

    /**
     * com.example.demo.controller.user
     * 表示拦截所有com.example.demo.controller.user包及子包下的所有的方法
     */
    @Pointcut("execution(public * com.example.demo.common.export.*.*(..))")
    @Order(1) // Order 代表优先级，数字越小优先级越高
    public void recordLog(){
    }

    //    Controller层切点路径
    @Pointcut("execution(public * com.example.demo.common.*.*(..))")
    @Order(2) //代表优先级，数字越小优先级越高
    public void controllerAspect() {
    }

    @Before("recordLog()")
    public void before(JoinPoint joinPoint) {
        System.out.println("@Before 方法执行中。。。");
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("<=====================================================");
        logger.info("请求来源： =》" + request.getRemoteAddr());
        logger.info("请求URL：" + request.getRequestURL().toString());
        logger.info("请求方式：" + request.getMethod());
        logger.info("响应方法：" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("请求参数：" + Arrays.toString(joinPoint.getArgs()));
        logger.info("------------------------------------------------------");
    }

    @Around("recordLog()")
    public void around(ProceedingJoinPoint pjp) throws Throwable{
        System.out.println("已经记录下操作日志@Around 方法执行前");
        long startTime = System.currentTimeMillis();
        pjp.proceed();
        long endTime = System.currentTimeMillis();
        System.out.println("共耗时：" + (endTime - startTime));
        System.out.println("方法环绕around ，end...");
    }


//    @Around("controllerAspect()")
//    public Object checkToken(ProceedingJoinPoint point) throws Throwable {
//        Object object = null;
//        String error = "";
//        try {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//            response.setCharacterEncoding("UTF-8");
//            JSONObject json = getAgent(request);
//            String observer = json.has("observer") ? json.getString("observer") : "";
//            if(StringUtils.isNotBlank(observer)&&"1".equals(observer)){
//                PrintWriter writer=response.getWriter();
//                writer.write(error(403, "该操作没有权限"));
//                writer.flush();
//                return object;
//            }
//        }catch (Exception e){
//            return object;
//        }
//        System.out.println("controllerAspect已经记录下操作日志@Around 方法执行前");
//        object = point.proceed();
//        System.out.println("controllerAspect 已经记录下操作日志@Around 方法执行后");
//        return object;
//    }

//    @After("recordLog()")
//    public void after() {
//        System.out.println("@After 方法执行中。。。");
//    }

}
