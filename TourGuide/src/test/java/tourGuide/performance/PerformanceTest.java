package tourGuide.performance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;


import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.VisitedLocation;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tourGuide.service.feign.GpsUtilFeign;

/**
 * The type Performance test.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PerformanceTest {

	private Logger logger = LoggerFactory.getLogger(PerformanceTest.class);
	
	/*
	 * A note on performance improvements:
	 *     
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *     
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *     
	 *     
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent. 
	 * 
	 *     These are performance metrics that we are trying to hit:
	 *     
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */

	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private RewardsService rewardsService;
	@Autowired
	private TourGuideService tourGuideService;
	private static Locale locale = new Locale("en", "US");

    /**
     * Sets up.
     */
    @BeforeAll
	public static void setUp() {
		Locale.setDefault(locale);
		InternalTestHelper.setInternalUserNumber(100000);
	}

    /**
     * High volume track location.
     *
     * @throws UsersGatheringException the users gathering exception
     */

	@Test
	public void highVolumeTrackLocation() throws UsersGatheringException {//195s for 100 000 users

		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		tourGuideService.trackUserLocationMultiThread(allUsers);
		stopWatch.stop();


		logger.info("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

	}

    /**
     * High volume get rewards.
     *
     * @throws UsersGatheringException the users gathering exception
     */

	@Test
	public void highVolumeGetRewards() throws UsersGatheringException {//253 seconds for 100 000 users

		TourGuideService tourGuideService = new TourGuideService(rewardsService,true,false);

		StopWatch stopWatch = new StopWatch();

	    Attraction attraction = gpsUtilFeign.getAttractions().get(0);
		List<User> allUsers;
		allUsers = tourGuideService.getAllUsers();
		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		stopWatch.start();
		rewardsService.calculateRewardsMultiThread(allUsers);
		stopWatch.stop();

		for(User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));

	}
	
}

