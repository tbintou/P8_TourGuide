package com.bintou.trip.pricer.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class TripPricerControllerTest {

    @Autowired
    private TripPricerController tripPricerController;

    @Test
    public void getPriceTest() {
        UUID uuid = UUID.randomUUID();
        System.out.println("uuid = " + uuid);

        List<Provider> providerList = tripPricerController.getPrice("test", uuid, 1, 0, 3, 1);
        Assertions.assertNotNull(providerList);
        Assertions.assertEquals(providerList.size(), 5);
    }
}
