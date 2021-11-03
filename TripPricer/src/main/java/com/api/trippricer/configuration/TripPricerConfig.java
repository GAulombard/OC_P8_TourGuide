package com.api.trippricer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tripPricer.TripPricer;

@Configuration
public class TripPricerConfig {

    @Bean
    public TripPricer getGpsUtil() {
        return new  TripPricer();
    }

}
