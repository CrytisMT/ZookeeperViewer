package com.maitaidan.service;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
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
    private Logger logger = LoggerFactory.getLogger(InitService.class);
    private RetryForever retryForever = new RetryForever(1000);
    private static ApplicationContext applicationContext;

    @Value("#{'${zk.address}'.split('\\|')}")
    private List<String> servers;
    @Resource
    CacheService cacheService;

    /**
     * bean name是zk地址
     */
    @PostConstruct
    public void initCuratorFramework() {
        for (String server : servers) {
            logger.info("创建{}连接实例...", server);
            CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(server, retryForever);
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


    public void addZKClient(String address) {
        Preconditions.checkArgument(StringUtils.isNotBlank(address), "新增zk地址为空！");
        CuratorFramework bean = null;
        try {
            bean = applicationContext.getBean(address, CuratorFramework.class);
            if (bean != null) {
                logger.info("{}已经存在，不再添加", address);
                return;
            }
        } catch (NoSuchBeanDefinitionException e) {
            logger.info("添加连接{}", address);
        }

        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(address, retryForever);
        curatorFramework.start();
        logger.info("{}启动成功...",address);
        registerBeanToSpring(address, curatorFramework);
    }

    public static  <T> Map<String, T> getAllBeans(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }
}
