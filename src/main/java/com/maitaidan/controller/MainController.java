package com.maitaidan.controller;

import com.maitaidan.model.GeneralResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Crytis on 2016/5/10.
 * Just test.
 */
@Slf4j
@RestController
@RequestMapping("/")
public class MainController {



    @RequestMapping("getNode")
    public GeneralResult<List> getNodes(String path) {
        if (StringUtils.isBlank(path)) {
            path = "/";
//            log.info("path为空，设置为根目录");
        }
        CuratorFramework client = CuratorFrameworkFactory.newClient("zk.dev.corp.qunar.com:2181,zk.dev.qunar.com:2181,l-zk1.plat.dev.cn6.qunar.com:2181", new RetryNTimes(3, 300));
        client.start();
        GetDataBuilder data = client.getData();
        return null;
    }

}
