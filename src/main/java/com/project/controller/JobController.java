package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultGenerator;
import com.project.domin.dto.QrtzJobInfo;
import com.project.exception.ServiceException;
import com.project.service.scheduler.JobService;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author fanqingyuan
 */
@Log4j2
@RestController
@Api
public class JobController {

    @Autowired
    JobService service;

    @GetMapping("/jobs")
    public Result listAllJobs() {
        List<QrtzJobInfo> jobList = service.listAllJobs();
        if (jobList == null) {
            return ResultGenerator.genFailResult("not job exists");
        }
        return ResultGenerator.genSuccessResult(jobList);
    }

    @GetMapping("/jobs/{jobName}")
    public Result listJob(@PathVariable("jobName") String jobName) {
        QrtzJobInfo qrtzTriggers = service.listJobByName(jobName);
        if (qrtzTriggers == null) {
            return ResultGenerator.genFailResult(jobName + " not  exists ...");
        }
        return ResultGenerator.genSuccessResult(qrtzTriggers);
    }

    @PutMapping("/jobs/fire/{jobName}")
    public Result fireJob(@PathVariable("jobName") String jobName) throws ServiceException {
        service.fireJob(jobName);
        return ResultGenerator.genSuccessResult("fire job : " + jobName);
    }

    @PostMapping("/jobs")
    public Result createJob(
            @RequestParam("className") String className,
            @RequestParam("jobName") String jobName, @RequestParam("jobGroupName") String jobGroupName,
            @RequestParam("triggerName") String triggerName, @RequestParam("triggerGroupName") String triggerGroupName,
            @RequestParam("cron") String cron, @RequestParam(value = "description", required = false) String description,
            @RequestParam(name = "param", required = false) String param) throws ServiceException {
        service.checkJobExist(jobName);
        Class<? extends Job> newClass = service.validateClass(className);
        service.createAndScheduleNewJob(param, newClass, jobName, jobGroupName, triggerName, triggerGroupName, cron, description);
        QrtzJobInfo qrtzTriggers = service.listJobByName(jobName);
        return ResultGenerator.genSuccessResult(qrtzTriggers);
    }

    @PostMapping("/jobs/pause/{jobName}")
    public Result pauseJob(@PathVariable("jobName") String jobName) throws ServiceException {
        service.pauseJob(jobName);
        return ResultGenerator.genSuccessResult(String.format("job[%s] paused successfully", jobName));
    }

    @PostMapping("/jobs/resume/{jobName}")
    public Result resumeJob(@PathVariable("jobName") String jobName) throws ServiceException {
        service.resumeJob(jobName);
        return ResultGenerator.genSuccessResult(String.format("job[%s] resumed successfully", jobName));
    }

    @PostMapping("/jobs/{jobName}")
    public Result updateJobCron(@PathVariable("jobName") String jobName, @RequestParam("cron") String cron)
            throws ServiceException {
        service.modifyTrigger(jobName, cron);
        QrtzJobInfo qrtzTriggers = service.listJobByName(jobName);
        return ResultGenerator.genSuccessResult(qrtzTriggers);
    }

    @DeleteMapping("/jobs/{jobName}")
    public Result deleteJob(@PathVariable("jobName") String jobName) throws ServiceException {
        QrtzJobInfo jobInfo = service.listJobByName(jobName);
        if (jobInfo == null) {
            return ResultGenerator.genFailResult("job doesn't exists ...");
        }
        service.unScheduleTrigger(jobInfo.getTriggerName());
        service.deleteJob(jobName, jobInfo.getJobGroup());
        return ResultGenerator.genSuccessResult(String.format("job[%s] deleted ", jobInfo.getJobName()));
    }

}

