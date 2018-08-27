package com.project.service.scheduler;

import com.project.domin.dto.ClassFullNames;
import com.project.mapper.job.ClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {

    @Autowired
    ClassMapper mapper;

    public List<ClassFullNames> getFullNames() {
        List<ClassFullNames> fullNames = mapper.getClassNames();
        return fullNames;
    }


}
