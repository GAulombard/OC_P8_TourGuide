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

@FeignClient(name = "gpsutil",url = "${proxy.gpsutil}")
public interface GpsUtilFeign {

    @GetMapping(value = "/getUserLocation", produces = MediaType.APPLICATION_JSON_VALUE)
    VisitedLocation getUserLocation(@RequestParam("userId") UUID uuid);

    @GetMapping(value = "/getAttractions", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Attraction> getAttractions();

}
