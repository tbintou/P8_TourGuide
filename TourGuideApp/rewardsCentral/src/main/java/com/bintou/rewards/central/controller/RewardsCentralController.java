package com.bintou.rewards.central.controller;

import com.bintou.rewards.central.service.RewardsCentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class RewardsCentralController{

    @Autowired
    private RewardsCentralService rewardsCentralService;

    @GetMapping("/rewardpoints/{attractionId}/{userId}")
    public int getAttractionRewardPoints(
                                            @PathVariable("attractionId") UUID attractionId,
                                            @PathVariable("userId") UUID userId
                                         ) {
        return rewardsCentralService.getAttractionRewardPoints(attractionId, userId);
    }

}
