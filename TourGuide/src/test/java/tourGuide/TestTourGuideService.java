package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;

import gpsUtil.location.VisitedLocation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import rewardCentral.RewardCentral;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.NearbyAttraction;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tripPricer.Provider;

@SpringBootTest
public class TestTourGuideService {

	@Test
	public void getUserLocation() throws UserNotFoundException, UserAlreadyExistsException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		assertTrue(visitedLocation.userId.equals(user.getUserId()));

		InternalTestHelper.freeInternalUserMap();
	}
	
	@Test
	public void addUser() throws UserNotFoundException, UserAlreadyExistsException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		User retrievedUser = tourGuideService.getUser(user.getUserName());
		User retrievedUser2 = tourGuideService.getUser(user2.getUserName());


		
		assertEquals(user, retrievedUser);
		assertEquals(user2, retrievedUser2);

		InternalTestHelper.freeInternalUserMap();
	}
	
	@Test
	public void getAllUsers() throws UserAlreadyExistsException, UsersGatheringException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);
		
		List<User> allUsers = tourGuideService.getAllUsers();


		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));

		InternalTestHelper.freeInternalUserMap();
	}
	
	@Test
	public void trackUser() throws UserNotFoundException, UserAlreadyExistsException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		

		
		assertEquals(user.getUserId(), visitedLocation.userId);

		InternalTestHelper.freeInternalUserMap();
	}
	
	//@Ignore
	@Test
	public void getNearbyAttractions() throws UserNotFoundException, UserAlreadyExistsException {
		Locale.setDefault(new Locale("en", "US"));
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.tracker.stopTracking();
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		
		List<NearbyAttraction> nearbyAttractions = tourGuideService.getNearByAttractions(visitedLocation,user);

		
		assertEquals(5, nearbyAttractions.size());

		InternalTestHelper.freeInternalUserMap();
	}

	//@Ignore
	@Test
	public void getTripDeals() throws UserNotFoundException, UserAlreadyExistsException {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		tourGuideService.tracker.stopTracking();

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.addUser(user);
		List<Provider> providers = tourGuideService.getTripDeals(user);

		assertEquals(130, providers.size());

		InternalTestHelper.freeInternalUserMap();
	}
	
	
}
