package tourGuide.controller;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourguide.commons.model.Location;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import com.tourguide.commons.model.VisitedLocation;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.model.NearbyAttraction;
import tourGuide.model.UserPreferences;
import tourGuide.model.UserReward;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import com.tourguide.commons.model.Provider;

import javax.validation.Valid;

/**
 * The type Tour guide controller.
 */
@RestController
@Validated
@Api(description = "Tour Guide api")
public class TourGuideController {

    private final Logger logger = LoggerFactory.getLogger(TourGuideController.class);

	@Autowired
	private TourGuideService tourGuideService;
    @Autowired
    private RewardsService rewardsService;

    /**
     * Index string.
     *
     * @return the string
     */
    @ApiOperation(value = "This URI returns \"Greetings from TourGuide!\".")
    @GetMapping(value={"","/"})
    public String index() {

        logger.info("HTTP GET request receive at index");

        return "Greetings from TourGuide!";
    }

    /**
     * Gets user as JSON.
     *
     * @param userName the user's name
     * @return the user
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "This URI returns a User requiring userName.")
    @GetMapping(value = "/getUser",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUser(
            @ApiParam(
                    value = "userName",
                    example = "internalUser1")
            @RequestParam String userName) throws UserNotFoundException {

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

    /**
     * Gets user preferences as JSON.
     *
     * @param userName the user name
     * @return the user preferences
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "This URI returns a user preferences requiring userName.")
    @GetMapping(value = "/getPreferences",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserPreferences(
            @ApiParam(
                    value = "userName",
                    example = "internalUser1")
            @RequestParam String userName) throws UserNotFoundException {

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

    /**
     * Gets all users as JSON.
     *
     * @return the all
     * @throws UsersGatheringException the users gathering exception
     */
    @ApiOperation(value = "This URI returns a list of all Users.")
    @GetMapping(value = "/getAllUsers",produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * Return the last visited VisitedLocation for a specified userName as JSON.
     *
     * @param userName the user's name
     * @return the location
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Return the last visited VisitedLocation for a specified user name.")
    @GetMapping(value = "/getLocation",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getLocation(
            @ApiParam(
                    value = "userName",
                    example = "internalUser1")
            @RequestParam String userName) throws UserNotFoundException {

        logger.info("HTTP GET request receive at \"/getLocation?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);

    	try {
            ObjectMapper objectMapper = new ObjectMapper();
            //fixme:?? return timeVisited as Integer number instead of readable date format
            return objectMapper.writeValueAsString(visitedLocation);

        } catch (JsonProcessingException e) {
    	    logger.error("ERROR: Object visitedLocation could not be serialized to JSON.");
    	    return null;
        }
    }

    /**
     * Gets nearby attractions as JSON.
     * Return the closest five tourist attractions to the user
     * - no matter how far the user is -
     *
     * @param userName the user's name
     * @return the nearby attractions
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Return the closest five tourist attractions to the user - no matter how far the user is -")
    @GetMapping(value = "/getNearbyAttractions",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNearbyAttractions(
            @ApiParam(
                    value = "userName",
                    example = "internalUser1")
            @RequestParam String userName) throws UserNotFoundException {

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

    /**
     * Returns the rewards for a specific user as JSON.
     *
     * @param userName the user's name
     * @return the rewards
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Return all the Rewards for a specified user name.")
    @GetMapping(value = "/getRewards",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRewards(
            @ApiParam(
                    value = "userName",
                    example = "internalUser1")
            @RequestParam String userName) throws UserNotFoundException {
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

    /**
     * Gets all current locations.
     * Current location are the most recent ones.
     * Return object is a JSON mapping of userId to Locations similar to:<br>
     * <p>
     * {
     * "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
     *  ...
     * }
     * </p>
     *
     * @return the all current locations
     * @throws UsersGatheringException the users gathering exception
     */
    @ApiOperation(value = "Get the list of every user's most recent location as JSON.")
    @GetMapping(value = "/getAllCurrentLocations",produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * Calculate and return a list of providers for attractions depending on user total reward points and preferences.
     *
     * @param userName the user's name
     * @return the trip deals
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Calculate and return a list of providers for attractions depending on user total reward points and preferences.")
    @GetMapping(value = "/getTripDeals",produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTripDeals(
            @ApiParam(
                    value = "userName",
                    example = "internalUser1")
            @RequestParam String userName) throws UserNotFoundException {
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

    /**
     * Update user's preferences.
     *
     * @param userName       the user's name
     * @param newPreferences the new preferences
     * @return the response entity
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "This URI allows to update user preferences.")
    @PutMapping("/updatePreferences")
    public ResponseEntity<String> updatePreferences(
            @ApiParam(
                    value = "userName",
                    example = "internalUser1")
            @RequestParam String userName,
            @Valid @RequestBody UserPreferences newPreferences) throws UserNotFoundException {

        logger.info("HTTP PUT request receive at \"/updatePreferences?userName="+userName+"\"");

        User user = tourGuideService.getUser(userName);

        tourGuideService.updatePreferences(user,newPreferences);

        return new ResponseEntity<>("Preferences updated", HttpStatus.OK);
    }

    /**
     * Force new tracking of all users
     *
     * @throws UsersGatheringException the user gathering exception
     */
    @ApiOperation(value = "Force new tracking of all users")
    @GetMapping(value = "/trackAll",produces = MediaType.APPLICATION_JSON_VALUE)
    public void trackAllUsers() throws UsersGatheringException {
        logger.info("HTTP GET request received at /trackAll");

        tourGuideService.trackUserLocationMultiThread(tourGuideService.getAllUsers());

    }

}