package com.bintou.gps.controller;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class GpsUtilControllerTest {

    @Autowired
    private GpsUtilController gpsUtilController;


    @Test
    public void getAllAttractionsTest() {
        List<Attraction> attractionList = gpsUtilController.getAttractions();
        Assertions.assertNotNull(attractionList);
        Assertions.assertEquals(attractionList.size(), 26);
    }

    @Test
    public void getUserLocationTest() {
        UUID uuid = UUID.randomUUID();
        VisitedLocation visitedLocation = gpsUtilController.getUserLocation(uuid);
        System.out.println(uuid); // "e572901c-3c11-4f3c-85b1-c8144c986e45"
        Assertions.assertNotNull(visitedLocation);
    }
}
