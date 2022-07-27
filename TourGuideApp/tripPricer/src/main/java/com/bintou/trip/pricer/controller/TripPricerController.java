package com.bintou.trip.pricer.controller;

import com.bintou.trip.pricer.service.TripPricerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@RestController
public class TripPricerController {

    @Autowired
    private TripPricerService tripPricerService;

    @GetMapping("/trip/{apiKey}/{attractionId}/{adults}/{children}/{nightsStay}/{rewardsPoints}")
    public List<Provider> getPrice(
                                    @PathVariable("apiKey") String apiKey,
                                    @PathVariable("attractionId") UUID attractionId,
                                    @PathVariable("adults") int adults,
                                    @PathVariable("children") int children,
                                    @PathVariable("nightsStay") int nightsStay,
                                    @PathVariable("rewardsPoints") int rewardsPoints
                                ) {
        return tripPricerService.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

}
