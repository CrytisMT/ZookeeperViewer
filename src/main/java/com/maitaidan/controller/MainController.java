package com.maitaidan.controller;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.maitaidan.aop.ProcessArgs;
import com.maitaidan.model.GeneralResult;
import com.maitaidan.model.ZNode;
import com.maitaidan.service.CacheService;
import com.maitaidan.service.InitService;
import com.maitaidan.service.ZKClientContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Resource
    CacheService cacheService;


    @ProcessArgs
    @RequestMapping("getNode")
    public GeneralResult<List> getNodes(String path, String clientParent) throws Exception {
        if (StringUtils.isAnyBlank(clientParent)) {
            throw new IllegalArgumentException("clientParent父zk节点是空");
        }
        List<String> znodes;
        CuratorFramework curatorFramework = ZKClientContext.getCurrentClient();
        Preconditions.checkNotNull(curatorFramework);
        znodes = curatorFramework.getChildren().forPath(path);
        return new GeneralResult<List>(true, znodes, "success");
    }

    @RequestMapping("deleteNode")
    public GeneralResult<Void> deleteNode(String path, String clientParent) throws Exception {
        if (StringUtils.isAnyBlank(path, clientParent)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        ZKClientContext.getCurrentClient().delete().deletingChildrenIfNeeded().forPath(path);
        return new GeneralResult<>(true, null, "success");

    }

    @RequestMapping("addNode")
    public GeneralResult<Void> addNode(String path, String value) throws Exception {
        if (StringUtils.isAnyBlank(path)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        CreateBuilder createBuilder = ZKClientContext.getCurrentClient().create();
        if (StringUtils.isBlank(value)) {
            createBuilder.forPath(path);
        } else {
            createBuilder.forPath(path, value.getBytes());
        }
        return new GeneralResult<>(true, null, "success");

    }

    @RequestMapping("getNodeData")
    public GeneralResult<ZNode> getNodeDate(String path, String clientParent) throws Exception {
        if (StringUtils.isAnyBlank(path, clientParent)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        CuratorFramework currentClient = ZKClientContext.getCurrentClient();
        // TODO: 2016/7/5
        System.out.println(ZKUtil.listSubTreeBFS(currentClient.getZookeeperClient().getZooKeeper(), path));
        byte[] dataBytes = currentClient.getData().forPath(path);
        List<ACL> aclList = currentClient.getACL().forPath(path);
        Stat stat = currentClient.getZookeeperClient().getZooKeeper().exists(path, false);
        logger.info("acl:{}", aclList);
        ZNode zNode = new ZNode(path, new String(dataBytes), aclList, stat);
        return new GeneralResult<>(true, zNode, "success");
    }

    @RequestMapping("getClientList")
    public GeneralResult<List> getClientList() {
        List<String> clientList = Lists.newArrayList();
        Map<String, CuratorFramework> allCuratorFrameworks = InitService.getAllBeans(CuratorFramework.class);
        for (Map.Entry<String, CuratorFramework> curatorFrameworkEntry : allCuratorFrameworks.entrySet()) {
            clientList.add(curatorFrameworkEntry.getValue().getZookeeperClient().getCurrentConnectionString());
        }
        return new GeneralResult<List>(true, clientList, "success");
    }



    @RequestMapping("modifyValue")
    public GeneralResult<Void> modifyValue(String path, String value) throws Exception {
        if (StringUtils.isAnyEmpty(path, value)) {
            return new GeneralResult<>(false, null, "参数不能为空");
        }
        ZKClientContext.getCurrentClient().setData().forPath(path, value.getBytes());
        return new GeneralResult<>(true, null, "success");

    }

    @RequestMapping("search")
    public GeneralResult<Map> searchPath(String keyword) {
        Map<String, Set<String>> searchResult = cacheService.search(keyword);
        return new GeneralResult<Map>(true, searchResult, "success");
    }

    private String checkPath(String path) {

        if (!StringUtils.startsWith(path, "/")) {
            return "/" + path;

        }
        return path;
    }

}
