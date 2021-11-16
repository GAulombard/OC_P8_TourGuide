package com.api.gpsutil.configuration;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Gps util config.
 */
@Configuration
public class GpsUtilConfig {

    /**
     * Gets gps util.
     *
     * @return the gps util
     */
    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }
}
