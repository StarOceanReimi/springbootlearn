package me.springbootlearn.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

/**
 * Created by reimi on 11/19/17.
 */
@Aspect
@Component
public class ServiceTracerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTracerAdvice.class);

    @Pointcut("execution(* me.springbootlearn.repository.ArticleServiceImpl.newArticle(String))")
    public void servicePointCut() {};

    @Around("servicePointCut()")
    public Object timeSpent(ProceedingJoinPoint joinPoint) throws Throwable {

        String desc = joinPoint.getSignature().toShortString();
        LOGGER.debug("ENTER SERVICE: {}", desc);
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(joinPoint.getArgs());
        long timeSpent = System.currentTimeMillis()-start;
        LOGGER.debug("LEAVE SERVICE: {} spent {} ms", desc, timeSpent);
        return result;
    }

}
