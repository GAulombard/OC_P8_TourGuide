package tourGuide.controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.model.NearbyAttraction;
import tourGuide.model.UserPreferences;
import tourGuide.model.UserReward;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    private Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	@Autowired
	private TourGuideService tourGuideService;
    @Autowired
    private RewardsService rewardsService;
	
    @RequestMapping(value={"","/"})
    public String index() {
        logger.info("HTTP GET request receive at index");

        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getUser")
    public String getUser(@RequestParam String userName) throws UserNotFoundException {
        logger.info("HTTP GET request receive at /getUseruserName="+userName+"\"");

        User user = tourGuideService.getUser(userName);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user);

        } catch (JsonProcessingException e) {
            logger.error("ERROR: user could not be serialized to JSON.");
            return null;
        }
    }

    @RequestMapping("/getPreferences")
    public String getUserPreferences(@RequestParam String userName) throws UserNotFoundException {
        logger.info("HTTP GET request receive at /getPreferences?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
        logger.info("HighPricePoint: "+user.getUserPreferences().getHighPricePoint().getNumber());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(user.getUserPreferences());

        } catch (JsonProcessingException e) {
            logger.error("ERROR: user could not be serialized to JSON.");
            return null;
        }
    }

    @RequestMapping("/getAllUsers")
    public String getAll() throws UsersGatheringException {
        logger.info("HTTP GET request receive at /getAllUsers");

        List<User> users = tourGuideService.getAllUsers();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(users);

        } catch (JsonProcessingException e) {
            logger.error("ERROR: list of users could not be serialized to JSON.");
            return null;
        }
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) throws UserNotFoundException {
        logger.info("HTTP GET request receive at \"/getLocation?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);

    	try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(visitedLocation); //fixme: return timeVisited as Integer number instead of readable date format

        } catch (JsonProcessingException e) {
    	    logger.error("ERROR: Object visitedLocation could not be serialized to JSON.");
    	    return null;
        }
    }

    @RequestMapping("/getNearbyAttractions") //Get the closest five tourist attractions to the user - no matter how far away they are.
    public String getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
        logger.info("HTTP GET request receive at \"/getNearbyAttractions?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        List<NearbyAttraction> nearbyAttractions = tourGuideService.getNearByAttractions(visitedLocation,user);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(nearbyAttractions);

        } catch (JsonProcessingException e) {
            logger.error("ERROR: Object visitedLocation could not be serialized to JSON.");
            return null;
        }
    }
    
    @RequestMapping("/getRewards") //fixme: always return nothing... ???
    public String getRewards(@RequestParam String userName) throws UserNotFoundException {
        logger.info("HTTP GET request receive at \"/getRewards?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
        List<UserReward> userRewards = rewardsService.getUserRewards(user);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(userRewards);

        } catch (JsonProcessingException e) {
            logger.error("ERROR: List of user rewards could not be serialized to JSON.");
            return null;
        }
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() throws UsersGatheringException {
        logger.info("HTTP GET request receive at \"/getAllCurrentLocations\"");

        Map<String, Location> allCurrentLocations = tourGuideService.getAllCurrentLocation();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(allCurrentLocations);

        } catch (JsonProcessingException e) {
            logger.error("ERROR: List of user's current location could not be serialized to JSON.");
            return null;
        }
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) throws UserNotFoundException {
        logger.info("HTTP GET request receive at \"/getTripDeals?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
    	List<Provider> providers = tourGuideService.getTripDeals(user);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(providers);

        } catch (JsonProcessingException e) {
            logger.error("ERROR: List of providers could not be serialized to JSON.");
            return null;
        }
    }

    @PostMapping("/updatePreferences")
    public ResponseEntity<String> updatePreferences(@RequestParam String userName, @RequestBody UserPreferences newPreferences) throws UserNotFoundException {
        logger.info("HTTP POST request receive at \"/updatePreferences?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);

        tourGuideService.updatePreferences(user,newPreferences);

        return ResponseEntity.ok("Preferences updated");
    }

}