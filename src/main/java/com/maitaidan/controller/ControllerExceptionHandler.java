package com.maitaidan.controller;

import com.maitaidan.model.GeneralResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Crytis on 2016/6/24.
 * controller异常处理.
 */
@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public GeneralResult<Void> processControllerException(Exception e){
        log.error("controller error!",e);
        return new GeneralResult<>(false, null, "系统出错，请重试！");
    }
}
