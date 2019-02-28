package com.example.demo.common.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author janseny
 * @date 2018/12/05
 */
@Configuration
public class SchedulerConfig {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private JobFactory jobFactory;
    @Autowired
    private DataSource dataSource;

    /**
     * quartz配置文件
     * @return
     * @throws IOException
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        bean.setJobFactory(jobFactory);
        bean.setApplicationContext(this.applicationContext);
        bean.setOverwriteExistingJobs(true);
        // 延时启动
        bean.setStartupDelay(20);
        bean.setSchedulerName("schedulerFactoryBeanCWD");
        bean.setAutoStartup(true);
        bean.setDataSource(dataSource);
        bean.setQuartzProperties(quartzProperties());
        return bean;
    }
}

