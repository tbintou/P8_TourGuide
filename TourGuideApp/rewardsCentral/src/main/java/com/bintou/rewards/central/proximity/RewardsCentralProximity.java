package com.bintou.rewards.central.proximity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(value = "rewardsCentral", url = "${rewardsCentral.serviceUrl}")
public interface RewardsCentralProximity {

    @GetMapping("/rewardpoints/{attractionId}/{userId}")
    public int getAttractionRewardPoints(
                                            @PathVariable("attractionId") UUID attractionId,
                                            @PathVariable("userId") UUID userId
                                        );

}
