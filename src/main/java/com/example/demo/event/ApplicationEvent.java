package com.example.demo.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by janseny on 2018/11/30.
 * 使用场景
 * 在一些业务场景中，当容器初始化完成之后，需要处理一些操作，比如一些数据的加载、初始化缓存、特定任务的注册等等。
 * 这个时候我们就可以使用Spring提供的ApplicationListener来进行操作。
 *
 */
public class ApplicationEvent implements ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(ApplicationEvent.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("我的父容器为：" + contextRefreshedEvent.getApplicationContext().getParent());
        System.out.println("初始化时我被调用了。");
//        try {
//            // 启动redis 消息队列线程
//            logger.info("redis message start");
//            logger.info("redis message end");
//        } catch (Exception e) {
//            logger.info("redis message start failed");
//        }
    }
}
