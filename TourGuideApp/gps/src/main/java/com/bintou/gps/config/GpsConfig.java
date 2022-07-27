package com.bintou.gps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;

@Configuration
public class GpsConfig {

    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }

}
