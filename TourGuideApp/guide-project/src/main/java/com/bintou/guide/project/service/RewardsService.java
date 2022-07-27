package com.bintou.guide.project.service;

import com.bintou.guide.project.model.location.Attraction;
import com.bintou.guide.project.model.location.Location;
import com.bintou.guide.project.model.location.VisitedLocation;
import com.bintou.guide.project.proximity.GpsProximity;
import com.bintou.guide.project.proximity.RewardsCentralProximity;
import com.bintou.guide.project.user.User;
import com.bintou.guide.project.user.UserReward;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;
    private final GpsProximity gpsProximity;
    private final RewardsCentralProximity rewardsCentralProximity;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(1,5,10, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    public RewardsService(GpsProximity gpsProximity, RewardsCentralProximity rewardsCentralProximity) {
        this.gpsProximity = gpsProximity;
        this.rewardsCentralProximity = rewardsCentralProximity;
    }

    public List<Attraction> getAllAttractions(){
        return gpsProximity.getAttractions();
    }

    public int getAttractionRewardPoints(UUID attractionId, UUID userId){
        return rewardsCentralProximity.getAttractionRewardPoints(attractionId, userId);
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    public List<UserReward> calculateRewards(User user) {
        List<VisitedLocation> userLocations = user.getVisitedLocations();
        List<Attraction> attractionList = getAllAttractions();

        List<VisitedLocation> locationList = new CopyOnWriteArrayList<>(userLocations);
        List<UserReward> userRewardList = new ArrayList<>();

        for (Attraction attraction : attractionList) {
            for (VisitedLocation visitedLocation : locationList) {
                if (nearAttraction(visitedLocation, attraction)){
                    if (user.getUserRewards().stream().noneMatch(userReward -> userReward.attraction.attractionName.equals(attraction.attractionName))){
                        UserReward userReward = new UserReward(visitedLocation, attraction, 0);
                        user.addUserReward(userReward);
                    }
                }
            }
        }
        userRewardList.stream().map(userReward -> CompletableFuture.supplyAsync(() -> getRewardPoints(userReward.attraction, user), executor)
                .thenAccept((points) -> user.addUserReward(new UserReward(user.getLastVisitedLocation(), userReward.attraction, points))));

        return user.getUserRewards();
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Attraction location) {
        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    int getRewardPoints(Attraction attraction, User user) {
        return getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}
