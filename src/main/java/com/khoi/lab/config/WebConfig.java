package com.khoi.lab.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.khoi.lab.service.AccountSessionUpdaterService;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private AccountSessionUpdaterService accountSessionUpdater;

    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accountSessionUpdater)
                .addPathPatterns("/**"); // apply to all URLs
    }
}