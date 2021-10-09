package tourGuide.util;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.UUID;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;

import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class DistanceCalculatorTest {

    private final static Logger logger = LoggerFactory.getLogger(DistanceCalculatorTest.class);
    private GpsUtil gpsUtil = new GpsUtil();
    private RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
    private TourGuideService tourGuideService;
    private User user;
    private User user2;

    @BeforeAll
    public static void setup(){

        logger.debug("@BeforeAll");
        InternalTestHelper.setInternalUserNumber(0); //set the list of user to 0
        Locale.setDefault(new Locale("en", "US")); //Set default locale to avoid problems with comma between "," and "."

    }

    @BeforeEach
    void init() throws UserAlreadyExistsException, UserNotFoundException {
        logger.debug("@BeforeEach");
        DistanceCalculator.setProximityBuffer(10);
        tourGuideService = new TourGuideService(gpsUtil,rewardsService);
        tourGuideService.tracker.stopTracking();

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
        user2 = new User(UUID.randomUUID(), "jon2", "000", "jon@tourGuide.com");

    }

    @AfterEach
    void tearDown(){
        logger.info("@AfterEach");
        InternalTestHelper.freeInternalUserMap();
    }


    @Test
    public void isWithinAttractionProximity_shouldReturnTrue() {

        Attraction attraction = gpsUtil.getAttractions().get(0);
        assertTrue(DistanceCalculator.isWithinAttractionProximity(attraction, attraction));
    }

    @Test
    public void isWithinAttractionProximity_shouldReturnFalse() {

        Attraction attraction = gpsUtil.getAttractions().get(0);
        Attraction attraction2 = gpsUtil.getAttractions().get(1);

        DistanceCalculator.setAttractionProximityRange(1);

        assertFalse(DistanceCalculator.isWithinAttractionProximity(attraction, attraction2));
    }

    @Test
    public void setDefaultProximityBuffer(){
        DistanceCalculator distanceCalculator = new DistanceCalculator();

        distanceCalculator.setProximityBuffer(200);

        assertEquals(200,distanceCalculator.getProximityBuffer());

        distanceCalculator.setDefaultProximityBuffer();

        assertEquals(10, distanceCalculator.getProximityBuffer());


    }


}
