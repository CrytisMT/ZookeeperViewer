package com.maitaidan.service;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Crytis on 2016/6/23.
 * 初始化zk连接
 */
@Service
public class InitService implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Resource
    CacheService cacheService;
    private Logger logger = LoggerFactory.getLogger(InitService.class);
    private RetryNTimes retryPolicy = new RetryNTimes(3, 1000);
    @Value("#{'${zk.address}'.split('\\|')}")
    private List<String> servers;

    public static <T> Map<String, T> getAllBeans(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * bean name是zk地址
     */
    @PostConstruct
    public void initCuratorFramework() {
        for (String server : servers) {
            logger.info("创建{}连接实例...", server);
            CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(server, retryPolicy);
            curatorFramework.start();
            registerBeanToSpring(server, curatorFramework);
        }
        logger.info("curatorFramework实例创建完毕...");
        cacheService.reloadAll();

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        InitService.applicationContext = applicationContext;
    }

    private void registerBeanToSpring(String beanName, Object obj) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        ConfigurableListableBeanFactory beanFactory = configurableApplicationContext.getBeanFactory();
        beanFactory.registerSingleton(beanName, obj);
    }

    public void addZKClient(String clientName) throws KeeperException.ConnectionLossException {
        Preconditions.checkArgument(StringUtils.isNotBlank(clientName), "新增zk地址为空！");
        CuratorFramework bean;
        try {
            bean = applicationContext.getBean(clientName, CuratorFramework.class);
            if (bean != null) {
                logger.info("{}已经存在，不再添加", clientName);
                return;
            }
        } catch (NoSuchBeanDefinitionException e) {
            logger.info("添加连接{}", clientName);
        }

        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(clientName, retryPolicy);
        try {
            curatorFramework.start();
            curatorFramework.getChildren().forPath("/zookeeper");
        } catch (Exception e) {
            logger.error("{}添加新zk失败",e.getMessage());
            throw new KeeperException.ConnectionLossException();
        }
        logger.info("{}启动成功...", clientName);
        registerBeanToSpring(clientName, curatorFramework);
        // TODO: 2016/7/7  添加后刷新缓存 且不阻断
        // TODO: 2016/7/7 一个zk链接失效后   阻碍
//        cacheService.reloadAll();
    }

    public void deleteZKClient(String clientName) {

    }
}
