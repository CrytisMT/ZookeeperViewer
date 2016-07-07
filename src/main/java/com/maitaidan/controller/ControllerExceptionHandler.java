package com.maitaidan.controller;

import com.maitaidan.model.GeneralResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
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


    @ExceptionHandler(KeeperException.NoNodeException.class)
    @ResponseBody
    public GeneralResult<Void> processNoNodeException(KeeperException.NoNodeException e){
        log.error("node不存在{}",e.getMessage());
        return new GeneralResult<>(false, null, "节点已经不存在，请刷新后重试！");
    }

    @ExceptionHandler(KeeperException.ConnectionLossException.class)
    @ResponseBody
    public GeneralResult<Void> processConnectionException(KeeperException.ConnectionLossException e){
        return new GeneralResult<>(false, null, "创建连接失败！！");
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public GeneralResult<Void> processControllerException(Exception e){
        log.error("controller error!",e);
        return new GeneralResult<>(false, null, "系统出错，请重试！");
    }
}
