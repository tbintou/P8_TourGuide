package com.bintou.guide.project.service;

import com.bintou.guide.project.dto.RecommendationsAttraction;
import com.bintou.guide.project.dto.RecommendationsAttractionDTO;
import com.bintou.guide.project.dto.UserPreferencesDTO;
import com.bintou.guide.project.helper.InternalTestHelper;
import com.bintou.guide.project.model.location.Attraction;
import com.bintou.guide.project.model.location.Location;
import com.bintou.guide.project.model.location.VisitedLocation;
import com.bintou.guide.project.model.tripPricer.Provider;
import com.bintou.guide.project.proximity.GpsProximity;
import com.bintou.guide.project.proximity.TripPricerProximity;
import com.bintou.guide.project.tracker.Tracker;
import com.bintou.guide.project.user.User;
import com.bintou.guide.project.user.UserPreferences;
import com.bintou.guide.project.user.UserReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    //private final GpsUtil gpsUtil;
    private final GpsProximity gpsProximity;
    private final RewardsService rewardsService;
   // private final TripPricer tripPricer = new TripPricer();
    private final TripPricerProximity tripPricerProximity;
    public final Tracker tracker;
    boolean testMode = true;

    public TourGuideService(GpsProximity gpsProximity, RewardsService rewardsService, TripPricerProximity tripPricerProximity) {
        this.gpsProximity = gpsProximity;
        this.tripPricerProximity = tripPricerProximity;
        this.rewardsService = rewardsService;

        if(testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    public List<Attraction> getAllAttractions(){
        List<Attraction> attractionList = gpsProximity.getAttractions();
        return attractionList;
    }


    public VisitedLocation getUserLocation(UUID userId){
        return gpsProximity.getUserLocation(userId);
    }

    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints){
        return tripPricerProximity.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

    ThreadPoolExecutor executor = new ThreadPoolExecutor(1,5,10, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
            );

    public List<UserReward> getUserRewards(User user){
        return user.getUserRewards();
    }

    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    public Map<String, Location> getAllCurrentLocations(){
        Map<String, Location> locationMap = new ConcurrentHashMap<>();
        getAllUsers().forEach(user -> {
            locationMap.put(user.getUserId().toString(), user.getLastVisitedLocation().location);
        });
        return locationMap;
    }

    public void addUser(User user) {
        if(!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    public VisitedLocation trackUserLocation(User user) {
        CompletableFuture<VisitedLocation> future = CompletableFuture.supplyAsync(() -> getUserLocation(user.getUserId()), executor)
                .thenApply(visitedLocation -> {
                    user.addToVisitedLocations(visitedLocation);
                    rewardsService.calculateRewards(user);
                    return visitedLocation;
                });
        VisitedLocation visitedLocation = future.join();
        return visitedLocation;
    }

    public UserPreferences getUserPreferences(String userName){
        return getUser(userName).getUserPreferences();
    }

    public UserPreferences updateUserPreferences(UserPreferencesDTO userPreferencesDTO){
        String userName = userPreferencesDTO.getUserName();
        User user = getUser(userName);
        UserPreferences userExisting = user.getUserPreferences();

        if (userPreferencesDTO.getHighPricePoint() != null){
            userExisting.setHighPricePoint(userPreferencesDTO.getHighPricePoint());
        }
        if (userPreferencesDTO.getLowerPricePoint() != null){
            userExisting.setLowerPricePoint(userPreferencesDTO.getLowerPricePoint());
        }
        if(userPreferencesDTO.getNumberOfAdults() > 0){
            userExisting.setNumberOfAdults(userPreferencesDTO.getNumberOfAdults());
        }
        if(userPreferencesDTO.getNumberOfChildren() > 0){
            userExisting.setNumberOfChildren(userPreferencesDTO.getNumberOfChildren());
        }
        if(userPreferencesDTO.getTicketQuantity() > 0){
            userExisting.setTicketQuantity(userPreferencesDTO.getTicketQuantity());
        }
        if (userPreferencesDTO.getTripDuration() > 0){
            userExisting.setTripDuration(userPreferencesDTO.getTripDuration());
        }
        return userExisting;
    }

    public RecommendationsAttractionDTO getRecommendationsAttractions(String userName){
        RecommendationsAttractionDTO recommendationsAttractionDTO = new RecommendationsAttractionDTO();

        User user = getUser(userName);
        VisitedLocation visitedLocation = getUser(userName).getLastVisitedLocation();
        Location location = visitedLocation.location;

        List<RecommendationsAttraction> attractionList = new CopyOnWriteArrayList<>();
        List<Attraction> proximityAttractions = getNearByAttractions(visitedLocation);
        for (Attraction attraction : proximityAttractions) {
            RecommendationsAttraction recommendationsAttraction = new RecommendationsAttraction();
            recommendationsAttraction.setName(attraction.attractionName);
            recommendationsAttraction.setLocation(attraction.latitude, attraction.longitude);
            recommendationsAttraction.setDistance(rewardsService.getDistance(attraction, location));
            recommendationsAttraction.setRewardPoints(rewardsService.getRewardPoints(attraction, user));
            attractionList.add(recommendationsAttraction);
        }
        recommendationsAttractionDTO.setUserLocation(location);
        recommendationsAttractionDTO.setAttractionList(attractionList);

        return recommendationsAttractionDTO;
    }

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = getAllAttractions();
        return nearbyAttractions.stream()
                .sorted(Comparator.comparing(attraction -> rewardsService.getDistance(visitedLocation.location, attraction)))
                .limit(5)
                .collect(Collectors.toList());
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i-> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
