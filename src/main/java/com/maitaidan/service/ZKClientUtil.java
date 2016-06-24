package com.maitaidan.service;

import org.apache.curator.framework.CuratorFramework;

/**
 * Created by Crytis on 2016/6/24.
 * 存放当前的client
 */
public class ZKClientUtil {

    private static InheritableThreadLocal<CuratorFramework> currentClient = new InheritableThreadLocal<>();

    public static void setCurrentClient(CuratorFramework client) {
        currentClient.set(client);
    }

    public static CuratorFramework getCurrentClient() {
        return currentClient.get();
    }
}
