package com.project.service.scheduler;

import com.project.mapper.job.TriggerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TriggerService {

    @Autowired
    TriggerMapper mapper;

    public List<String> getTriggerGroupNames() {
        List<String> triggerNames = mapper.getTriggerGroups();
        return triggerNames;
    }
}
