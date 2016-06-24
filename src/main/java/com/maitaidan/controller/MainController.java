package com.maitaidan.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.maitaidan.aop.ProcessArgs;
import com.maitaidan.model.GeneralResult;
import com.maitaidan.service.InitService;
import com.maitaidan.service.ZKClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Crytis on 2016/5/10.
 * Just test.
 */
@Lazy
@RestController
public class MainController {
    private static Logger logger = LoggerFactory.getLogger(MainController.class);
    @Resource
    List<CuratorFramework> curatorFrameworkList;
    @Resource
    InitService initService;

    /**
     * 如果通过接口添加了，这个是所有的
     */
    private Map<String, CuratorFramework> allCuratorFramework;

    @ProcessArgs
    @RequestMapping("getNode")
    public GeneralResult<List> getNodes(String path, String clientParent) {
//        allCuratorFramework = initService.getAllBeans(CuratorFramework.class);
        if (StringUtils.isAnyBlank(clientParent)) {
            throw new IllegalArgumentException("clientParent父zk节点是空");
        }
        List<String> znodes;
//        CuratorFramework curatorFramework = allCuratorFramework.get(clientParent);
        CuratorFramework curatorFramework = ZKClientUtil.getCurrentClient();
        Preconditions.checkNotNull(curatorFramework);
        try {
            znodes = curatorFramework.getChildren().forPath(path);
        } catch (IllegalArgumentException e) {
            logger.info("路径不存在:{}", path);
            return new GeneralResult<List>(true, Lists.newArrayList(), "查询节点失败");
        } catch (Exception e) {
            logger.error("get znodes Error", e);
            return new GeneralResult<List>(false, Lists.newArrayList(), "查询节点失败");
        }
        return new GeneralResult<List>(true, znodes, "success");
    }

    @RequestMapping("deleteNode")
    public GeneralResult<Void> deleteNode(String path, String clientParent) {
        if (StringUtils.isAnyBlank(path, clientParent)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        try {
            ZKClientUtil.getCurrentClient().delete().deletingChildrenIfNeeded().forPath(path);
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
            CreateBuilder createBuilder = ZKClientUtil.getCurrentClient().create();
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

    @RequestMapping("getClientList")
    public GeneralResult<List> getClientList() {
        List<String> clientList = Lists.newArrayList();
        Map<String, CuratorFramework> allCuratorFrameworks = initService.getAllBeans(CuratorFramework.class);
        for (Map.Entry<String, CuratorFramework> curatorFrameworkEntry : allCuratorFrameworks.entrySet()) {
            clientList.add(curatorFrameworkEntry.getValue().getZookeeperClient().getCurrentConnectionString());
        }
        return new GeneralResult<List>(true, clientList, "success");
    }

    @ProcessArgs
    @RequestMapping("addClient")
    public GeneralResult<Void> addClientList(String address) {
        if (StringUtils.isBlank(address)) return new GeneralResult<>(false, null, "参数错误");
        initService.addZKClient(address);
        return new GeneralResult<>(true, null, "添加成功");
    }


    @RequestMapping("modifyValue")
    public GeneralResult<Void> modifyValue(String path, String value) {
        if (StringUtils.isAnyEmpty(path, value)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        try {
            ZKClientUtil.getCurrentClient().setData().forPath(path, value.getBytes());
        } catch (Exception e) {
            logger.error("delete node error", e);
            return new GeneralResult<>(false, null, "修改失败");

        }
        return new GeneralResult<>(true, null, "success");

    }

    @RequestMapping("search")
    public GeneralResult<List> searchPath(String keyword) {
        return new GeneralResult<List>(true, Lists.newArrayList("aaa", "aab"), "success");
    }

    private String checkPath(String path) {

        if (!StringUtils.startsWith(path, "/")) {
            return "/" + path;

        }
        return path;
    }

}
