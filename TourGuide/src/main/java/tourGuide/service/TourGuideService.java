package tourGuide.service;

import java.util.*;
import java.util.stream.Collectors;

import com.jsoniter.output.JsonStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.util.DistanceCalculator;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {

	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);

	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	private final InternalTestHelper internalTestHelper = new InternalTestHelper();
	boolean testMode = true;

	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) { //fixme: put it into internal test helper and call it when launch the app
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalTestHelper.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public VisitedLocation getUserLocation(User user) throws UserNotFoundException {
		logger.info("** Processing to get user location. User: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation() : trackUserLocation(user);

		return visitedLocation;
	}
	
	public User getUser(String userName) throws UserNotFoundException {
		logger.info("** Processing to get user by username");

		if(!internalTestHelper.getInternalUserMap().containsKey(userName)) throw new UserNotFoundException("User not found");

		return internalTestHelper.getInternalUserMap().get(userName);
	}
	
	public List<User> getAllUsers() {
		logger.info("** Processing to get all users");
		return internalTestHelper.getInternalUserMap().values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) throws UserAlreadyExistsException {
		logger.info("** Processing to add new user: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) {
			internalTestHelper.getInternalUserMap().put(user.getUserName(), user);
		} else {
			throw new UserAlreadyExistsException("ERROR: User already exists");
		}
	}
	
	public List<Provider> getTripDeals(User user) throws UserNotFoundException {
		logger.info("** Processing to get trip deals. User: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		logger.info("** cumulativePoint: "+cumulativeRewardPoints);

		List<Provider> providers = tripPricer.getPrice(internalTestHelper.getTripPricerApiKey(), user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);

		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) throws UserNotFoundException {
		logger.info("** Processing to track user location. User: "+user.getUserName());
		Locale.setDefault(new Locale("en", "US"));

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);

		return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		logger.info("** Processing to get nearby attractions. User: "+visitedLocation.userId);

		List<Attraction> nearbyAttractions = new ArrayList<>();

		gpsUtil.getAttractions().forEach(attraction -> {
			logger.debug("Attraction: "+attraction.attractionName+" Distance: "+ DistanceCalculator.getDistance(attraction,visitedLocation.location));
			if(DistanceCalculator.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				logger.info("Attraction nearby found");
				nearbyAttractions.add(attraction);
			}
		});

		return nearbyAttractions;
	}
	
	private void addShutDownHook() {
		logger.info("** Processing to add shut down hook");

		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	

	
}
