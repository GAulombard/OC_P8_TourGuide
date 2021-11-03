package com.api.rewardcentral.controller;

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

@RestController
public class RewardCentralController {

    private Logger logger = LoggerFactory.getLogger(RewardCentralController.class);

    @Autowired
    private RewardCentral rewardCentral;

    @RequestMapping(value = "/getAttractionRewardPoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    private int getAttractionRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId) {
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
