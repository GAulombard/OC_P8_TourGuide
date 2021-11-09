package tourGuide.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Service
@FeignClient(value = "RewardCentralApi",url = "http://127.0.0.1:8082")
public interface RewardCentralFeign {

    @GetMapping(value = "/getAttractionRewardPoints", produces = MediaType.APPLICATION_JSON_VALUE)
    int getAttractionRewardPoints(@RequestParam("attractionId") UUID attractionId, @RequestParam("userId") UUID userId);

}
