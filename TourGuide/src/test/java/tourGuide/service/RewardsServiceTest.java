package tourGuide.service;

import static org.junit.Assert.*;

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
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tourGuide.util.DistanceCalculator;

@SpringBootTest
public class RewardsServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(RewardsServiceTest.class);
	private GpsUtil gpsUtil = new GpsUtil();
	private RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
	private TourGuideService tourGuideService;
	private User user;

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

	}

	@AfterEach
	void tearDown(){
		logger.info("@AfterEach");
		InternalTestHelper.freeInternalUserMap();
	}

	@Test
	public void userGetRewards() throws UserNotFoundException, UserAlreadyExistsException {

		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();

		assertTrue(userRewards.size() == 1);

	}
	
	@Test
	public void isWithinAttractionProximity() {

		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(DistanceCalculator.isWithinAttractionProximity(attraction, attraction));
	}
	

	@Test
	public void nearAllAttractions() throws UserNotFoundException, UsersGatheringException {

		DistanceCalculator.setProximityBuffer(Integer.MAX_VALUE);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = rewardsService.getUserRewards(tourGuideService.getAllUsers().get(0));

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());

	}
	
}
