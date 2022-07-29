package com.bintou.guide.project.performance;

import com.bintou.guide.project.helper.InternalTestHelper;
import com.bintou.guide.project.model.location.Attraction;
import com.bintou.guide.project.model.location.VisitedLocation;
import com.bintou.guide.project.proximity.GpsProximity;
import com.bintou.guide.project.proximity.RewardsCentralProximity;
import com.bintou.guide.project.proximity.TripPricerProximity;
import com.bintou.guide.project.service.RewardsService;
import com.bintou.guide.project.service.TourGuideService;
import com.bintou.guide.project.user.User;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class PerformanceTest {

    /*
     * A note on performance improvements:
     *
     *     The number of users generated for the high volume tests can be easily adjusted via this method:
     *
     *     		InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     *     These tests can be modified to suit new solutions, just as long as the performance metrics
     *     at the end of the tests remains consistent.
     *
     *     These are performance metrics that we are trying to hit:
     *
     *     highVolumeTrackLocation: 100,000 users within 15 minutes:
     *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
     *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */

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
    public void highVolumeTrackLocationTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(100000);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        List<User> userList = new ArrayList<>();
        userList = tourGuideService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        userList.parallelStream().map(tourGuideService::trackUserLocation);
        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeTrackLocation : Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewardsTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(100000);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        Attraction attraction = gpsProximity.getAttractions().get(0);
        List<User> userList = new ArrayList<>();
        userList = tourGuideService.getAllUsers();

        userList.forEach(user -> user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date())));
        userList.parallelStream().forEach(rewardsService::calculateRewards);

        for (User user : userList) {
            Assertions.assertTrue(user.getUserRewards().size() > 0);
        }
        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}
