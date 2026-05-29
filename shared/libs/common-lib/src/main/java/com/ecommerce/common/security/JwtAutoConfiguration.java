package com.ecommerce.common.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    @Bean
    public JwtService jwtService(JwtProperties properties) {
        return new JwtService(properties);
    }
}
