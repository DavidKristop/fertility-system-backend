package com.group3.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @Bean
    @Primary
    public TimeZone defaultTimeZone() {
        return TimeZone.getTimeZone("Asia/Bangkok");
    }

    @Bean
    @Primary
    public ZoneId defaultZoneId() {
        return ZoneId.of("Asia/Bangkok");
    }
}
