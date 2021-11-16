package tourGuide.util;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.UUID;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import com.tourguide.commons.model.Attraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;

import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.feign.GpsUtilFeign;

/**
 * The type Distance calculator test.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class DistanceCalculatorTest {

    private final static Logger logger = LoggerFactory.getLogger(DistanceCalculatorTest.class);

    @Autowired
    private GpsUtilFeign gpsUtilFeign;
    @Autowired
    private TourGuideService tourGuideService;
    @Autowired
    private RewardsService rewardsService;

    private User user;
    private User user2;

    /**
     * Setup.
     */
    @BeforeAll
    public static void setup(){

        logger.debug("@BeforeAll");
        InternalTestHelper.setInternalUserNumber(0); //set the list of user to 0
        Locale.setDefault(new Locale("en", "US")); //Set default locale to avoid problems with comma between "," and "."

    }

    /**
     * Init.
     *
     * @throws UserAlreadyExistsException the user already exists exception
     * @throws UserNotFoundException      the user not found exception
     */
    @BeforeEach
    void init() throws UserAlreadyExistsException, UserNotFoundException {
        logger.debug("@BeforeEach");
        DistanceCalculator.setProximityBuffer(10);
        tourGuideService.tracker.stopTracking();

        user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        tourGuideService.addUser(user);
        user2 = new User(UUID.randomUUID(), "jon2", "000", "jon@tourGuide.com");

    }

    /**
     * Tear down.
     */
    @AfterEach
    void tearDown(){
        logger.info("@AfterEach");
        InternalTestHelper.freeInternalUserMap();
    }


    /**
     * Is within attraction proximity should return true.
     */
    @Test
    public void isWithinAttractionProximity_shouldReturnTrue() {

        Attraction attraction = gpsUtilFeign.getAttractions().get(0);
        assertTrue(DistanceCalculator.isWithinAttractionProximity(attraction, attraction));
    }

    /**
     * Is within attraction proximity should return false.
     */
    @Test
    public void isWithinAttractionProximity_shouldReturnFalse() {

        Attraction attraction = gpsUtilFeign.getAttractions().get(0);
        Attraction attraction2 = gpsUtilFeign.getAttractions().get(1);

        DistanceCalculator.setAttractionProximityRange(1);

        assertFalse(DistanceCalculator.isWithinAttractionProximity(attraction, attraction2));
    }

    /**
     * Set default proximity buffer.
     */
    @Test
    public void setDefaultProximityBuffer(){
        DistanceCalculator distanceCalculator = new DistanceCalculator();

        distanceCalculator.setProximityBuffer(200);

        assertEquals(200,distanceCalculator.getProximityBuffer());

        distanceCalculator.setDefaultProximityBuffer();

        assertEquals(10, distanceCalculator.getProximityBuffer());


    }


}
