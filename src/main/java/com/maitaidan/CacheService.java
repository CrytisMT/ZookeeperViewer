package com.maitaidan;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Crytis on 2016/6/19.
 */
@Service
public class CacheService {
    @Resource
    CuratorFramework curatorFramework;

    private LoadingCache<String, String> cache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
        @Override
        public String load(String key) throws Exception {
            return null;
        }
    });
}
