package com.api.trippricer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tripPricer.TripPricer;

/**
 * The type Trip pricer config.
 */
@Configuration
public class TripPricerConfig {

    /**
     * Gets gps util.
     *
     * @return the gps util
     */
    @Bean
    public TripPricer getGpsUtil() {
        return new  TripPricer();
    }

}
