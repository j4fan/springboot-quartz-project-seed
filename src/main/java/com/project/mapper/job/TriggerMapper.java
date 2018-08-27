package com.project.mapper.job;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriggerMapper {

    @Select(" select show_name from data_stat.qrtz_group_info ")
    List<String> getTriggerGroups();

}
