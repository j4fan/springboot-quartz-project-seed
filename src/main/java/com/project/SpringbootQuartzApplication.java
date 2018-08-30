package com.project;

import com.project.config.GlobalConf;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootQuartzApplication {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(SpringbootQuartzApplication.class);
        springApplication.addListeners(new GlobalConf());
        springApplication.run(args);

    }

}
