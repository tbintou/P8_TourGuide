package com.bintou.trip.pricer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;
import java.util.UUID;

@Service
public class TripPricerService {

    @Autowired
    private TripPricer tripPricer;

    public List<Provider> getPrice(
                                    String apiKey,
                                    UUID attractionId,
                                    int adults,
                                    int children,
                                    int nightsStay,
                                    int rewardsPoints
                                ) {
        return tripPricer.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

}
