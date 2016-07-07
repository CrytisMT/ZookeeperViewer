package com.maitaidan.service;

import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by Crytis on 2016/6/26.
 */
@ContextConfiguration("classpath:application-context.xml")
public class CacheServiceTest extends AbstractJUnit4SpringContextTests {
    @Resource
    CacheService cacheService;
    @Test
    public void getALlNodes() throws Exception {
        Map<String, CuratorFramework> allBeans = InitService.getAllBeans(CuratorFramework.class);
        CuratorFramework next = allBeans.values().iterator().next();
//        Multimap<String, String> aLlNodes = cacheService.getALlNodes(next, "/");
//        System.out.println(aLlNodes);
    }

}