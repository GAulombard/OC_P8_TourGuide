package tourGuide.service.feign;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Service
@FeignClient(value = "gpsUtil",url = "http://localhost:8081")
public interface GpsUtilFeign {

    @GetMapping(value = "/getUserLocation", produces = MediaType.APPLICATION_JSON_VALUE)
    VisitedLocation getUserLocation(@RequestParam("userId") UUID uuid);

    @GetMapping(value = "/getAttractions", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Attraction> getAttractions();

}
