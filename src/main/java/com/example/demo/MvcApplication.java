package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



@EnableScheduling //启动  spring Scheduler 定时任务
@SpringBootApplication
public class MvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcApplication.class, args);
	}
}
