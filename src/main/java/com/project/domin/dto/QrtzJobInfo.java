package com.project.domin.dto;

import lombok.Data;

@Data
public class QrtzJobInfo {

    private String jobClassName;

    private String jobName;

    private String jobGroup;

    private String triggerName;

    private String triggerGroup;

    private Long nextFireTime;

    private Long prevFireTime;

    private Long startTime;

    private String cronExpression;

    private String state;

    private String description;

}