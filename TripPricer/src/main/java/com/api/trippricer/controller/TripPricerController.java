package com.api.trippricer.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;

/**
 * The type Trip pricer controller.
 */
@RestController
@Api(description = "TripePricer api")
public class TripPricerController {

    private final Logger logger = LoggerFactory.getLogger(TripPricerController.class);

    @Autowired
    private TripPricer tripPricer;

    @ApiOperation(value = "This URI returns the list of price proposals by different operators.")
    @RequestMapping(value = "/getPrice", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    private List<Provider> getPrice(
            @ApiParam(
            value = "Key to access the api.",
            example = "test-server-api-key")
            @RequestParam String tripPricerApiKey,
            @ApiParam(
                    value = "User ID in UUID format.",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam UUID userId,
            @ApiParam(
                    value = "Number of adults.",
                    example = "2")
            @RequestParam @PositiveOrZero int numberOfAdults,
            @ApiParam(
                    value = "Number of children.",
                    example = "3")
            @RequestParam @PositiveOrZero int numberChildren,
            @ApiParam(
                    value = "Number of night stay.",
                    example = "10")
            @RequestParam @PositiveOrZero int tripDuration,
            @ApiParam(
                    value = "Number of rewards points.",
                    example = "489")
            @RequestParam @PositiveOrZero int cumulatativeRewardPoints) {

        logger.info("HTTP GET request receive at /getPrice to TripPricer microservice");

        List<Provider> providers = null;

        try {
            logger.info("Getting list of price for providers using :");
            logger.info("API key : {}", tripPricerApiKey);
            logger.info("User : {}", userId);
            logger.info("Number of Adults : {}", numberOfAdults);
            logger.info("Number of children : {}", numberChildren);
            logger.info("Trip duration : {}", tripDuration);
            logger.info("And cumulated point : {}", cumulatativeRewardPoints);
            providers = tripPricer.getPrice(tripPricerApiKey, userId, numberOfAdults, numberChildren, tripDuration,
                    cumulatativeRewardPoints);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return providers;
    }
}
