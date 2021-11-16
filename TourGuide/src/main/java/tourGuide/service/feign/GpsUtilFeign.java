package tourGuide.service.feign;

import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.VisitedLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * The interface Gps util feign.
 */
@FeignClient(name = "gpsutil",url = "${proxy.gpsutil}")
public interface GpsUtilFeign {

    /**
     * Gets user location.
     *
     * @param uuid the uuid
     * @return the user location
     */
    @GetMapping(value = "/getUserLocation", produces = MediaType.APPLICATION_JSON_VALUE)
    VisitedLocation getUserLocation(@RequestParam("userId") UUID uuid);

    /**
     * Gets attractions.
     *
     * @return the attractions
     */
    @GetMapping(value = "/getAttractions", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Attraction> getAttractions();

}
