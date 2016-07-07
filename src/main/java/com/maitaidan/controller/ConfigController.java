package com.maitaidan.controller;

import com.maitaidan.aop.ProcessArgs;
import com.maitaidan.model.GeneralResult;
import com.maitaidan.service.InitService;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by Crytis on 2016/7/7.
 * Just test.
 */
@RestController
public class ConfigController {
    private Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Resource
    InitService initService;

    @ProcessArgs
    @RequestMapping("addClient")
    public GeneralResult<Void> addClientList(String clientName) throws KeeperException.ConnectionLossException {
        if (StringUtils.isBlank(clientName)) return new GeneralResult<>(false, null, "参数错误");
        initService.addZKClient(clientName);
        return new GeneralResult<>(true, null, "添加成功");
    }

    @RequestMapping("deleteClient")
    public GeneralResult<Void> deleteClient(String clientName){
        if (StringUtils.isBlank(clientName)) return new GeneralResult<>(false, null, "参数错误");
        initService.deleteZKClient(clientName);
        return new GeneralResult<>(true, null, "添加成功");
    }

}
