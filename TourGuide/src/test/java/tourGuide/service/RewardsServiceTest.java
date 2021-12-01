package tourGuide.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.service.feign.GpsUtilFeign;
import tourGuide.util.DistanceCalculator;

/**
 * The type Rewards service test.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class RewardsServiceTest {

	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private TourGuideService tourGuideService;
	@Autowired
	private RewardsService rewardsService;

	private final static Logger logger = LoggerFactory.getLogger(RewardsServiceTest.class);

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
     * Gets user rewards.
     *
     * @throws UserNotFoundException the user not found exception
     */
    @Test
	public void getUserRewards() throws UserNotFoundException {

		Attraction attraction = gpsUtilFeign.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = user.getUserRewards();

		assertTrue(userRewards.size() == 1);

	}

    /**
     * Gets user rewards should throws user not found exception.
     */
    @Test
	public void getUserRewards_shouldThrowsUserNotFoundException() {

		assertThrows(UserNotFoundException.class,() -> rewardsService.getUserRewards(user2));

	}

    /**
     * Is within attraction proximity.
     */
    @Test
	public void isWithinAttractionProximity() {

		Attraction attraction = gpsUtilFeign.getAttractions().get(0);
		assertTrue(DistanceCalculator.isWithinAttractionProximity(attraction, attraction));
	}


    /**
     * Near all attractions.
     *
     * @throws UserNotFoundException   the user not found exception
     * @throws UsersGatheringException the users gathering exception
     */
    @Test
	public void nearAllAttractions() throws UserNotFoundException, UsersGatheringException {

		DistanceCalculator.setProximityBuffer(Integer.MAX_VALUE);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = rewardsService.getUserRewards(tourGuideService.getAllUsers().get(0));

		assertEquals(gpsUtilFeign.getAttractions().size(), userRewards.size());

	}
	
}
