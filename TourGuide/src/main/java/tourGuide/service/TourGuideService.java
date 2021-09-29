package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.jsoniter.output.JsonStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.model.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {

	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);

	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;

	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}

	//FIXME: maybe put this in RewardsService
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) {
		logger.info("********** Processing **********");
		logger.info("** Processing to get user location. User: "+user.getUserName());

		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);

		logger.info("** User location found at: "+ JsonStream.serialize(visitedLocation.location));
		return visitedLocation;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		logger.info("********** Processing **********");
		logger.info("** Processing to get trip deals. User: "+user.getUserName());

		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		logger.info("cumulativePoint: "+cumulatativeRewardPoints);
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		logger.info("********** Processing **********");
		logger.info("** Processing to track user location. User: "+user.getUserName());

		Locale.setDefault(new Locale("en", "US"));
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		//logger.info("Latitude: "+visitedLocation.location.latitude+" Longitude: "+visitedLocation.location.longitude);
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);

		//logger.info("** User location found: "+user.getVisitedLocations().size());
		return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		logger.info("********** Processing **********");
		logger.info("** Processing to get nearby attractions. User: "+visitedLocation.userId);

		List<Attraction> nearbyAttractions = new ArrayList<>();

		gpsUtil.getAttractions().forEach(attraction -> {
			logger.debug("Attraction: "+attraction.attractionName+" Distance: "+rewardsService.getDistance(attraction,visitedLocation.location));
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				logger.info("Attraction nearby found");
				nearbyAttractions.add(attraction);
			}
		});
/*		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				logger.info("Attraction nearby found");
				nearbyAttractions.add(attraction);
			}
		}*/

		logger.info("** Attraction nearby: "+ JsonStream.serialize(nearbyAttractions.toString()));
		return nearbyAttractions;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
