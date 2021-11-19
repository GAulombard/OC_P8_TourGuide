package com.api.gpsutil.controller;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * The type Gps util controller.
 */
@RestController
@Api(description = "GpsUtil api")
public class GpsUtilController {

    private Logger logger = LoggerFactory.getLogger(GpsUtilController.class);

    @Autowired
    private GpsUtil gpsUtil;

    /**
     * Gets user location.
     *
     * @param userId the user id
     * @return the user location
     */
    @ApiOperation(value = "This URI returns a VisitedLocation for a required user id(UUID format).")
    @RequestMapping(value = "/getUserLocation",produces = MediaType.APPLICATION_JSON_VALUE,method = RequestMethod.GET)
    public VisitedLocation getUserLocation(
            @ApiParam(
                    value = "User ID in UUID format.",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam UUID userId) {

        logger.info("HTTP GET request receive at /getUserLocation to gpsUtil microservice");
        VisitedLocation visitedLocation = null;
        try {
            visitedLocation = gpsUtil.getUserLocation(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return visitedLocation;
    }

    /**
     * Gets attractions list.
     *
     * @return the attractions
     */
    @ApiOperation(value = "This URI returns the list of all attractions of the gpsUtil.")
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
