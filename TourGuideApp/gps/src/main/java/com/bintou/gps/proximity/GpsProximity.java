package com.bintou.gps.proximity;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(value = "gps", url = "${gps.serviceUrl}")
public interface GpsProximity {

    @GetMapping("/gps/attractions")
    public List<Attraction> getAttractions();

    @GetMapping("/gps/userLocation/{id}")
    public VisitedLocation getUserLocation(@PathVariable UUID id);
}
