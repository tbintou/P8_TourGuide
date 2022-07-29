package com.bintou.rewards.central.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class RewardsCentralControllerTest {

    @Autowired
    private RewardsCentralController rewardsCentralController;

    @Test
    public void getAttractionRewardPointsTest() {
        UUID uuid = UUID.randomUUID();
        System.out.println("uuid = " + uuid); // 0c04503c-f22c-491d-a28d-d33cd5c0f990

        UUID uuid1 = UUID.randomUUID();
        System.out.println("uuid1 = " + uuid1); // 5c7822ab-c16b-4b35-9ed9-35b61b7d5759

        int rewardPointsTest = rewardsCentralController.getAttractionRewardPoints(uuid, uuid1);
        System.out.println("rewardPointsTest = " + rewardPointsTest); //

        Assertions.assertNotNull(rewardPointsTest);
    }
}
