package com.maitaidan.service;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Crytis on 2016/6/19.
 * 缓存各个节点
 * 延迟加载。等待zk先连接
 */
@Lazy
@Service
public class CacheService {
    @Resource
    CuratorFramework curatorFramework;


}
