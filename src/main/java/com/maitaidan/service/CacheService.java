package com.maitaidan.service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import sun.security.action.GetLongAction;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Crytis on 2016/6/19.
 * 缓存各个节点
 * 延迟加载。等待zk先连接
 */
@Slf4j
@Lazy
@Service
public class CacheService {

    @Value("${LoadCacheOnStart}")
    private String loadCache;
    //key 是connectString，value的key是node名，value的value是完整path
    private HashMap<String, Multimap<String, String>> pathCache = Maps.newHashMap();

    public void reloadAll() {
        if (!Boolean.valueOf(loadCache)) return;

        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("reload cache start...");
        Map<String, CuratorFramework> allCuratorFrameworks = InitService.getAllBeans(CuratorFramework.class);
        for (Map.Entry<String, CuratorFramework> curatorFrameworkEntry : allCuratorFrameworks.entrySet()) {
            String key = curatorFrameworkEntry.getKey();
            CuratorFramework curatorFramework = curatorFrameworkEntry.getValue();
            Multimap<String, String> aLlNodes = getALlNodes(curatorFramework, "/");
            pathCache.put(key, aLlNodes);
        }
        log.info("cache加载完毕，耗时:{}",stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * todo 多线程
     * @param client
     * @param path
     * @return
     */
    public Multimap<String, String> getALlNodes(CuratorFramework client, String path) {
        HashMultimap<String, String> multimap = HashMultimap.create();
        List<String> childrenNodes;
        try {
            childrenNodes = client.getChildren().forPath(path);

            if (childrenNodes.size() < 1) {
                return multimap;
            } else {
                for (String childrenNode : childrenNodes) {
                    String childrenPath = (path.equals("/") ? "" : path) + "/" + childrenNode;
                    multimap.put(childrenNode, childrenPath);
                    multimap.putAll(getALlNodes(client, childrenPath));
                }
                return multimap;
            }
        } catch (Exception e) {
            log.error("get all nodes error", e);
            return multimap;
        }
    }

    /**
     *
     * @param keyword
     * @return zk名:完整path
     * todo 增量更新
     */
    public Map<String, Set<String>> search(String keyword) {
        HashMap<String, Set<String>> result = Maps.newHashMap();
        if (StringUtils.isBlank(keyword)) return result;

        for (Map.Entry<String, Multimap<String, String>> entry : pathCache.entrySet()) {
            Multimap<String, String> nodeAndPath = entry.getValue();
            HashSet<String> pathsSet = Sets.newHashSet();
            for (String key : nodeAndPath.keySet()) {
                //从path中匹配
                for (String fullPath : nodeAndPath.get(key)) {
                    if (fullPath.contains(keyword)) {
                        pathsSet.add(fullPath);
                    }
                }
            }
            if (!pathsSet.isEmpty()) {
                result.put(entry.getKey(), pathsSet);
            }
        }
        return result;
    }
}
