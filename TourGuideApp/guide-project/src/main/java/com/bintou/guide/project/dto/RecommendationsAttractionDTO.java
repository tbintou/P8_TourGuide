package com.bintou.guide.project.dto;


import com.bintou.guide.project.model.location.Location;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecommendationsAttractionDTO {

    @JsonProperty("userLocation")
    private Location userLocation;
    @JsonProperty("recommendAttractions")
    private List<RecommendationsAttraction> attractionList;

    public RecommendationsAttractionDTO() {
    }

    public RecommendationsAttractionDTO(Location userLocation, List<RecommendationsAttraction> attractionList) {
        this.userLocation = userLocation;
        this.attractionList = attractionList;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public List<RecommendationsAttraction> getAttractionList() {
        return attractionList;
    }

    public void setAttractionList(List<RecommendationsAttraction> attractionList) {
        this.attractionList = attractionList;
    }

}
