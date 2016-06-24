package com.maitaidan.aop;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by Crytis on 2016/6/24.
 * 处理通用的参数path等.
 */
@Component
public class ControllerAspect {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Object processArgs(ProceedingJoinPoint joinPoint)  throws Throwable{
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length < 1) {
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                logger.error("controller aop Error!",e);
            }
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (StringUtils.equals("path", parameterNames[i]) && (args != null ? args[i].getClass() : null) == String.class && StringUtils.isBlank((String) args[i])) {
                args[i] = "/";
                logger.info("设置path为/");
            }
        }
        try {
            return joinPoint.proceed(args);
        } catch (Exception e) {
            logger.error("controller aop Error!",e);
            throw e;
        }

    }
}
