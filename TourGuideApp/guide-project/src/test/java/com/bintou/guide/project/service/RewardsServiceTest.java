package com.bintou.guide.project.service;

import com.bintou.guide.project.helper.InternalTestHelper;
import com.bintou.guide.project.model.location.Attraction;
import com.bintou.guide.project.model.location.VisitedLocation;
import com.bintou.guide.project.proximity.GpsProximity;
import com.bintou.guide.project.proximity.RewardsCentralProximity;
import com.bintou.guide.project.proximity.TripPricerProximity;
import com.bintou.guide.project.user.User;
import com.bintou.guide.project.user.UserReward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootTest
public class RewardsServiceTest {

    @Autowired
    private GpsProximity gpsProximity;
    @Autowired
    private RewardsCentralProximity rewardsCentralProximity;
    @Autowired
    private TripPricerProximity tripPricerProximity;

    @BeforeEach
    public void init() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void getRewardPointsTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity,rewardsService,tripPricerProximity);

        User user = new User(UUID.randomUUID(), "Bernard", "111", "bernard@tourGuide.com");
        Attraction attraction = gpsProximity.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewardList = user.getUserRewards();
        tourGuideService.tracker.stopTracking();
        System.out.println("rewards point = " + userRewardList.size());
        Assertions.assertTrue(userRewardList.size() == 1);
    }

    @Test
    public void isWithinAttractionProximityTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        Attraction attraction = gpsProximity.getAttractions().get(0);
        Assertions.assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    @Test
    public void nearAttractionTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
        List<UserReward> rewardList = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(gpsProximity.getAttractions().size(), rewardList.size());
    }
}
