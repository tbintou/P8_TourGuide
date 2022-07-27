package com.bintou.guide.project.Controller;

import com.bintou.guide.project.dto.RecommendationsAttractionDTO;
import com.bintou.guide.project.dto.UserPreferencesDTO;
import com.bintou.guide.project.model.location.Location;
import com.bintou.guide.project.model.location.VisitedLocation;
import com.bintou.guide.project.model.tripPricer.Provider;
import com.bintou.guide.project.service.TourGuideService;
import com.bintou.guide.project.user.User;
import com.bintou.guide.project.user.UserPreferences;
import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @RequestMapping("/guide-project")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    //  TODO: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        RecommendationsAttractionDTO attractionDTO = tourGuideService.getRecommendationsAttractions(userName);
        return JsonStream.serialize(attractionDTO);
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        // TODO: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }
        Map<String, Location> allCurrentLocations = tourGuideService.getAllCurrentLocations();
        return JsonStream.serialize(allCurrentLocations);
    }

    @RequestMapping("/getUserPreferences")
    public String getUserPreferences(@RequestParam String userName) {
        UserPreferences userPreferences = tourGuideService.getUserPreferences(userName);
        return JsonStream.serialize(userPreferences);
    }

    @PostMapping("/updateUserPreferences")
    public String updateUserPreferences(@RequestBody UserPreferencesDTO userPreferencesDto) {
        UserPreferences userPreferences = tourGuideService.updateUserPreferences(userPreferencesDto);
        return JsonStream.serialize(userPreferences);
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }

}
