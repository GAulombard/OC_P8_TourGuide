package tourGuide.service;


import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.NearbyAttraction;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tripPricer.Provider;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class TourGuideServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(TourGuideServiceTest.class);
    private GpsUtil gpsUtil = new GpsUtil();
    private RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    private TourGuideService tourGuideService;
    private User user;
    private User user2;
    private User user3;

    @BeforeAll
    public static void setup(){

        logger.debug("@BeforeAll");
        InternalTestHelper.setInternalUserNumber(0); //set the list of user to 0
        Locale.setDefault(new Locale("en", "US")); //Set default locale to avoid problems with comma between "," and "."
    }

    @BeforeEach
    void init() throws UserAlreadyExistsException, UserNotFoundException {
        logger.debug("@BeforeEach");
        tourGuideService = new TourGuideService(gpsUtil,rewardsService);
        tourGuideService.tracker.stopTracking();

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
        user2 = new User(UUID.randomUUID(), "jon2", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user2);
        user3 = new User(UUID.randomUUID(), "jon3", "000", "jon@tourGuide.com");

    }

    @AfterEach
    void tearDown(){
        logger.debug("@AfterEach");
        InternalTestHelper.freeInternalUserMap();
    }

    @Test
    public void getUser() throws UserNotFoundException {

        assertEquals("jon", tourGuideService.getUser(user.getUserName()).getUserName());
        assertEquals("000", tourGuideService.getUser(user.getUserName()).getPhoneNumber());
        assertEquals("jon@tourGuide.com", tourGuideService.getUser(user.getUserName()).getEmailAddress());

    }

    @Test
    public void getUser_shouldThrowsUserNotFoundException() {

        assertThrows(UserNotFoundException.class, () -> tourGuideService.getUser("test"));

    }

    @Test
    public void getAllUsers() throws UsersGatheringException {

        assertEquals(2,tourGuideService.getAllUsers().size());
        assertTrue(tourGuideService.getAllUsers().contains(user));
        assertTrue(tourGuideService.getAllUsers().contains(user2));

    }

    @Test
    public void getUserLocation() throws UserNotFoundException {

        double latMax = 85.05112878;
        double latMin = -85.05112878;
        double longMax = 180;
        double longMin = -180;

        assertThat(tourGuideService.getUserLocation(user).location.latitude,allOf(greaterThan(latMin),lessThan(latMax)));
        assertThat(tourGuideService.getUserLocation(user).location.longitude,allOf(greaterThan(longMin),lessThan(longMax)));
    }

    @Test
    public void getUserLocation_shouldThrowsUserNotFoundException() {

        assertThrows(UserNotFoundException.class, () -> tourGuideService.getUserLocation(user3));

    }

    @Test
    public void addUser() throws UserNotFoundException, UserAlreadyExistsException, UsersGatheringException {
        User user4 = new User(UUID.randomUUID(), "jon4", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user4);

        User retrievedUser = tourGuideService.getUser(user4.getUserName());

        assertEquals(user4, retrievedUser);
        assertEquals(3,tourGuideService.getAllUsers().size());
        assertEquals(1,user4.getVisitedLocations().size());
    }

    @Test
    public void addUser_shouldThrowsUserAlreadyExistsException() {
        User user4 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        assertThrows(UserAlreadyExistsException.class, () -> tourGuideService.addUser(user4));
    }

    @Test
    public void getTripDeals() throws UserNotFoundException {

        List<Provider> providers = tourGuideService.getTripDeals(user);

        assertEquals(130, providers.size());
    }

    @Test
    public void getTripDeals_shouldThrowsUserNotfoundException() {

        assertThrows(UserNotFoundException.class, () -> tourGuideService.getTripDeals(user3));

    }

    @Test
    public void getAttractionsWithinRangePreferences_shouldThrowsUserNotFoundException(){

        assertThrows(UserNotFoundException.class, () -> tourGuideService.getAttractionsWithinRangePreferences(gpsUtil.getAttractions(),user3));

    }

    @Test
    public void getAttractionsWithinRangePreferences() throws UserNotFoundException {

        UserPreferences userPreferences = user.getUserPreferences();
        userPreferences.setAttractionProximity(Integer.MAX_VALUE);

        assertEquals(26,tourGuideService.getAttractionsWithinRangePreferences(gpsUtil.getAttractions(),user).size());

    }

    @Test
    public void trackUserLocation_shouldThrowsUserNotFoundException(){

        assertThrows(UserNotFoundException.class, () -> tourGuideService.trackUserLocation(user3));

    }

    @Test
    public void trackUserLocation() throws UserNotFoundException {

        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        assertEquals(user.getUserId(), visitedLocation.userId);

    }

    @Test
    public void trackAllUsersLocation() throws UserNotFoundException {

        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        VisitedLocation visitedLocation2 = tourGuideService.trackUserLocation(user2);

        assertEquals(2, user.getVisitedLocations().size());
        assertEquals(2, user2.getVisitedLocations().size());

    }

    @Test
    public void getNearbyAttractions() throws UserNotFoundException {

        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        List<NearbyAttraction> nearbyAttractions = tourGuideService.getNearByAttractions(visitedLocation,user);

        assertEquals(5, nearbyAttractions.size());

    }

    @Test
    public void getNearbyAttractions_shouldThrowUserNotFoundException() {

        assertThrows(UserNotFoundException.class, () -> tourGuideService.getNearByAttractions(tourGuideService.trackUserLocation(user3),user3));

    }

    @Test
    public void getAllCurrentLocation() throws UserNotFoundException {

        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        VisitedLocation visitedLocation2 = tourGuideService.trackUserLocation(user2);

        assertEquals(visitedLocation, user.getLastVisitedLocation());
        assertEquals(visitedLocation2, user2.getLastVisitedLocation());

    }

    @Test
    public void updatePreferences_shouldThrowsUserNotFoundException() {

        UserPreferences updatePreferences = new UserPreferences();

        assertThrows(UserNotFoundException.class, () -> tourGuideService.updatePreferences(user3,updatePreferences));

    }

    @Test
    public void updatePreferences() throws UserNotFoundException {

        UserPreferences updatePreferences = new UserPreferences();

        updatePreferences.setTicketQuantity(5);
        updatePreferences.setNumberOfChildren(3);
        updatePreferences.setNumberOfAdults(2);

        tourGuideService.updatePreferences(user,updatePreferences);

        assertEquals(5,user.getUserPreferences().getTicketQuantity());
        assertEquals(3,user.getUserPreferences().getNumberOfChildren());
        assertEquals(2,user.getUserPreferences().getNumberOfAdults());

    }
}
