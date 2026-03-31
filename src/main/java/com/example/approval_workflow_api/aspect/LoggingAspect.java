package com.example.approval_workflow_api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.example.approval_workflow_api.service..*(..))")
    public Object logBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("method={} args={} duration={}ms",
                    joinPoint.getSignature().getName(), // メソッド名
                    java.util.Arrays.toString(joinPoint.getArgs()), // 引数
                    duration // 処理時間
            );
        }
    }
}