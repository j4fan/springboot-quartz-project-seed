package com.project.controller;

import com.project.common.Result;
import com.project.common.ResultGenerator;
import com.project.service.scheduler.TriggerService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/triggers")
@Api
public class TriggerController {

    @Autowired
    TriggerService service;

    @GetMapping
    public Result getTriggerGroupNames() {
        return ResultGenerator.genSuccessResult(service.getTriggerGroupNames());
    }
}
