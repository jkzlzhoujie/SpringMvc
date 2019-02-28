package com.example.demo.job;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * @author janseny
 * @date 2018/12/04
 * DisallowConcurrentExecution 防止到了执行时间点前一任务还在执行中，但是这时有空闲的线程，那么马上又会执行，这样一来就会存在同一job被并行执行
 */
@Component
@Scope("prototype")
@DisallowConcurrentExecution
public class SchedulerJob implements Job {
    private Logger logger = LoggerFactory.getLogger(SchedulerJob.class);

    private String endTime; // 结束时间
    private String startTime; //开始时间

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            //springz注入
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            //初始化参数
            initParams(context);
            logger.warn("开始执行");
            excute();
        } catch (Exception e) {
            //如果出错立即重新执行
            JobExecutionException e2 = new JobExecutionException(e);
            e2.setRefireImmediately(true);
            e.printStackTrace();
        }
    }

    /**
     * 初始化参数
     * @param context
     */
    private void initParams(JobExecutionContext context) {
        JobDataMap paramMap = context.getJobDetail().getJobDataMap();
        startTime = (String) paramMap.get("startTime");
        endTime = (String) paramMap.get("endTime");
        System.out.println("startTime" + startTime + "endTime" + endTime);
    }

    private void excute(){
        System.out.println("执行中。。。。");
    }


}
