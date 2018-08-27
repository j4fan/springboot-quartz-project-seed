package com.project.config;

import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class SchedulerConfig {

    @Bean
    @Primary
    public ExecutorService executorService() {
        ExecutorService es = new ThreadPoolExecutor(5, 10, 30,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(5), new ThreadPoolExecutor.DiscardPolicy());
        return es;
    }

    @Bean
    public Scheduler scheduler(@Autowired SchedulerFactoryBean schedulerFactoryBean, @Autowired JobListener jobListener) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.getListenerManager().addJobListener(jobListener);
        scheduler.start();
        return scheduler;
    }

    @Bean
    public JobFactory jobFactory() {
        SchedulerJobFactory factory = new SchedulerJobFactory();
        return factory;
    }

    @Bean
    @Primary
    public SchedulerFactoryBean factoryBean(@Autowired JobFactory jobFactory,
                                            @Qualifier("qrtzDataSource") DataSource dataSource,
                                            @Autowired ExecutorService es) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setAutoStartup(true);
        factory.setTaskExecutor(es);
        factory.setSchedulerName("masterScheduler");
        Properties properties = new Properties();
        //stor job into db
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.useProperties", "true");
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        return factory;
    }

}


