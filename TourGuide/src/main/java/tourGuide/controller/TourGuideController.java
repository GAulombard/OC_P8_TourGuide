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
    @RequestMapping(value={"","/"})
    public String index() {

        logger.info("HTTP GET request receive at index");

        return "Greetings from TourGuide!";
    }

    /**
     * Gets user.
     *
     * @param userName the user name
     * @return the user
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "This URI returns a User requiring userName.")
    @RequestMapping(value = "/getUser",produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Gets user preferences.
     *
     * @param userName the user name
     * @return the user preferences
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "This URI returns a user preferences requiring userName.")
    @RequestMapping(value = "/getPreferences",produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Gets all Users.
     *
     * @return the all
     * @throws UsersGatheringException the users gathering exception
     */
    @ApiOperation(value = "This URI returns a list of all Users.")
    @RequestMapping(value = "/getAllUsers",produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Return the last visited VisitedLocation for a specified userName.
     *
     * @param userName the user name
     * @return the location
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Return the last visited VisitedLocation for a specified user name.")
    @RequestMapping(value = "/getLocation",produces = MediaType.APPLICATION_JSON_VALUE)
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
            return objectMapper.writeValueAsString(visitedLocation); //fixme:?? return timeVisited as Integer number instead of readable date format

        } catch (JsonProcessingException e) {
    	    logger.error("ERROR: Object visitedLocation could not be serialized to JSON.");
    	    return null;
        }
    }

    /**
     * Gets nearby attractions.
     * Return the closest five tourist attractions to the user
     * - no matter how far the user is -
     *
     * @param userName the user name
     * @return the nearby attractions
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Return the closest five tourist attractions to the user - no matter how far the user is -")
    @RequestMapping(value = "/getNearbyAttractions",produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Rturns the rewards for a specific user.
     *
     * @param userName the user name
     * @return the rewards
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Return all the Rewards for a specified user name.")
    @RequestMapping(value = "/getRewards",produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Gets all current locations as a JSON.
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
    @RequestMapping(value = "/getAllCurrentLocations",produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Return all the Rewards for a specified user name.
     *
     * @param userName the user name
     * @return the trip deals
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "Return all the Rewards for a specified user name.")
    @RequestMapping(value = "/getTripDeals",produces = MediaType.APPLICATION_JSON_VALUE)
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
     * Update preferences response entity.
     *
     * @param userName       the user name
     * @param newPreferences the new preferences
     * @return the response entity
     * @throws UserNotFoundException the user not found exception
     */
    @ApiOperation(value = "This URI allows to update user preferencies.")
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

}