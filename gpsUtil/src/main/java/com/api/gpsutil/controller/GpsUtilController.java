package com.api.gpsutil.controller;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class GpsUtilController {

    private Logger logger = LoggerFactory.getLogger(GpsUtilController.class);

    @Autowired
    private GpsUtil gpsUtil;

    @RequestMapping(value = "/getUserLocation",produces = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    public VisitedLocation getUserLocation(@RequestParam UUID userId) {
        logger.info("HTTP GET request receive at /getUserLocation to gpsUtil microservice");
        VisitedLocation visitedLocation = null;
        try {
            visitedLocation = gpsUtil.getUserLocation(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return visitedLocation;
    }

    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        logger.info("HTTP GET request receive at /getAttractions to gpsUtil microservice");
        List<Attraction> attractions = null;
        try {
            attractions = gpsUtil.getAttractions();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return attractions;
    }


}
