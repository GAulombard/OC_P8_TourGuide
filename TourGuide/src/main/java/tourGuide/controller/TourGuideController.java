package tourGuide.controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import javax.validation.Valid;

@RestController
@Validated
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
        logger.info("HTTP GET request receive at /getUser?userName="+userName+"\"");

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
            return objectMapper.writeValueAsString(visitedLocation); //fixme:?? return timeVisited as Integer number instead of readable date format

        } catch (JsonProcessingException e) {
    	    logger.error("ERROR: Object visitedLocation could not be serialized to JSON.");
    	    return null;
        }
    }

    @RequestMapping("/trackUsers")
    public String trackAllUsersLocation() throws UsersGatheringException {
        logger.info("HTTP GET request receive at /trackUsers");

        List<User> users = tourGuideService.getAllUsers();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(tourGuideService.trackAllUsersLocation(users));

        } catch (JsonProcessingException e) {
            logger.error("ERROR: all user's location could not be serialized to JSON.");
            return null;
        }
    }

    @RequestMapping("/getNearbyAttractions") //Get the closest 5 tourist attractions to the user - no matter how far away they are.
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
    
    @RequestMapping("/getRewards")
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

    @PutMapping("/updatePreferences")
    public ResponseEntity<String> updatePreferences(@RequestParam String userName, @Valid @RequestBody UserPreferences newPreferences) throws UserNotFoundException {
        logger.info("HTTP PUT request receive at \"/updatePreferences?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);

        tourGuideService.updatePreferences(user,newPreferences);

        return new ResponseEntity<>("Preferences updated", HttpStatus.OK);
    }

}