package com.bintou.trip.pricer.proximity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;


@FeignClient(value = "tripPricer", url = "${tripPricer.serviceUrl}")
public interface TripPricerProximity {
    @GetMapping("/trip/{apiKey}/{attractionId}/{adults}/{children}/{nightsStay}/{rewardsPoints}")
    public List<Provider> getPrice(
                                    @PathVariable("apiKey") String apiKey,
                                    @PathVariable("attractionId") UUID attractionId,
                                    @PathVariable("adults") int adults,
                                    @PathVariable("children") int children,
                                    @PathVariable("nightsStay") int nightsStay,
                                    @PathVariable("rewardsPoints") int rewardsPoints
                                 );
}
