package com.example.oie.config;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import com.okta.idx.sdk.api.client.IDXAuthenticationWrapper;

@Configuration
public class AppConfig {
	
	@Bean
    public IDXAuthenticationWrapper idxAuthenticationWrapper() {
        return new IDXAuthenticationWrapper();
    }
	

}
