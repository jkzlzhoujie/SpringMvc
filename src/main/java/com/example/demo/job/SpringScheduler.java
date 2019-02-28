package com.example.demo.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@Api(description = "定时任务", tags = {"定时任务"})
public class SpringScheduler {

    private static final Logger log = LoggerFactory.getLogger(SpringScheduler.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     *
     * cron = "0 10 02 * * ?"  秒 分 时 日 月 年  每天凌晨2点10分0秒 执行
     */
    @Scheduled(cron = "10 20 17 * * ?")
    public void statisticTransferTreatmentScheduler() {
        try {
            int i = 0 ;
            System.out.println("sss");
            while (i < 100){
                i++;
                Thread.sleep(100);
                System.out.println("测试打印 : " + i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
