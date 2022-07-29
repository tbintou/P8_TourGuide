package com.bintou.guide.project.service;

import com.bintou.guide.project.dto.UserPreferencesDTO;
import com.bintou.guide.project.helper.InternalTestHelper;
import com.bintou.guide.project.model.location.Attraction;
import com.bintou.guide.project.model.tripPricer.Provider;
import com.bintou.guide.project.proximity.GpsProximity;
import com.bintou.guide.project.proximity.RewardsCentralProximity;
import com.bintou.guide.project.proximity.TripPricerProximity;
import com.bintou.guide.project.user.User;
import com.bintou.guide.project.user.UserPreferences;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.money.Monetary;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootTest
public class TourGuideServiceTest {

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
    public void getUserLocationTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        User user = new User(UUID.randomUUID(), "Bernard", "111", "bernard@tourGuide.com");
        tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();
        Assertions.assertTrue(user.getLastVisitedLocation().userId.equals(user.getUserId()));
    }

    @Test
    public void addUserTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        User user = new User(UUID.randomUUID(), "Bernard", "111", "bernard@tourGuide.com");
        User user1 = new User(UUID.randomUUID(), "Bernard1", "000", "bernard1@tourGuide.com");
        tourGuideService.addUser(user);
        tourGuideService.addUser(user1);

        User recupUser = tourGuideService.getUser(user.getUserName());
        User recupUser1 = tourGuideService.getUser(user1.getUserName());
        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(user, recupUser);
        Assertions.assertEquals(user1, recupUser1);
    }

    @Test
    public void getAllUsersTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        User user = new User(UUID.randomUUID(), "Bernard", "111", "bernard@tourGuide.com");
        User user1 = new User(UUID.randomUUID(), "Bernard1", "000", "bernard1@tourGuide.com");
        tourGuideService.addUser(user);
        tourGuideService.addUser(user1);

        List<User> userList = tourGuideService.getAllUsers();
        tourGuideService.tracker.stopTracking();

        Assertions.assertTrue(userList.contains(user));
        Assertions.assertTrue(userList.contains(user1));
    }

    @Test
    public void updateUserPreferencesTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        User user = new User(UUID.randomUUID(), "Bernard", "111", "bernard@tourGuide.com");
        tourGuideService.addUser(user);

        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setTripDuration(3);
        userPreferences.setTicketQuantity(2);
        userPreferences.setLowerPricePoint(Money.of(200, Monetary.getCurrency("USD")));
        userPreferences.setHighPricePoint(Money.of(800, Monetary.getCurrency("USD")));
        userPreferences.setAttractionProximity(10);
        userPreferences.setNumberOfAdults(2);
        userPreferences.setNumberOfChildren(1);
        user.setUserPreferences(userPreferences);

        UserPreferencesDTO userPreferencesDTO = new UserPreferencesDTO();
        userPreferencesDTO.setUserName(user.getUserName());

        userPreferencesDTO.setNumberOfAdults(3);
        userPreferencesDTO.setNumberOfChildren(2);
        userPreferencesDTO.setTripDuration(10);
        tourGuideService.updateUserPreferences(userPreferencesDTO);

        Assertions.assertEquals(user.getUserPreferences().getNumberOfAdults(), 3);
        Assertions.assertEquals(user.getUserPreferences().getNumberOfChildren(), 2);
        Assertions.assertEquals(user.getUserPreferences().getTripDuration(), 10);
    }

    @Test
    public void trackUserLocationTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        User user = new User(UUID.randomUUID(), "Bernard", "111", "bernard@tourGuide.com");
        tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(user.getUserId(), user.getLastVisitedLocation().userId);
    }

    @Test
    public void getNearByAttractionsTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        User user = new User(UUID.randomUUID(), "Bernard", "111", "bernard@tourGuide.com");
        tourGuideService.trackUserLocation(user);

        List<Attraction> attractionList = tourGuideService.getNearByAttractions(user.getLastVisitedLocation());
        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(attractionList.size(), 5);
    }

    @Test
    public void getTripDealsTest() {
        RewardsService rewardsService = new RewardsService(gpsProximity, rewardsCentralProximity);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsProximity, rewardsService, tripPricerProximity);

        User user = new User(UUID.randomUUID(), "internalUser78", "000", "internalUser78@tourGuide.com");

        List<Provider> providerList = tourGuideService.getTripDeals(user);
        tourGuideService.tracker.stopTracking();

        Assertions.assertEquals(providerList.size(), 5);
    }
}
