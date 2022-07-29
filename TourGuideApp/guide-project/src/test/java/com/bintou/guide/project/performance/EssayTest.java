package com.bintou.guide.project.performance;

import com.bintou.guide.project.model.location.Attraction;
import com.bintou.guide.project.proximity.GpsProximity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class EssayTest {

    @Autowired
    private GpsProximity gpsProximity;

    @Test
    public void getAllAttractionsTest() {
        List<Attraction> attractionList = gpsProximity.getAttractions();
        System.out.println(attractionList);
        Assertions.assertEquals(attractionList.size(), 26);
    }
}
