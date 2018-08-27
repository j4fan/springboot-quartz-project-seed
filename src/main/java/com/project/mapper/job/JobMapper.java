package com.project.mapper.job;

import com.project.domin.dto.QrtzJobInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobMapper {

    @Select("select count(1) from data_stat.qrtz_job_details where JOB_NAME = #{jobName}")
    int checkJobExists(@Param("jobName") String jobName);

    @Select("select d.class_simple_name as job_class_name,a.JOB_NAME,a.JOB_GROUP,a.TRIGGER_NAME,a.TRIGGER_GROUP,\n" +
            "a.NEXT_FIRE_TIME,a.PREV_FIRE_TIME,a.START_TIME,b.CRON_EXPRESSION,\n" +
            "(case when f.STATE is null then a.TRIGGER_STATE else f.state end) as state,c.DESCRIPTION \n" +
            "from data_stat.qrtz_triggers a\n" +
            "inner join data_stat.qrtz_cron_triggers b on a.TRIGGER_NAME = b.TRIGGER_NAME\n" +
            "inner join data_stat.qrtz_job_details c on a.JOB_NAME = c.JOB_NAME\n" +
            "inner join data_stat.qrtz_class_name d on c.JOB_CLASS_NAME = d.class_full_name\n" +
            "left join data_stat.qrtz_fired_triggers f on f.JOB_NAME = a.JOB_NAME")
    List<QrtzJobInfo> listAllJobs();

    @Select("select d.class_simple_name as job_class_name,a.JOB_NAME,a.JOB_GROUP,a.TRIGGER_NAME,a.TRIGGER_GROUP,\n" +
            "a.NEXT_FIRE_TIME,a.PREV_FIRE_TIME,a.START_TIME,b.CRON_EXPRESSION,\n" +
            "(case when f.STATE is null then a.TRIGGER_STATE else f.state end) as state,c.DESCRIPTION\n" +
            "from data_stat.qrtz_triggers a\n" +
            "inner join data_stat.qrtz_cron_triggers b on a.TRIGGER_NAME = b.TRIGGER_NAME\n" +
            "inner join data_stat.qrtz_job_details c on a.JOB_NAME = c.JOB_NAME\n" +
            "inner join data_stat.qrtz_class_name d on c.JOB_CLASS_NAME = d.class_full_name\n" +
            "left join data_stat.qrtz_fired_triggers f on f.JOB_NAME = a.JOB_NAME\n" +
            "where a.JOB_NAME=#{jobName}")
    QrtzJobInfo listJobByName(@Param("jobName") String jobName);

}
