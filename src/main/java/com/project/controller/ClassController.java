package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultGenerator;
import com.project.service.scheduler.ClassService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classes")
@Api
public class ClassController {

    @Autowired
    ClassService service;

    @GetMapping
    public Result getClassNames() {
        return ResultGenerator.genSuccessResult(service.getFullNames());
    }

}
