package com.api.rewardcentral.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

/**
 * The type Reward central config.
 */
@Configuration
public class RewardCentralConfig {

    /**
     * Gets gps util.
     *
     * @return the gps util
     */
    @Bean
    public RewardCentral getGpsUtil() {
        return new  RewardCentral();
    }

}
