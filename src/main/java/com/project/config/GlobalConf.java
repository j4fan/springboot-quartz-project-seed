package com.project.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

@Log4j2
public class GlobalConf implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    public static boolean IS_ONLINE = false;
    public static boolean IS_LOCAL = false;

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
        Environment environment = applicationEnvironmentPreparedEvent.getEnvironment();
        IS_ONLINE = environment.acceptsProfiles("prod");
        IS_LOCAL = environment.acceptsProfiles("dev");
    }
}
