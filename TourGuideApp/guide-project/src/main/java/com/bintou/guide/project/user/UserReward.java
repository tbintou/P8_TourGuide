package com.bintou.guide.project.user;

import com.bintou.guide.project.model.location.Attraction;
import com.bintou.guide.project.model.location.VisitedLocation;

public class UserReward {

    public final VisitedLocation visitedLocation;
    public final Attraction attraction;
    private int rewardPoints;


    public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
        this.rewardPoints = rewardPoints;
    }

    public UserReward(VisitedLocation visitedLocation, Attraction attraction) {
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

}
