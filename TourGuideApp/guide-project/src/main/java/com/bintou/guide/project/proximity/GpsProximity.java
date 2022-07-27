package com.bintou.guide.project.proximity;

import com.bintou.guide.project.model.location.Attraction;

import com.bintou.guide.project.model.location.VisitedLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Component
@FeignClient(value = "gps", url = "${gps.serviceUrl}")
public interface GpsProximity {

    @GetMapping("/gps/attractions")
    public List<Attraction> getAttractions();

    @GetMapping("/gps/userLocation/{id}")
    public VisitedLocation getUserLocation(@PathVariable UUID id);
}
