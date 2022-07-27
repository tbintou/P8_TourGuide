package com.bintou.guide.project.proximity;

import com.bintou.guide.project.model.tripPricer.Provider;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(value = "tripPricer", url = "${tripPricer.serviceUrl}")
public interface TripPricerProximity {
    @GetMapping("/trip/{apiKey}/{attractionId}/{adults}/{children}/{nightsStay}/{rewardsPoints}")
    public List<Provider> getPrice(
                                     @PathVariable("keyApi") String apiKey,
                                     @PathVariable("attractionId") UUID attractionId,
                                     @PathVariable("adults") int adults,
                                     @PathVariable("kids") int children,
                                     @PathVariable("daysOfStaying") int nightsStay,
                                     @PathVariable("numberOfRewardsPoints") int rewardsPoints
                                   );
}
