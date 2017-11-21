package me.springbootlearn.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.springbootlearn.aop.ServiceInterceptor;
import me.springbootlearn.aop.ServiceTracerAdvice;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by reimi on 11/19/17.
 */
@Configuration
@ComponentScan(basePackages = {"me.springbootlearn.aop", "me.springbootlearn.repository"})
@EnableAspectJAutoProxy
public class ServiceConfig {

    @Bean
    public ObjectMapper serviceMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
              .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper;
    }

    @Bean
    public ObjectMapper strictMapper() {
        return new ObjectMapper();
    }

    public ServiceInterceptor interceptor() {
        return new ServiceInterceptor();
    }

    public BeanNameAutoProxyCreator proxyCreator() {
        BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
        creator.setBeanNames("*ServiceImpl");
        creator.setInterceptorNames("interceptor");
        return creator;
    }
}
