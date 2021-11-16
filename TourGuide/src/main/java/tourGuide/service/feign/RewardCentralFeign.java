package tourGuide.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * The interface Reward central feign.
 */
@FeignClient(name = "rewardcentral",url = "${proxy.rewardcentral}")
public interface RewardCentralFeign {

    /**
     * Gets attraction reward points.
     *
     * @param attractionId the attraction id
     * @param userId       the user id
     * @return the attraction reward points
     */
    @GetMapping(value = "/getAttractionRewardPoints", produces = MediaType.APPLICATION_JSON_VALUE)
    int getAttractionRewardPoints(@RequestParam("attractionId") UUID attractionId, @RequestParam("userId") UUID userId);

}
