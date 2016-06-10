package com.maitaidan.controller;

import com.google.common.collect.Lists;
import com.maitaidan.model.GeneralResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Crytis on 2016/5/10.
 * Just test.
 */
@Controller
@RequestMapping("a")
public class MainController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    CuratorFramework curatorFramework;

    @ResponseBody
    @RequestMapping("getNode")
    public GeneralResult<List> getNodes(String path) {
        if (StringUtils.isBlank(path)) {
            path = "/";
            logger.info("path为空，设置为根目录");
        }
        List<String> znodes = null;
        try {
            znodes = curatorFramework.getChildren().forPath(path);
        } catch (Exception e) {
            logger.error("get znodes error", e);
            return new GeneralResult<List>(false, Lists.newArrayList(), "查询节点失败");
        }
        return new GeneralResult<List>(true, znodes, "success");
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
        CuratorFramework bean = applicationContext.getBean(CuratorFramework.class);
        List<String> strings = bean.getChildren().forPath("/");
        System.out.println(strings);
    }

}
