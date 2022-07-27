package com.bintou.rewards.central.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewardCentral.RewardCentral;

@Configuration
public class RewardsCentralConfig {

    @Bean
    public RewardCentral getRewardCentral() {
            return new RewardCentral();
    }
}
