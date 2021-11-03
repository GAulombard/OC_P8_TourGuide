package com.api.rewardcentral.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

@Configuration
public class RewardCentralConfig {

    @Bean
    public RewardCentral getGpsUtil() {
        return new  RewardCentral();
    }

}
