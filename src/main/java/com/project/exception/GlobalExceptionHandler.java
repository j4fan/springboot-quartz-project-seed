package com.project.exception;

import com.project.common.Result;
import com.project.common.ResultGenerator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ServiceException.class)
    public Result errorHandlerOverJson(ServiceException exception) {
        String errorMessage = exception.getMessage();
        return ResultGenerator.genFailResult(errorMessage);
    }

}
