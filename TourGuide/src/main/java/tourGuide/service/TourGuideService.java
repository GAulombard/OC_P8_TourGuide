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
	public final Tracker tracker;
	private final InternalTestHelper internalTestHelper = new InternalTestHelper();
	boolean testMode = true;


	
	public TourGuideService(RewardsService rewardsService) {
		//this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalTestHelper.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	

	public User getUser(String userName) throws UserNotFoundException {
		logger.info("** Processing to get user by username");

		if(!internalTestHelper.getInternalUserMap().containsKey(userName)) throw new UserNotFoundException("User not found");

		return internalTestHelper.getInternalUserMap().get(userName);
	}
	
	public List<User> getAllUsers() throws UsersGatheringException {
		logger.info("** Processing to get all users");

		List<User> users = new ArrayList<>();

		try {
			users = internalTestHelper.getInternalUserMap().values().stream().collect(Collectors.toList());
		}catch (Exception e) { //todo: find a way to test this
			logger.error("ERROR: Impossible to get all users");
			throw new UsersGatheringException(e.getMessage());
		}
		return users;
	}

	public VisitedLocation getUserLocation(User user) throws UserNotFoundException {
		logger.info("** Processing to get user location. User: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation() : trackUserLocation(user);

		return visitedLocation;
	}

	//todo:add endpoint for that ?
	public void addUser(User user) throws UserAlreadyExistsException, UserNotFoundException {
		logger.info("** Processing to add new user: "+user.getUserName());

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) {
			internalTestHelper.getInternalUserMap().put(user.getUserName(), user);
			trackUserLocation(user);
		} else {
			throw new UserAlreadyExistsException("ERROR: User already exists");
		}
	}

	public List<Provider> getTripDeals(User user) throws UserNotFoundException {
		logger.info("** Processing to get trip deals. User: "+user.getUserName());
		List<Provider> providers = new ArrayList<>();


		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		//les complex than foreach
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

	public VisitedLocation trackUserLocation(User user) throws UserNotFoundException {
		logger.info("** Processing to track user location. User: "+user.getUserName());
		Locale.setDefault(new Locale("en", "US"));

		if(!internalTestHelper.getInternalUserMap().containsKey(user.getUserName())) throw new UserNotFoundException("User not found");

		VisitedLocation visitedLocation = gpsUtilFeign.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);

		return visitedLocation;
	}

	public void trackUserLocationMultiThread(List<User> userList) {

		logger.info("** Multithread ** Processing to track all user location.");
		ExecutorService executorService = Executors.newFixedThreadPool(50);

		List<Future<?>> listFuture = new ArrayList<>();

		for(User user: userList) {
			Future<?> future = executorService.submit( () -> {

				VisitedLocation visitedLocation = gpsUtilFeign.getUserLocation(user.getUserId());;
				user.addToVisitedLocations(visitedLocation);
			});
			listFuture.add(future);
		}

		listFuture.stream().forEach(f->{
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e.getMessage());

			}
		});

		rewardsService.calculateRewardsMultiThread(userList);

	}

	// Get the closest five tourist attractions to the user - no matter how far away they are.
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
		nearbyAttractions.subList(5,nearbyAttractions.size()).clear();

		return nearbyAttractions;
	}

	public Map<String, Location> getAllCurrentLocation() throws UsersGatheringException {
		logger.info("** Processing to get all user's current location");
		Map<String,Location> allCurrentLocations = new HashMap<>();
		List<User> users = getAllUsers();

		users.forEach(user -> {
			allCurrentLocations.put(user.getUserId().toString(),user.getLastVisitedLocation().location);
		});

		return allCurrentLocations;
	}

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
