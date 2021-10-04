package tourGuide.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gpsUtil.location.Attraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
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
        logger.info("* HTTP GET request receive at root");

        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getAllUsers")
    public String getAll(){
        logger.info("* HTTP GET request receive at /getAllUsers");

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
        logger.info("* HTTP GET request receive at \"/getLocation?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);

    	try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(visitedLocation);

        } catch (JsonProcessingException e) {
    	    logger.error("ERROR: Object visitedLocation could not be serialized to JSON.");
    	    return null;
        }
    }

    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
        logger.info("HTTP GET request receive at \"/getNearbyAttractions?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(attractions);

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
    public String getAllCurrentLocations() {
        logger.info("HTTP GET request receive at \"/getAllCurrentLocations\"");
    	// TODO: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }
    	
    	return JsonStream.serialize("");
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

}