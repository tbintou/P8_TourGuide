package com.bintou.guide.project.dto;

import com.bintou.guide.project.model.location.Location;

public class RecommendationsAttraction {

    private String name;
    private Location location;
    private double distance;
    private int rewardPoints;


    public RecommendationsAttraction() {
    }

    public RecommendationsAttraction(String name, Location location, double distance, int rewardPoints) {
        this.name = name;
        this.location = location;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(double latitude, double longitude) {
        this.location = new Location(latitude, longitude);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
