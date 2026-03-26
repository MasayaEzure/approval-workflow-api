package com.example.approval_workflow_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.approval_workflow_api.filter.RequestIdFilter;
import com.example.approval_workflow_api.filter.AccessLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilter() {
        FilterRegistrationBean<RequestIdFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new RequestIdFilter());  // RequestIdFilterをフィルターとして登録
        registration.addUrlPatterns("/api/*"); // /api以下のすべてのエンドポイントに適用

        return registration;
    }

    @Bean
    public FilterRegistrationBean<AccessLogFilter> accessLogFilter() {
        FilterRegistrationBean<AccessLogFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(new AccessLogFilter()); // AccessLogFilterをフィルターとして登録
        registration.addUrlPatterns("/api/*"); // /api以下のすべてのエンドポイントに適用    

        return registration;
    }
}