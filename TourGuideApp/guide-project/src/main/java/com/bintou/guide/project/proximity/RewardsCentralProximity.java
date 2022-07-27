package com.bintou.guide.project.proximity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Component
@FeignClient(value = "rewardsCentral", url = "${rewardsCentral.serviceUrl}")
public interface RewardsCentralProximity {

    @GetMapping("/rewardpoints/{attractionId}/{userId}")
    public int getAttractionRewardPoints(
                                            @PathVariable("attractionId") UUID attractionId,
                                            @PathVariable("userId") UUID userId
                                         );

}
