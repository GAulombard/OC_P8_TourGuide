package com.api.rewardcentral.controller;

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
import rewardCentral.RewardCentral;

import java.util.UUID;

/**
 * The type Reward central controller.
 */
@RestController
@Api(description = "RewardCentral api")
public class RewardCentralController {

    private final Logger logger = LoggerFactory.getLogger(RewardCentralController.class);

    @Autowired
    private RewardCentral rewardCentral;

    @ApiOperation(value = "This URI returns the number of rewards points for a required user id and attraction id.")
    @RequestMapping(value = "/getAttractionRewardPoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    private int getAttractionRewardPoints(
            @ApiParam(
                    value = "Attraction ID in UUID format.",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam UUID attractionId,
            @ApiParam(
                    value = "User ID in UUID format.",
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam UUID userId) {

        logger.info("HTTP GET request receive at /getAttractionRewardPoints to RewardCentral microservice");

        int rewardPoints = 0;

        try {
            rewardPoints = rewardCentral.getAttractionRewardPoints(attractionId, userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return rewardPoints;
    }
}
