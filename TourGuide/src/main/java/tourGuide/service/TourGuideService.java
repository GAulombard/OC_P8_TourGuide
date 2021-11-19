package tourGuide.service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.tourguide.commons.model.VisitedLocation;
import com.tourguide.commons.model.Attraction;
import com.tourguide.commons.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tourGuide.exception.UserAlreadyExistsException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.exception.UsersGatheringException;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.NearbyAttraction;
import tourGuide.model.UserPreferences;
import tourGuide.service.feign.GpsUtilFeign;
import tourGuide.service.feign.RewardCentralFeign;
import tourGuide.service.feign.TripPricerFeign;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.util.DistanceCalculator;
import com.tourguide.commons.model.Provider;


/**
 * The type Tour guide service.
 */
@Service
public class TourGuideService {

	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);

	//Libs
	@Autowired
	private GpsUtilFeign gpsUtilFeign;
	@Autowired
	private RewardCentralFeign rewardCentralFeign;
	@Autowired
	private TripPricerFeign tripPricerFeign;

	private final RewardsService rewardsService;
	/**
	 * The Tracker.
	 */
	public final Tracker tracker;
	private final InternalTestHelper internalTestHelper = new InternalTestHelper();


	/**
	 * Instantiates a new Tour guide service.
	 *
	 * @param rewardsService the rewards service
	 * @param testMode the test mode
	 * @param runTrackerAtStartup the run tracker at startup
	 */
	@Autowired
	public TourGuideService(RewardsService rewardsService, @Value("true") boolean testMode,@Value("true") boolean runTrackerAtStartup) {

		this.rewardsService = rewardsService;

		logger.info("TestMode: {}",testMode);

		if(testMode) {
			logger.debug("Initializing users");
			internalTestHelper.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this, runTrackerAtStartup);
		addShutDownHook();
	}

	/**
	 * Gets user.
	 *
	 * @param userName the user name
	 * @return the user
	 * @throws UserNotFoundException the user not found exception
	 */
	public User getUser(String userName) throws UserNotFoundException {
		logger.info("** Processing to get user by username");

		if(!internalTestHelper.getInternalUserMap().containsKey(userName)) throw new UserNotFoundException("User not found");

		return internalTestHelper.getInternalUserMap().get(userName);
	}

	/**
	 * Gets all users.
	 *
	 * @return the all users
	 * @throws UsersGatheringException the users gathering exception
	 */
	public List<User> getAllUsers() throws UsersGatheringException {
		logger.info("** Processing to get all users");

		List<User> users = new ArrayList<>();

		try {
			users = internalTestHelper.getInternalUserMap().values().stream().collect(Collectors.toList());
		}catch (Exception e) {
			logger.error("ERROR: Impossible to get all users");
			throw new UsersGatheringException(e.getMessage());
		}
		return users;
	}

	/**
	 * Gets user's last visited location.
	 *if last visited location is empty, ask GpsUtil api to get a location update.
	 *
	 * @param user the user
	 * @return the user location
	 * @throws UserNotFoundException the user not found exception
	 */
	public VisitedLocation getUserLocation(User user) throws UserNotFoundException {
		logger.info("** Processing to get user location. User: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation() : trackUserLocation(user);

		return visitedLocation;
	}

	/**
	 * Add user.
	 *
	 * @param user the user
	 * @throws UserAlreadyExistsException the user already exists exception
	 * @throws UserNotFoundException      the user not found exception
	 */

	public void addUser(User user) throws UserAlreadyExistsException, UserNotFoundException {
		logger.info("** Processing to add new user: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) {
			internalTestHelper.getInternalUserMap().put(user.getUserName(), user);
			trackUserLocation(user);
		} else {
			throw new UserAlreadyExistsException("ERROR: User already exists");
		}
	}

	/**
	 * Gets trip deals.
	 * Calculate the sum of rewards points, and for each attraction within the range user preference
	 * return a corresponding trip deal if this one exists (requiring user preferences).
	 *
	 * @param user the user
	 * @return the trip deals
	 * @throws UserNotFoundException the user not found exception
	 */
	public List<Provider> getTripDeals(User user) throws UserNotFoundException {
		logger.info("** Processing to get trip deals. User: "+user.getUserName());
		List<Provider> providers = new ArrayList<>();


		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();

		List<Attraction> attractionsWithinRange = getAttractionsWithinRangePreferences(gpsUtilFeign.getAttractions(),user);

		attractionsWithinRange.forEach(attraction -> {

			List<Provider> tempProviders = new ArrayList<>();

			tempProviders = tripPricerFeign.getPrice(internalTestHelper.getTripPricerApiKey(), attraction.attractionId, user.getUserPreferences().getNumberOfAdults(),
					user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);

			tempProviders.forEach(provider -> {
				if(provider.price <= user.getUserPreferences().getHighPricePoint().getNumber().doubleValueExact()) {
					logger.info("** --> Provider found: "+provider.name+" / Price: "+provider.price+" USD / Attraction: "+attraction.attractionName);
					providers.add(provider);
				}
			});
		});

		user.setTripDeals(providers);

		return providers;
	}

	/**
	 * Gets attractions within range preferences.
	 *
	 * @param attractions the attractions
	 * @param user        the user
	 * @return the attractions within range preferences
	 * @throws UserNotFoundException the user not found exception
	 */
	public List<Attraction> getAttractionsWithinRangePreferences(List <Attraction> attractions, User user) throws UserNotFoundException {
		List<Attraction> attractionsWithinRange = new ArrayList<>();

		Location userLocation = trackUserLocation(user).location;

		attractions.forEach(attraction -> {
			if(DistanceCalculator.getDistance(attraction,userLocation) <= user.getUserPreferences().getAttractionProximity()) {
				attractionsWithinRange.add(attraction);
			}
		});

		return attractionsWithinRange;
	}

	/**
	 * Track user location by calling the GpsUtil Api.
	 * The api provide a random coordinates in WGS84 decimal format.
	 *
	 *
	 * @param user the user
	 * @return the visited location
	 * @throws UserNotFoundException the user not found exception
	 */
	public VisitedLocation trackUserLocation(User user) throws UserNotFoundException {

		logger.info("** Processing to track user location. User: "+user.getUserName());
		Locale.setDefault(new Locale("en", "US"));

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = gpsUtilFeign.getUserLocation(user.getUserId()); //return random WGS84 position
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user); // calculate new reward

		return visitedLocation;
	}


	/**
	 * Track all users' location by calling the GpsUtil api.
	 * The api provide a random coordinates in WGS884 decimal format for all users.
	 *
	 * @param userList the user list
	 */
	public void trackUserLocationMultiThread(List<User> userList) {

		logger.info("** Multithreading ** Processing to track all user location.");
		//requesting a pool of n Threads
		ExecutorService executorService = Executors.newFixedThreadPool(200);//get an instance of the n threads

		List<Future<?>> listFuture = new ArrayList<>();

		for(User user: userList) {
			//submit multiple callable instances to the pool, using lambda
			Future<?> future = executorService.submit( () -> {

				VisitedLocation visitedLocation = gpsUtilFeign.getUserLocation(user.getUserId());//return random WGS84 position
				user.addToVisitedLocations(visitedLocation);
			});
			listFuture.add(future);
		}

		listFuture.stream().forEach(futureResult->{
			try {
				//call get() to see the result returned by the callable lambda used before
				futureResult.get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e.getMessage());

			}
		});

		rewardsService.calculateRewardsMultiThread(userList);

	}

	/**
	 * Gets nearby attractions.
	 * Always return the 5 closest attraction from the user no matter how far he is.
	 *
	 * @param visitedLocation the visited location
	 * @param user            the user
	 * @return the nearby attractions
	 * @throws UserNotFoundException the user not found exception
	 */

	public List<NearbyAttraction> getNearByAttractions(VisitedLocation visitedLocation,User user) throws UserNotFoundException {
		logger.info("** Processing to get nearby attractions.");
		logger.info("** -->User: "+user.getUserName());
		logger.info("** -->Latitude: "+visitedLocation.location.latitude+" Longitude: "+visitedLocation.location.longitude);

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		List<NearbyAttraction> nearbyAttractions = new ArrayList<>();
		List<Attraction> attractions = gpsUtilFeign.getAttractions();

		attractions.forEach(attraction -> {
			NearbyAttraction nearbyAttraction = new NearbyAttraction();
			nearbyAttraction.setAttractionName(attraction.attractionName);
			nearbyAttraction.setAttractionLatitude(attraction.latitude);
			nearbyAttraction.setAttractionLongitude(attraction.longitude);
			nearbyAttraction.setUserLatitude(visitedLocation.location.latitude);
			nearbyAttraction.setUserLongitude(visitedLocation.location.longitude);
			nearbyAttraction.setDistanceBetweenUserAndAttraction(DistanceCalculator.getDistance(attraction,visitedLocation.location));
			nearbyAttraction.setRewardPoint(rewardCentralFeign.getAttractionRewardPoints(attraction.attractionId,user.getUserId()));
			nearbyAttractions.add(nearbyAttraction);
		});

		Collections.sort(nearbyAttractions,Comparator.comparingDouble(NearbyAttraction::getDistanceBetweenUserAndAttraction));

		// Get the closest five tourist attractions to the user - no matter how far away they are.
		nearbyAttractions.subList(5,nearbyAttractions.size()).clear();

		return nearbyAttractions;
	}

	/**
	 * Gets all most recent location.
	 *
	 * @return the all current location
	 * @throws UsersGatheringException the users gathering exception
	 */
	public Map<String, Location> getAllCurrentLocation() throws UsersGatheringException {
		logger.info("** Processing to get all user's current location");
		Map<String,Location> allCurrentLocations = new HashMap<>();
		List<User> users = getAllUsers();

		users.forEach(user -> {
			try {
				allCurrentLocations.put(user.getUserId().toString(),getUserLocation(user).getLocation());
			} catch (UserNotFoundException e) {
				e.printStackTrace();
			}
		});

		return allCurrentLocations;
	}

	/**
	 * Update preferences.
	 *
	 * @param user           the user
	 * @param newPreferences the new preferences
	 * @throws UserNotFoundException the user not found exception
	 */
	public void updatePreferences(User user, UserPreferences newPreferences) throws UserNotFoundException {
		logger.info("** Processing to update preferences");

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		UserPreferences actualPreferences = user.getUserPreferences();

		actualPreferences.setNumberOfAdults(newPreferences.getNumberOfAdults());
		actualPreferences.setNumberOfChildren(newPreferences.getNumberOfChildren());
		actualPreferences.setAttractionProximity(newPreferences.getAttractionProximity());
		actualPreferences.setHighPricePoint(newPreferences.getHighPricePoint().getNumber().intValue());
		actualPreferences.setLowerPricePoint(newPreferences.getLowerPricePoint().getNumber().intValue());
		actualPreferences.setTicketQuantity(newPreferences.getTicketQuantity());
		actualPreferences.setTripDuration(newPreferences.getTripDuration());

	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}



}
