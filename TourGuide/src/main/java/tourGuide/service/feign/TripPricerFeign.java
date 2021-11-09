package tourGuide.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.tourguide.commons.model.Provider;

import java.util.List;
import java.util.UUID;

@Service
@FeignClient(value = "TripPricerApi", url = "http://127.0.0.1:8083")
public interface TripPricerFeign {

    @GetMapping(value = "/getPrice", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Provider> getPrice(@RequestParam("tripPricerApiKey") String tripPricerApiKey, @RequestParam("userId") UUID userId,
                            @RequestParam("numberOfAdults") int numberOfAdults, @RequestParam("numberChildren") int numberChildren, @RequestParam("tripDuration") int tripDuration,
                            @RequestParam("cumulatativeRewardPoints") int cumulatativeRewardPoints);

}
