package me.springbootlearn.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by reimi on 11/19/17.
 */
public class ServiceInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("Hello Interceptor!");
        return invocation.proceed();
    }
}
