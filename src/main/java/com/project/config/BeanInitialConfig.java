package com.project.config;

import com.project.domin.dto.ClassFullNames;
import com.project.mapper.job.ClassMapper;
import org.quartz.Job;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author j4fan
 *
 * initialize beans that implement Job.class into database in order to used by caller from the web page
 */

@Component
public class BeanInitialConfig implements ApplicationContextAware {

    @Autowired
    ClassMapper mapper;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        String[] beans = applicationContext.getBeanNamesForType(Job.class);

        mapper.truncateNames();
        List<ClassFullNames> namesList = new ArrayList<>();
        Arrays.asList(beans).forEach(bean ->
                {
                    Class className = applicationContext.getBean(bean).getClass();
                    namesList.add(new ClassFullNames(className.getName().split("\\$")[0], className.getSimpleName().split("\\$")[0]));
                }
        );
        //remove proxy class
        namesList.remove(new ClassFullNames("com.sun.proxy.", "$Proxy88"));
        namesList.remove(new ClassFullNames("com.sun.proxy.", ""));
        if (namesList.size() > 0) {
            mapper.insertClassNames(namesList);
        }

    }
}
