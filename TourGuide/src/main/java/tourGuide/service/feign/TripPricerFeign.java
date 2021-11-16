package tourGuide.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.tourguide.commons.model.Provider;

import java.util.List;
import java.util.UUID;

/**
 * The interface Trip pricer feign.
 */
@FeignClient(name = "trippricer", url = "${proxy.trippricer}")
public interface TripPricerFeign {

    /**
     * Gets price.
     *
     * @param tripPricerApiKey         the trip pricer api key
     * @param userId                   the user id
     * @param numberOfAdults           the number of adults
     * @param numberChildren           the number children
     * @param tripDuration             the trip duration
     * @param cumulatativeRewardPoints the cumulatative reward points
     * @return the price
     */
    @GetMapping(value = "/getPrice", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Provider> getPrice(@RequestParam("tripPricerApiKey") String tripPricerApiKey, @RequestParam("userId") UUID userId,
                            @RequestParam("numberOfAdults") int numberOfAdults, @RequestParam("numberChildren") int numberChildren, @RequestParam("tripDuration") int tripDuration,
                            @RequestParam("cumulatativeRewardPoints") int cumulatativeRewardPoints);

}
