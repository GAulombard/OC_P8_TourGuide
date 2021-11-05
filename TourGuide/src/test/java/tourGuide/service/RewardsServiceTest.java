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
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
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
	public void getUserRewards() throws UserNotFoundException {

		Attraction attraction = gpsUtilFeign.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();

		assertTrue(userRewards.size() == 1);

	}

	@Test
	public void getUserRewards_shouldThrowsUserNotFoundException() {

		assertThrows(UserNotFoundException.class,() -> rewardsService.getUserRewards(user2));

	}
	
	@Test
	public void isWithinAttractionProximity() {

		Attraction attraction = gpsUtilFeign.getAttractions().get(0);
		assertTrue(DistanceCalculator.isWithinAttractionProximity(attraction, attraction));
	}
	

	@Test
	public void nearAllAttractions() throws UserNotFoundException, UsersGatheringException {

		DistanceCalculator.setProximityBuffer(Integer.MAX_VALUE);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = rewardsService.getUserRewards(tourGuideService.getAllUsers().get(0));

		assertEquals(gpsUtilFeign.getAttractions().size(), userRewards.size());

	}
	
}
