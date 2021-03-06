package com.maitaidan.interceptor;

import com.maitaidan.service.InitService;
import com.maitaidan.service.ZKClientContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Crytis on 2016/6/24.
 * 拦截器,设置当前的client.
 */
@Slf4j
public class Interceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientParent = request.getParameter("clientParent");
        if (clientParent == null) {
            log.info("请求{}没有clientParent，不进行设置client", request.getRequestURI());
            return true;
        } else {
            CuratorFramework client = InitService.getAllBeans(CuratorFramework.class).get(clientParent);
            if (client == null) {
                return false;
            } else {
                log.debug("为连接{}设置client", clientParent);
                ZKClientContext.setCurrentClient(client);
            }
        }
        return true;
    }
}
