package com.maitaidan.controller;

import com.google.common.collect.Lists;
import com.maitaidan.model.GeneralResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Crytis on 2016/5/10.
 * Just test.
 */
@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    CuratorFramework curatorFramework;

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

    @RequestMapping("deleteNode")
    public GeneralResult<Void> deleteNode(String path) {
        if (StringUtils.isBlank(path)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        try {
            curatorFramework.delete().forPath(path);
        } catch (Exception e) {
            logger.error("delete node error", e);
            return new GeneralResult<>(false, null, "删除失败");

        }
        return new GeneralResult<>(true, null, "success");

    }

    @RequestMapping("addNode")
    public GeneralResult<Void> addNode(String path, String value) {
        if (StringUtils.isAnyBlank(path)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        try {
            CreateBuilder createBuilder = curatorFramework.create();
//            StringUtils.isBlank(value) ? createBuilder.forPath(path) : createBuilder.forPath(path, value.getBytes());
            if (StringUtils.isBlank(value)) {
                createBuilder.forPath(path);
            } else {
                createBuilder.forPath(path, value.getBytes());
            }
        } catch (Exception e) {
            logger.error("delete node error", e);
            return new GeneralResult<>(false, null, "添加失败");

        }
        return new GeneralResult<>(true, null, "success");

    }

    @RequestMapping("modifyValue")
    public GeneralResult<Void> modifyValue(String path, String value) {
        if (StringUtils.isAnyEmpty(path, value)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        try {
            curatorFramework.setData().forPath(path, value.getBytes());
        } catch (Exception e) {
            logger.error("delete node error", e);
            return new GeneralResult<>(false, null, "修改失败");

        }
        return new GeneralResult<>(true, null, "success");

    }


    private String checkPath(String path) {
        if (!StringUtils.startsWith(path, "/")) {
            return "/" + path;

        }
        return path;
    }

    public static void main(String[] args) throws Exception {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
        CuratorFramework bean = applicationContext.getBean(CuratorFramework.class);
        List<String> strings = bean.getChildren().forPath("/");
        System.out.println(strings);
    }

}
