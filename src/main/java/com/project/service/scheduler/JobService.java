package com.project.service.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.project.domin.dto.QrtzJobInfo;
import com.project.exception.ServiceException;
import com.project.mapper.job.JobMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * @author fanqingyuan
 */
@Service
@Log4j2
public class JobService {

    @Autowired
    Scheduler scheduler;

    @Autowired
    JobMapper mapper;


    public List<QrtzJobInfo> listAllJobs() {
        List<QrtzJobInfo> jobs = mapper.listAllJobs();
        return jobs;
    }

    public QrtzJobInfo listJobByName(String jobName) {
        QrtzJobInfo qrtzTriggers = mapper.listJobByName(jobName);
        return qrtzTriggers;
    }

    public void checkJobExist(String jobName) throws ServiceException {
        int jobCount = mapper.checkJobExists(jobName);
        if (jobCount > 0) {
            throw new ServiceException(String.format("jobName:%s already exists", jobName));
        }
    }

    public Class<? extends Job> validateClass(String className) throws ServiceException {
        Class<? extends Job> newClass;
        try {
            newClass = (Class<? extends Job>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ServiceException(String.format("className not found : %s", className));
        }
        return newClass;
    }

    public void createAndScheduleNewJob(String param, Class<? extends Job> newClass,
                                        String jobName, String groupName, String triggerName, String triggerGroupName,
                                        String cron, String description) throws ServiceException {

        JobDetail job = createJob(param, newClass, jobName, groupName, description);

        CronTrigger cronTrigger = createCrontrigger(triggerName, triggerGroupName, cron);

        schedulerJob(job, cronTrigger);
    }

    private void schedulerJob(JobDetail job, CronTrigger cronTrigger) throws ServiceException {
        try {
            scheduler.scheduleJob(job, cronTrigger);
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }


    private CronTrigger createCrontrigger(String triggerName, String triggerGroupName, String cron) throws ServiceException {
        CronTrigger cronTrigger;
        try {
            cronTrigger = newTrigger().
                    withIdentity(triggerName, triggerGroupName).
                    withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing()).
                    build();
        } catch (RuntimeException e) {
            throw new ServiceException(e.getMessage());
        }
        return cronTrigger;
    }

    private JobDetail createJob(String param, Class<? extends Job> newClass,
                                String jobName, String groupName, String description) throws ServiceException {
        JobDetail job = null;

        if (StringUtils.isNotBlank(param)) {
            JobDataMap jobDataMap = parseParam(param);
            job = newJob(newClass).withDescription(description)
                    .withIdentity(jobName, groupName)
                    .usingJobData(jobDataMap)
                    .build();
        } else {
            job = newJob(newClass).withDescription(description)
                    .withIdentity(jobName, groupName)
                    .build();
        }
        return job;
    }

    public void unScheduleTrigger(String triggerName) throws ServiceException {
        try {
            scheduler.unscheduleJob(triggerKey(triggerName, "startNowTriggerGroup"));
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void deleteJob(String jobName, String groupName) throws ServiceException {
        try {
            scheduler.deleteJob(jobKey(jobName, groupName));
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void fireJob(String jobName) throws ServiceException {
        QrtzJobInfo qrtzTriggers = mapper.listJobByName(jobName);
        try {
            scheduler.triggerJob(new JobKey(qrtzTriggers.getJobName(), qrtzTriggers.getJobGroup()));
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void pauseJob(String jobName) throws ServiceException {
        QrtzJobInfo qrtzTriggers = mapper.listJobByName(jobName);
        try {
            scheduler.pauseJob(new JobKey(qrtzTriggers.getJobName(), qrtzTriggers.getJobGroup()));
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void resumeJob(String jobName) throws ServiceException {
        QrtzJobInfo qrtzTriggers = mapper.listJobByName(jobName);
        try {
            scheduler.resumeJob(new JobKey(qrtzTriggers.getJobName(), qrtzTriggers.getJobGroup()));
        } catch (SchedulerException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public void modifyTrigger(String jobName, String cron) throws ServiceException {
        QrtzJobInfo qrtzTriggers = mapper.listJobByName(jobName);

        // retrieve the trigger
        Trigger oldTrigger = null;
        try {
            oldTrigger = scheduler.getTrigger(triggerKey(qrtzTriggers.getTriggerName(), qrtzTriggers.getTriggerGroup()));

            // obtain a builder that would produce the trigger
            TriggerBuilder tb = oldTrigger.getTriggerBuilder();

            // update the schedule associated with the builder, and build the new trigger
            // (other builder methods could be called, to change the trigger in any desired way)
            Trigger newTrigger = tb.withSchedule
                    (CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing()).build();

            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);

        } catch (SchedulerException e) {
            throw new ServiceException("retrigger the job fail ...");
        }
    }

    private JobDataMap parseParam(String param) throws ServiceException {
        JobDataMap dataMap = new JobDataMap();
        Map map;
        try {
            map = JSON.parseObject(param, Map.class);
        } catch (JSONException e) {
            throw new ServiceException(String.format("param[%s] parse to json fail ", param));
        }
        dataMap.putAll(map);
        return dataMap;
    }


}
